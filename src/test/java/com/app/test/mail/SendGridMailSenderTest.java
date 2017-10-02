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

import com.app.mail.SendGridMailSender;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;

import com.app.util.ConstantsUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;
import com.sendgrid.Content;
import com.sendgrid.Mail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sendgrid.SendGrid;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import javax.mail.Transport;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class SendGridMailSenderTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		setUpProperties();

		_clazz = Class.forName(SendGridMailSender.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@Test
	public void testSendAccountDeletionMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"sendAccountDeletionMessage", String.class);

		sendAccountDeletionMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendCancellationMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCancellationMessage = _clazz.getDeclaredMethod(
			"sendCancellationMessage", String.class);

		sendCancellationMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendCardDetailsMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCardDetailsMessage = _clazz.getDeclaredMethod(
			"sendCardDetailsMessage", String.class);

		sendCardDetailsMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendContactMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendContactMessage = _clazz.getDeclaredMethod(
			"sendContactMessage", String.class, String.class);

		sendContactMessage.invoke(
			_classInstance, "test@test.com", "Contact Message");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendEmail() throws Exception {
		SendGrid sendGrid = Mockito.mock(SendGrid.class);

		Mockito.doReturn(
			null
		).when(
			sendGrid
		).api(
			Mockito.anyObject()
		);

		PowerMockito.spy(SendGrid.class);

		PowerMockito.whenNew(
			SendGrid.class
		).withArguments(
			Mockito.anyString()
		).thenReturn(
			sendGrid
		);

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"_sendEmail", Mail.class);

		sendAccountDeletionMessage.setAccessible(true);

		Mail mail = new Mail();

		sendAccountDeletionMessage.invoke(_classInstance, mail);

		Mockito.verify(
			sendGrid, Mockito.times(1)
		).api(
			Mockito.anyObject()
		);
	}

	@Test
	public void testSendEmailWithException() throws Exception {
		SendGrid sendGrid = Mockito.mock(SendGrid.class);

		Mockito.doReturn(
			null
		).when(
			sendGrid
		).api(
			Mockito.anyObject()
		);

		PowerMockito.spy(SendGrid.class);

		PowerMockito.whenNew(
			SendGrid.class
		).withArguments(
			Mockito.anyString()
		).thenReturn(
			sendGrid
		);

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"_sendEmail", Mail.class);

		sendAccountDeletionMessage.setAccessible(true);

		Mail mail = null;

		sendAccountDeletionMessage.invoke(_classInstance, mail);

		Mockito.verify(
			sendGrid, Mockito.never()
		).api(
			Mockito.anyObject()
		);
	}

	@Test
	public void testSendPasswordResetToken() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPasswordResetToken = _clazz.getDeclaredMethod(
			"sendPasswordResetToken", String.class, String.class);

		sendPasswordResetToken.invoke(
			_classInstance, "test@test.com", "passwordResetToken");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendPaymentFailedMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPaymentFailedMessage = _clazz.getDeclaredMethod(
			"sendPaymentFailedMessage", String.class);

		sendPaymentFailedMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendResubscribeMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendResubscribeMessage = _clazz.getDeclaredMethod(
			"sendResubscribeMessage", String.class);

		sendResubscribeMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(1);
	}

	@Test
	public void testSendSearchResultsToRecipient() throws Exception {
		setUpDatabase();
		setUpUserUtil();
		setUpSendGridMailSender();

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

		_assertSendGridCalled(1);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(1, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientExceedingEmailLimit()
		throws Exception {

		setUpDatabase();
		setUpUserUtil();
		setUpSendGridMailSender();

		ConstantsUtil.init();

		_initializeVelocityTemplate(_clazz, _classInstance);

		UserUtil.addUser("test@test.com", "password");//

		UserUtil.updateUserDetails(
			"test@test.com", "", "", "http://www.ebay.com/itm/", false);

		Method sendSearchResultsToRecipient = _clazz.getDeclaredMethod(
			"sendSearchResultsToRecipient", int.class, Map.class);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		sendSearchResultsToRecipient.invoke(
			_classInstance, 1, searchQueryResultMap);

		_assertSendGridCalled(0);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(0, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientWithoutEmailNotifications()
		throws Exception {

		setUpDatabase();
		setUpUserUtil();
		setUpSendGridMailSender();

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

		_assertSendGridCalled(0);

		User user = UserUtil.getUserByUserId(1);

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_EMAILS_PER_DAY + 1,
			user.getEmailsSent());
	}

	@Test
	public void testSendWelcomeMessage() throws Exception {
		setUpSendGridMailSender();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendWelcomeMessage = _clazz.getDeclaredMethod(
			"sendWelcomeMessage", String.class);

		sendWelcomeMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(1);
	}

	@Test
	public void testPopulateAccountDeletionMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "user@test.com", "Account Deletion Successful",
				"account_deletion_email.vm");

		Assert.assertEquals(
			"Account Deletion Successful", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_ACCOUNT_DELETION_EMAIL, content.getValue());
	}

	@Test
	public void testPopulateCancellationMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "user@test.com", "Cancellation Successful",
				"cancellation_email.vm");

		Assert.assertEquals(
			"Cancellation Successful", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_CANCELLATION_EMAIL, content.getValue());
	}

	@Test
	public void testPopulateCardDetailsMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "user@test.com", "Card Details Updated",
				"card_details_email.vm");

		Assert.assertEquals(
			"Card Details Updated", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_CARD_DETAILS_EMAIL, content.getValue());
	}

	@Test
	public void testPopulateContactMessage() throws Exception {
		Method populateContactMessageMethod = _clazz.getDeclaredMethod(
			"_populateContactMessage", String.class, String.class);

		populateContactMessageMethod.setAccessible(true);

		Mail mail =
			(Mail)populateContactMessageMethod.invoke(
				_classInstance, "user@test.com", "Sample contact message");

		Assert.assertEquals(
			"You Have A New Message From user@test.com", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals("Sample contact message", content.getValue());
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateEmailMessageMethod = _clazz.getDeclaredMethod(
			"_populateEmailMessage", Map.class,	String.class, String.class);

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

		Mail mail =
			(Mail)populateEmailMessageMethod.invoke(
				_classInstance, searchQueryResultMap, "user@test.com",
				"unsubscribeToken");

		Assert.assertTrue(
			mail.getSubject().contains("New Search Results - "));

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_EMAIL_CONTENT, content.getValue());
	}

	@Test
	public void testPopulatePaymentFailedMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "user@test.com", "Payment Failed",
				"payment_failed_email.vm");

		Assert.assertEquals(
			"Payment Failed", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_PAYMENT_FAILED_EMAIL, content.getValue());
	}

	@Test
	public void testPopulatePasswordResetToken() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populatePasswordResetToken = _clazz.getDeclaredMethod(
			"_populatePasswordResetToken", String.class, String.class);

		populatePasswordResetToken.setAccessible(true);

		Mail mail =
			(Mail)populatePasswordResetToken.invoke(
				_classInstance, "user@test.com", "Sample password reset token");

		Assert.assertEquals(
			"Password Reset Token", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_PASSWORD_RESET_TOKEN_EMAIL, content.getValue());
	}

	@Test
	public void testPopulateResubscribeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "user@test.com", "Resubscribe Successful",
				"resubscribe_email.vm");

		Assert.assertEquals(
			"Resubscribe Successful", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_RESUBSCRIBE_EMAIL, content.getValue());
	}

	@Test
	public void testPopulateWelcomeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail)populateMessageMethod.invoke(
				_classInstance, "user@test.com", "Welcome", "welcome_email.vm");

		Assert.assertEquals(
			"Welcome", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_WELCOME_EMAIL, content.getValue());
	}

	private static void _assertSendGridCalled(int times)
		throws Exception {

		if (times == 0) {
			PowerMockito.verifyPrivate(
				SendGridMailSender.class, Mockito.never()).invoke(
					"_sendEmail", Mockito.anyObject());
		}
		else if (times == 1) {
			PowerMockito.verifyPrivate(
				SendGridMailSender.class, Mockito.times(1)).invoke(
					"_sendEmail", Mockito.anyObject());
		}
	}

	private static Object _classInstance;
	private static Class _clazz;

}