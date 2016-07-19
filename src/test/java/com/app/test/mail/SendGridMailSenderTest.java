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

import com.sendgrid.SendGrid;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
	public void testPopulateContactMessage() throws Exception {
		Method populateContactMessageMethod = _clazz.getDeclaredMethod(
			"_populateContactMessage", String.class, String.class);

		populateContactMessageMethod.setAccessible(true);

		SendGrid.Email email =
			(SendGrid.Email)populateContactMessageMethod.invoke(
				_classInstance, "test@test.com", "Sample contact message");

		Assert.assertEquals("test@test.com", email.getFrom());
		Assert.assertEquals(
			"You Have A New Message From test@test.com", email.getSubject());
		Assert.assertEquals("Sample contact message", email.getText());
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
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

		SendGrid.Email email =
			(SendGrid.Email)populateEmailMessageMethod.invoke(
				_classInstance, searchQueryResultMap, "test@test.com",
				"unsubscribeToken");

		Assert.assertEquals("test@test.com", email.getFrom());
		Assert.assertTrue(
			email.getSubject().contains("New Search Results - "));
		Assert.assertEquals(
			"Keywords: Test keywords\n\nItem: itemTitle\n" +
				"Auction Price: $14.99\nFixed Price: $29.99\n" +
					"URL: http://www.ebay.com/itm/1234\n\n" +
						"<a href=\"/email_unsubscribe?emailAddress=test@test.com&" +
							"unsubscribeToken=unsubscribeToken\">Unsubscribe</a>",
			email.getText());

		String[] recipientAddresses = new String[1];

		recipientAddresses[0] = "test@test.com";

		Assert.assertArrayEquals(recipientAddresses, email.getTos());
	}

	private static Object _classInstance;
	private static Class _clazz;

}