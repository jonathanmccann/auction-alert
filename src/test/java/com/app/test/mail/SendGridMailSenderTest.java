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
import com.sendgrid.Email;
import com.sendgrid.Mail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sendgrid.Personalization;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest(HttpClients.class)
@RunWith(PowerMockRunner.class)
@WebAppConfiguration
public class SendGridMailSenderTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		setUpProperties();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		_clazz = Class.forName(SendGridMailSender.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByUserId(_userId);
	}

	@Test
	public void testAddSearchResultIdsToMail() throws Exception {
		Method addSearchResultIdsToMailMethod = _clazz.getDeclaredMethod(
			"_addSearchResultIdsToMail", Mail.class, Map.class);

		addSearchResultIdsToMailMethod.setAccessible(true);

		Email emailFrom = new Email("from@test.com");
		Email emailTo = new Email("to@test.com");

		Content content = new Content("text/html", "Content");

		Mail mail = new Mail(emailFrom, "Subject", emailTo, content);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		SearchQuery searchQuery = new SearchQuery();

		SearchResult firstSearchResult = new SearchResult();

		firstSearchResult.setSearchResultId(1);

		SearchResult secondSearchResult = new SearchResult();

		secondSearchResult.setSearchResultId(2);

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(firstSearchResult);
		searchResults.add(secondSearchResult);

		searchQueryResultMap.put(searchQuery, searchResults);

		addSearchResultIdsToMailMethod.invoke(
			_classInstance, mail, searchQueryResultMap);

		Personalization personalization = mail.getPersonalization().get(0);

		Map<String, String> customArgs = personalization.getCustomArgs();

		Assert.assertEquals(1, customArgs.size());
		Assert.assertEquals("1,2", customArgs.get("searchResultIds"));
	}

	@Test
	public void testSendAccountDeletionMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendAccountDeletionMessage = _clazz.getDeclaredMethod(
			"sendAccountDeletionMessage", String.class);

		sendAccountDeletionMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendCancellationMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCancellationMessage = _clazz.getDeclaredMethod(
			"sendCancellationMessage", String.class);

		sendCancellationMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendCardDetailsMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendCardDetailsMessage = _clazz.getDeclaredMethod(
			"sendCardDetailsMessage", String.class);

		sendCardDetailsMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendContactMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		Method sendContactMessage = _clazz.getDeclaredMethod(
			"sendContactMessage", String.class, String.class);

		sendContactMessage.invoke(
			_classInstance, "test@test.com", "Contact Message");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendContactMessageWithException() throws Exception {
		Method sendContactMessage = _clazz.getDeclaredMethod(
			"sendContactMessage", String.class, String.class);

		try {
			sendContactMessage.invoke(
				_classInstance, "test@test.com", "Contact Message");
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof HttpResponseException);
		}
	}

	@Test
	public void testSendEmail() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		Method sendEmail = _clazz.getDeclaredMethod("_sendEmail", Mail.class);

		sendEmail.setAccessible(true);

		Mail mail = new Mail();

		sendEmail.invoke(_classInstance, mail);

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendEmailWithException() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		Method sendEmail = _clazz.getDeclaredMethod("_sendEmail", Mail.class);

		sendEmail.setAccessible(true);

		Mail mail = null;

		sendEmail.invoke(_classInstance, mail);

		_assertSendGridCalled(closeableHttpClient, 0);
	}

	@Test
	public void testSendPasswordResetToken() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPasswordResetToken = _clazz.getDeclaredMethod(
			"sendPasswordResetToken", String.class, String.class);

		sendPasswordResetToken.invoke(
			_classInstance, "test@test.com", "passwordResetToken");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendPaymentFailedMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendPaymentFailedMessage = _clazz.getDeclaredMethod(
			"sendPaymentFailedMessage", String.class);

		sendPaymentFailedMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendResubscribeMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendResubscribeMessage = _clazz.getDeclaredMethod(
			"sendResubscribeMessage", String.class);

		sendResubscribeMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(closeableHttpClient, 1);
	}

	@Test
	public void testSendSearchResultsToRecipient() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

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

		_assertSendGridCalled(closeableHttpClient, 1);

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(1, user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientExceedingEmailLimit()
		throws Exception {

		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

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

		_assertSendGridCalled(closeableHttpClient, 0);

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_EMAILS_PER_DAY + 1,
			user.getEmailsSent());
	}

	@Test
	public void testSendSearchResultsToRecipientWithoutEmailNotifications()
		throws Exception {

		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

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

		_assertSendGridCalled(closeableHttpClient, 0);

		user = UserUtil.getUserByUserId(_userId);

		Assert.assertEquals(0, user.getEmailsSent());
	}

	@Test
	public void testSendWelcomeMessage() throws Exception {
		CloseableHttpClient closeableHttpClient = _setUpSendGrid();

		_initializeVelocityTemplate(_clazz, _classInstance);

		Method sendWelcomeMessage = _clazz.getDeclaredMethod(
			"sendWelcomeMessage", String.class);

		sendWelcomeMessage.invoke(_classInstance, "test@test.com");

		_assertSendGridCalled(closeableHttpClient, 1);
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
			"_populateEmailMessage", Map.class,	String.class);

		populateEmailMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(1);
		searchResult.setItemId("1234");
		searchResult.setItemTitle("itemTitle");
		searchResult.setAuctionPrice("$14.99");
		searchResult.setFixedPrice("$29.99");
		searchResult.setItemURL("http://www.ebay.com/itm/1234");
		searchResult.setGalleryURL("http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		searchQueryResultMap.put(searchQuery, searchResults);

		Mail mail =
			(Mail)populateEmailMessageMethod.invoke(
				_classInstance, searchQueryResultMap, "user@test.com");

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

	private static void _assertSendGridCalled(
			CloseableHttpClient closeableHttpClient, int times)
		throws Exception {

		if (times == 0) {
			Mockito.verify(
				closeableHttpClient, Mockito.never()
			).execute(
				Mockito.anyObject()
			);
		}
		else {
			ArgumentCaptor<HttpUriRequest> argumentCaptor =
				ArgumentCaptor.forClass(HttpUriRequest.class);

			Mockito.verify(
				closeableHttpClient, Mockito.times(1)
			).execute(
				argumentCaptor.capture()
			);

			Assert.assertNotNull(argumentCaptor.getValue());
		}
	}

	private static CloseableHttpClient _setUpSendGrid() throws Exception {
		CloseableHttpClient closeableHttpClient = Mockito.mock(
			CloseableHttpClient.class);

		CloseableHttpResponse closeableHttpResponse = Mockito.mock(
			CloseableHttpResponse.class);

		BasicStatusLine basicStatusLine = Mockito.mock(BasicStatusLine.class);

		Mockito.when(
			basicStatusLine.getStatusCode()
		).thenReturn(
			200
		);

		Mockito.when(
			closeableHttpResponse.getStatusLine()
		).thenReturn(
			basicStatusLine
		);

		Mockito.when(
			closeableHttpResponse.getAllHeaders()
		).thenReturn(
			new Header[0]
		);

		Mockito.when(
			closeableHttpClient.execute(Mockito.anyObject())
		).thenReturn(
			closeableHttpResponse
		);

		PowerMockito.spy(HttpClients.class);

		PowerMockito.doReturn(
			closeableHttpClient
		).when(
			HttpClients.class, "createDefault"
		);

		return closeableHttpClient;
	}

	private static Object _classInstance;
	private static Class _clazz;
	private static int _userId;

}