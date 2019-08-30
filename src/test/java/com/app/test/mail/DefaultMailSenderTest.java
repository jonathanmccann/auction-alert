/**
 * Copyright (c) 2014-present Jonathan McCann
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package com.app.test.mail;

import com.app.mail.DefaultMailSender;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import com.app.util.ConstantsUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
public class DefaultMailSenderTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		setUpProperties();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		setUpTransport();

		_clazz = Class.forName(DefaultMailSender.class.getName());

		_classInstance = _clazz.newInstance();

		Method _authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("_authenticateOutboundEmailAddress");

		_authenticateOutboundEmailAddressMethod.setAccessible(true);

		_session =
			(Session)_authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByUserId(_userId);
	}

	@Test
	public void testSessionPasswordAuthentication() throws Exception {
		PasswordAuthentication passwordAuthentication =
			_session.requestPasswordAuthentication(null, 0, null, null, null);

		Assert.assertEquals(
			PropertiesValues.OUTBOUND_EMAIL_ADDRESS,
			passwordAuthentication.getUserName());

		Assert.assertEquals(
			PropertiesValues.OUTBOUND_EMAIL_ADDRESS_PASSWORD,
			passwordAuthentication.getPassword());
	}

	@Test
	public void testSendAccountDeletionMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"sendAccountDeletionMessage", String.class);

		sendAccountDeletionMessage.invoke(_classInstance, "user@test.com");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Account Deletion Successful", message.getSubject());
		Assert.assertEquals(_ACCOUNT_DELETION_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendAccountDeletionMessageWithException() throws Exception {
		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"sendAccountDeletionMessage", String.class);

		sendAccountDeletionMessage.invoke(_classInstance, "user@test.com");

		assertTransportCalled(0);
	}

	@Test
	public void testSendCancellationMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCancellationMessage = _clazz.getDeclaredMethod(
			"sendCancellationMessage", String.class);

		sendCancellationMessage.invoke(_classInstance, "user@test.com");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Cancellation Successful", message.getSubject());
		Assert.assertEquals(_CANCELLATION_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendCancellationMessageWithException() throws Exception {
		Method sendCancellationMessage = _clazz.getDeclaredMethod(
			"sendCancellationMessage", String.class);

		sendCancellationMessage.invoke(_classInstance, "user@test.com");

		assertTransportCalled(0);
	}

	@Test
	public void testSendCardDetailsMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCardDetailsMessage = _clazz.getDeclaredMethod(
			"sendCardDetailsMessage", String.class);

		sendCardDetailsMessage.invoke(_classInstance, "user@test.com");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Card Details Updated", message.getSubject());
		Assert.assertEquals(_CARD_DETAILS_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendCardDetailsMessageWithException() throws Exception {
		Method sendCardDetailsMessage = _clazz.getDeclaredMethod(
			"sendCardDetailsMessage", String.class);

		sendCardDetailsMessage.invoke(_classInstance, "user@test.com");

		assertTransportCalled(0);
	}

	@Test
	public void testSendContactMessage() throws Exception {
		Method sendContactMessage = _clazz.getDeclaredMethod(
			"sendContactMessage", String.class, String.class);

		sendContactMessage.invoke(
			_classInstance, "user@test.com", "Sample contact message");

		Message message = assertTransportCalled(1);

		Assert.assertEquals("user@test.com", message.getFrom()[0].toString());
		Assert.assertEquals(
			"You Have A New Message From user@test.com", message.getSubject());
		Assert.assertEquals("Sample contact message", message.getContent());
	}

	@Test
	public void testSendPasswordResetToken() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPasswordResetToken = _clazz.getDeclaredMethod(
			"sendPasswordResetToken", String.class, String.class);

		sendPasswordResetToken.invoke(
			_classInstance, "user@test.com", "Sample password reset token");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Password Reset Token", message.getSubject());
		Assert.assertEquals(_PASSWORD_RESET_TOKEN_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendPasswordResetTokenWithException() throws Exception {
		Method sendPasswordResetToken = _clazz.getDeclaredMethod(
			"sendPasswordResetToken", String.class, String.class);

		sendPasswordResetToken.invoke(
			_classInstance, "user@test.com", "passwordResetToken");

		assertTransportCalled(0);
	}

	@Test
	public void testSendPaymentFailedMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPaymentFailedMessage = _clazz.getDeclaredMethod(
			"sendPaymentFailedMessage", String.class);

		sendPaymentFailedMessage.invoke(_classInstance, "user@test.com");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Payment Failed", message.getSubject());
		Assert.assertEquals(_PAYMENT_FAILED_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendPaymentFailedMessageWithException() throws Exception {
		Method sendPaymentFailedMessage = _clazz.getDeclaredMethod(
			"sendPaymentFailedMessage", String.class);

		sendPaymentFailedMessage.invoke(_classInstance, "user@test.com");

		assertTransportCalled(0);
	}

	@Test
	public void testSendResubscribeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendResubscribeMessage = _clazz.getDeclaredMethod(
			"sendResubscribeMessage", String.class);

		sendResubscribeMessage.invoke(_classInstance, "user@test.com");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Resubscribe Successful", message.getSubject());
		Assert.assertEquals(_RESUBSCRIBE_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendResubscribeMessageWithException() throws Exception {
		Method sendResubscribeMessage = _clazz.getDeclaredMethod(
			"sendResubscribeMessage", String.class);

		sendResubscribeMessage.invoke(_classInstance, "user@test.com");

		assertTransportCalled(0);
	}

	@Test
	public void testSendSearchResultsToRecipient()
		throws Exception {

		_initializeVelocityTemplate(_clazz, _classInstance);

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		UserUtil.updateUserSubscription(
			_userId, "customerId", "subscriptionId", true, false);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		List<SearchResult> searchResults = new ArrayList<>();

		SearchQuery searchQuery = new SearchQuery(1, _userId, "Test keywords");

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", "$14.99", "$29.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		searchQueryResultMap.put(searchQuery, searchResults);

		sendSearchResultsToRecipient.invoke(
			_classInstance, _userId, searchQueryResultMap);

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertTrue(
			message.getSubject().contains("New Search Results - "));
		Assert.assertEquals(_EMAIL_CONTENT, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(1, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientExceedingEmailLimit()
		throws Exception {

		_initializeVelocityTemplate(_clazz, _classInstance);

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		UserUtil.updateEmailsSent(
			_userId, PropertiesValues.NUMBER_OF_EMAILS_PER_DAY + 1);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, _userId, searchQueryResultMap);

		assertTransportCalled(0);

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_EMAILS_PER_DAY + 1,
			user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientWithException()
		throws Exception {

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		UserUtil.updateUserSubscription(
			_userId, "customerId", "subscriptionId", true, false);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, _userId, searchQueryResultMap);

		assertTransportCalled(0);

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(0, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientWithoutEmailNotifications()
		throws Exception {

		_initializeVelocityTemplate(_clazz, _classInstance);

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		UserUtil.unsubscribeUserFromEmailNotifications("user@test.com");

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, _userId, searchQueryResultMap);

		assertTransportCalled(0);

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(0, user.getEmailsSent());
	}

	@Test
	public void testSendWelcomeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendWelcomeMessage = _clazz.getDeclaredMethod(
			"sendWelcomeMessage", String.class);

		sendWelcomeMessage.invoke(_classInstance, "user@test.com");

		Message message = assertTransportCalled(1);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Welcome", message.getSubject());
		Assert.assertEquals(_WELCOME_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSendWelcomeMessageWithException() throws Exception {
		Method sendWelcomeMessage = _clazz.getDeclaredMethod(
			"sendWelcomeMessage", String.class);

		sendWelcomeMessage.invoke(_classInstance, "test@test.com");

		assertTransportCalled(0);
	}

	private static Object _classInstance;
	private static Class _clazz;
	private static int _userId;
	private static Session _session;

}