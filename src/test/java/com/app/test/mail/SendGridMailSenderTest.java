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
import com.app.test.BaseTestCase;

import com.sendgrid.Content;
import com.sendgrid.Mail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.ui.velocity.VelocityEngineFactoryBean;

/**
 * @author Jonathan McCann
 */
public class SendGridMailSenderTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpProperties();

		_clazz = Class.forName(SendGridMailSender.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@Test
	public void testPopulateCancellationMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "test@test.com", "Cancellation Successful",
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
				_classInstance, "test@test.com", "Card Details Updated",
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
				_classInstance, "test@test.com", "Sample contact message");

		Assert.assertEquals(
			"You Have A New Message From test@test.com", mail.getSubject());

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
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		searchQueryResultMap.put(searchQuery, searchResults);

		Mail mail =
			(Mail)populateEmailMessageMethod.invoke(
				_classInstance, searchQueryResultMap, "test@test.com",
				"unsubscribeToken");

		Assert.assertTrue(
			mail.getSubject().contains("New Search Results - "));

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_EMAIL_CONTENT, content.getValue());
	}

	@Test
	public void testPopulateResubscribeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateMessageMethod = _clazz.getDeclaredMethod(
			"_populateMessage", String.class, String.class, String.class);

		populateMessageMethod.setAccessible(true);

		Mail mail =
			(Mail) populateMessageMethod.invoke(
				_classInstance, "test@test.com", "Resubscribe Successful",
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
				_classInstance, "test@test.com", "Welcome", "welcome_email.vm");

		Assert.assertEquals(
			"Welcome", mail.getSubject());

		List<Content> mailContent = mail.getContent();

		Content content = mailContent.get(0);

		Assert.assertEquals("text/html", content.getType());
		Assert.assertEquals(_WELCOME_EMAIL, content.getValue());
	}

	private static Object _classInstance;
	private static Class _clazz;

}