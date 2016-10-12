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
import com.app.test.BaseTestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

/**
 * @author Jonathan McCann
 */
public class DefaultMailSenderTest extends BaseTestCase {

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
	public void testPopulateCancellationMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateCancellationMessage = _clazz.getDeclaredMethod(
			"_populateCancellationMessage", String.class, Session.class);

		populateCancellationMessage.setAccessible(true);

		Message message = (Message)populateCancellationMessage.invoke(
			_classInstance, "test@test.com", _session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Cancellation Successful", message.getSubject());
		Assert.assertEquals(_CANCELLATION_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("test@test.com");

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
			_classInstance, "test@test.com", "Sample contact message",
			_session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertEquals(
			"You Have A New Message From test@test.com", message.getSubject());
		Assert.assertEquals("Sample contact message", message.getContent());
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateEmailMessageMethod = _clazz.getDeclaredMethod(
			"_populateEmailMessage", Map.class, String.class, String.class,
			String.class, Session.class);

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

		Message message = (Message)populateEmailMessageMethod.invoke(
			_classInstance, searchQueryResultMap, "test@test.com",
			"test@test.com", "unsubscribeToken", _session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertTrue(
			message.getSubject().contains("New Search Results - "));
		Assert.assertEquals(_EMAIL_CONTENT, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("test@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testPopulateWelcomeMessage() throws Exception {
		_initializeVelocityTemplate(_clazz, _classInstance);

		Method populateWelcomeMessage = _clazz.getDeclaredMethod(
			"_populateWelcomeMessage", String.class, Session.class);

		populateWelcomeMessage.setAccessible(true);

		Message message = (Message)populateWelcomeMessage.invoke(
			_classInstance, "test@test.com", _session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertEquals(
			"Welcome", message.getSubject());
		Assert.assertEquals(_WELCOME_EMAIL, message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("test@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	private static Object _classInstance;
	private static Class _clazz;
	private static Session _session;

}