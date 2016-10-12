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
		_initializeVelocityTemplate();

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
		_initializeVelocityTemplate();

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

	private void _initializeVelocityTemplate() throws Exception {
		Field velocityEngine = _clazz.getDeclaredField("velocityEngine");

		velocityEngine.setAccessible(true);

		VelocityEngineFactoryBean velocityEngineFactoryBean =
			new VelocityEngineFactoryBean();

		Map<String, Object> velocityPropertiesMap = new HashMap<>();

		velocityPropertiesMap.put("resource.loader", "class");
		velocityPropertiesMap.put(
			"class.resource.loader.class",
			"org.apache.velocity.runtime.resource.loader." +
				"ClasspathResourceLoader");

		velocityEngineFactoryBean.setVelocityPropertiesMap(
			velocityPropertiesMap);

		velocityEngine.set(
			_classInstance, velocityEngineFactoryBean.createVelocityEngine());
	}

	private static Object _classInstance;
	private static Class _clazz;
	private static Session _session;

	public static final String _EMAIL_CONTENT = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Auction Alert</h2>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t<table>\n" +
		"\t\t\t\t\t\t\t\n" +
		"\t\t\t\t\t\t\t<h3 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.25em; line-height: 1.5em; text-align: center;\">Test keywords</h3>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
		"\t\t\t\t\t\t\t\t\t<td>\n" +
		"\t\t\t\t\t\t\t\t\t\t<div style=\"display: inline-block; padding: 10px; width: 140px;\">\n" +
		"\t\t\t\t\t\t\t\t\t\t\t<img alt=\"http://www.ebay.com/123.jpg\" src=\"http://www.ebay.com/123.jpg\">\n" +
		"\t\t\t\t\t\t\t\t\t\t</div>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t<div style=\"display: inline-block; text-align: left; vertical-align: top;\">\n" +
		"\t\t\t\t\t\t\t\t\t\t\t<a href=\"http://www.ebay.com/itm/1234\">itemTitle</a> <br> <br>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAuction Price: $14.99\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tFixed Price: $29.99\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
		"\t\t\t\t\t\t\t\t\t</td>\n" +
		"\t\t\t\t\t\t\t\t</tr>\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
		"\t\t\t\t\t\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t<a style=\"display: block;\" href=\"http://www.test.com/email_unsubscribe?emailAddress=test@test.com&unsubscribeToken=unsubscribeToken\">Unsubscribe</a>\n" +
		"\t\t\t<p style=\"text-align: center\">\n" +
		"\t\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t\t</p>\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	private static final String _WELCOME_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Welcome to Auction Alert</h2>\n" +
		"\n" +
		"\t\t\t\t\t<h3 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">What can you do now?</h3>\n" +
		"\n" +
		"\t\t\t\t\t<ul style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\t<li><a href=\"http://www.test.com/add_search_query\">Start adding search queries</a></li>\n" +
		"\t\t\t\t\t\t<li><a href=\"http://www.test.com/monitor\">Monitor a favorite search in real time</a></li>\n" +
		"\t\t\t\t\t\t<li><a href=\"http://www.test.com/faq\">Learn more in the FAQ</a></li>\n" +
		"\t\t\t\t\t</ul>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

}