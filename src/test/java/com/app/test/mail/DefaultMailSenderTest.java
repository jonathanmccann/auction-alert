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
import com.app.mail.MailSender;
import com.app.mail.MailSenderFactory;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import com.app.util.ConstantsUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultMailSenderTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		setUpProperties();

		_clazz = Class.forName(DefaultMailSender.class.getName());

		_classInstance = _clazz.newInstance();

		Method _authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("_authenticateOutboundEmailAddress");

		_authenticateOutboundEmailAddressMethod.setAccessible(true);

		_session =
			(Session)_authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);
	}

	@Test
	public void testPopulateAccountDeletionMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessage = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class,
			Session.class);

		populateMessage.setAccessible(true);

		Message message = (Message)populateMessage.invoke(
			_classInstance, "user@test.com", "Account Deletion Successful",
			"account_deletion_email.vm", _session);

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
	public void testPopulateCancellationMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessage = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class,
			Session.class);

		populateMessage.setAccessible(true);

		Message message = (Message)populateMessage.invoke(
			_classInstance, "user@test.com", "Cancellation Successful",
			"cancellation_email.vm", _session);

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
	public void testPopulateCardDetailsMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessage = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class,
			Session.class);

		populateMessage.setAccessible(true);

		Message message = (Message)populateMessage.invoke(
			_classInstance, "user@test.com", "Card Details Updated",
			"card_details_email.vm", _session);

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
	public void testPopulateContactMessage() throws Exception {
		Method populateContactMessage = _clazz.getDeclaredMethod(
			"_populateContactMessage", String.class, String.class,
			Session.class);

		populateContactMessage.setAccessible(true);

		Message message = (Message)populateContactMessage.invoke(
			_classInstance, "user@test.com", "Sample contact message",
			_session);

		Assert.assertEquals("user@test.com", message.getFrom()[0].toString());
		Assert.assertEquals(
			"You Have A New Message From user@test.com", message.getSubject());
		Assert.assertEquals("Sample contact message", message.getContent());
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateEmailMessageMethod = _clazz.getDeclaredMethod(
			"_populateEmailMessage", Map.class, String.class, String.class,
			Session.class);

		populateEmailMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "Test keywords");

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", "$14.99", "$29.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		searchQueryResultMap.put(searchQuery, searchResults);

		Message message = (Message)populateEmailMessageMethod.invoke(
			_classInstance, searchQueryResultMap, "user@test.com",
			"unsubscribeToken", _session);

		Assert.assertEquals(
			"Auction Alert <test@test.com>", message.getFrom()[0].toString());
		Assert.assertTrue(
			message.getSubject().contains("New Search Results - "));
		Assert.assertEquals(_EMAIL_CONTENT, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("user@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testPopulatePaymentFailedMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessage = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class,
			Session.class);

		populateMessage.setAccessible(true);

		Message message = (Message)populateMessage.invoke(
			_classInstance, "user@test.com", "Payment Failed",
			"payment_failed_email.vm", _session);

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
	public void testPopulatePasswordResetToken() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populatePasswordResetToken = _clazz.getDeclaredMethod(
			"_populatePasswordResetToken", String.class, String.class,
			Session.class);

		populatePasswordResetToken.setAccessible(true);

		Message message = (Message)populatePasswordResetToken.invoke(
			_classInstance, "user@test.com", "Sample password reset token",
			_session);

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
	public void testPopulateResubscribeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessage = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class,
			Session.class);

		populateMessage.setAccessible(true);

		Message message = (Message)populateMessage.invoke(
			_classInstance, "user@test.com", "Resubscribe Successful",
			"resubscribe_email.vm", _session);

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
	public void testPopulateWelcomeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessage = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class,
			Session.class);

		populateMessage.setAccessible(true);

		Message message = (Message)populateMessage.invoke(
			_classInstance, "user@test.com", "Welcome", "welcome_email.vm",
			_session);

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
	public void testSendAccountDeletionMessage() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"sendAccountDeletionMessage", String.class);

		sendAccountDeletionMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendAccountDeletionMessageWithException() throws Exception {
		setUpTransport();

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"sendAccountDeletionMessage", String.class);

		sendAccountDeletionMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(0);
	}

	@Test
	public void testSendCancellationMessage() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCancellationMessage = _clazz.getDeclaredMethod(
			"sendCancellationMessage", String.class);

		sendCancellationMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendCancellationMessageWithException() throws Exception {
		setUpTransport();

		Method sendCancellationMessage = _clazz.getDeclaredMethod(
			"sendCancellationMessage", String.class);

		sendCancellationMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(0);
	}

	@Test
	public void testSendCardDetailsMessage() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCardDetailsMessage = _clazz.getDeclaredMethod(
			"sendCardDetailsMessage", String.class);

		sendCardDetailsMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendCardDetailsMessageWithException() throws Exception {
		setUpTransport();

		Method sendCardDetailsMessage = _clazz.getDeclaredMethod(
			"sendCardDetailsMessage", String.class);

		sendCardDetailsMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(0);
	}

	@Test
	public void testSendContactMessage() throws Exception {
		setUpTransport();

		Method sendContactMessage = _clazz.getDeclaredMethod(
			"sendContactMessage", String.class, String.class);

		sendContactMessage.invoke(
			_classInstance, "test@test.com", "Contact Message");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendPasswordResetToken() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPasswordResetToken = _clazz.getDeclaredMethod(
			"sendPasswordResetToken", String.class, String.class);

		sendPasswordResetToken.invoke(
			_classInstance, "test@test.com", "passwordResetToken");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendPasswordResetTokenWithException() throws Exception {
		setUpTransport();

		Method sendPasswordResetToken = _clazz.getDeclaredMethod(
			"sendPasswordResetToken", String.class, String.class);

		sendPasswordResetToken.invoke(
			_classInstance, "test@test.com", "passwordResetToken");

		_assertTransportCalled(0);
	}

	@Test
	public void testSendPaymentFailedMessage() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPaymentFailedMessage = _clazz.getDeclaredMethod(
			"sendPaymentFailedMessage", String.class);

		sendPaymentFailedMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendPaymentFailedMessageWithException() throws Exception {
		setUpTransport();

		Method sendPaymentFailedMessage = _clazz.getDeclaredMethod(
			"sendPaymentFailedMessage", String.class);

		sendPaymentFailedMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(0);
	}

	@Test
	public void testSendResubscribeMessage() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendResubscribeMessage = _clazz.getDeclaredMethod(
			"sendResubscribeMessage", String.class);

		sendResubscribeMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendResubscribeMessageWithException() throws Exception {
		setUpTransport();

		Method sendResubscribeMessage = _clazz.getDeclaredMethod(
			"sendResubscribeMessage", String.class);

		sendResubscribeMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(0);
	}

	@Test
	public void testSendSearchResultsToRecipient()
		throws Exception {

		setUpDatabase();
		setUpUserUtil();
		setUpTransport();

		ConstantsUtil.init();

		_initializeVelocityTemplate(_clazz, _classInstance);

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			1, "unsubscribeToken", "customerId", "subscriptionId", true, false);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, 1, searchQueryResultMap);

		_assertTransportCalled(1);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(1, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientExceedingEmailLimit()
		throws Exception {

		setUpDatabase();
		setUpUserUtil();
		setUpTransport();

		ConstantsUtil.init();

		_initializeVelocityTemplate(_clazz, _classInstance);

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateEmailsSent(
			1, PropertiesValues.NUMBER_OF_EMAILS_PER_DAY + 1);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, 1, searchQueryResultMap);

		_assertTransportCalled(0);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_EMAILS_PER_DAY + 1,
			user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientWithException()
		throws Exception {

		setUpDatabase();
		setUpUserUtil();
		setUpTransport();

		ConstantsUtil.init();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			1, "unsubscribeToken", "customerId", "subscriptionId", true, false);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, 1, searchQueryResultMap);

		_assertTransportCalled(0);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(0, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientWithoutEmailNotifications()
		throws Exception {

		setUpDatabase();
		setUpUserUtil();
		setUpTransport();

		ConstantsUtil.init();

		_initializeVelocityTemplate(_clazz, _classInstance);

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserDetails(
			"test@test.com", "", "", "http://www.ebay.com/itm/", false);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, 1, searchQueryResultMap);

		_assertTransportCalled(0);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(0, user.getEmailsSent());
	}

	@Test
	public void testSendWelcomeMessage() throws Exception {
		setUpTransport();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendWelcomeMessage = _clazz.getDeclaredMethod(
			"sendWelcomeMessage", String.class);

		sendWelcomeMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(1);
	}

	@Test
	public void testSendWelcomeMessageWithException() throws Exception {
		setUpTransport();

		Method sendWelcomeMessage = _clazz.getDeclaredMethod(
			"sendWelcomeMessage", String.class);

		sendWelcomeMessage.invoke(_classInstance, "test@test.com");

		_assertTransportCalled(0);
	}

	private static void _assertTransportCalled(int times)
		throws Exception {

		if (times == 0) {
			PowerMockito.verifyStatic(Mockito.never());
		}
		else if (times == 1) {
			PowerMockito.verifyStatic(Mockito.times(1));
		}

		Transport.send(Mockito.anyObject());
	}

	private static Object _classInstance;
	private static Class _clazz;
	private static Session _session;

}