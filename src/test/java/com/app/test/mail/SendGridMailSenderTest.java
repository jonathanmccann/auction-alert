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
import java.util.List;

import org.joda.time.DateTimeUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SendGridMailSenderTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(SendGridMailSender.class.getName());

		_classInstance = _clazz.newInstance();

		setUpProperties();
	}

	@After
	public void tearDown() throws Exception {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void testPopulateAndroidTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"_populateTextMessage", SearchResult.class, String.class,
			String.class);

		populateTextMessageMethod.setAccessible(true);

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		SendGrid.Email email = (SendGrid.Email)populateTextMessageMethod.invoke(
			_classInstance, searchResult, "1234567890@txt.att.net", "Android");

		Assert.assertEquals("test@test.com", email.getFrom());
		Assert.assertEquals(
			"itemTitle\neBay://item/view?id=1234\n", email.getText());

		String[] recipientAddresses = new String[1];

		recipientAddresses[0] = "1234567890@txt.att.net";

		Assert.assertArrayEquals(recipientAddresses, email.getTos());
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		Method populateEmailMessageMethod = _clazz.getDeclaredMethod(
			"_populateEmailMessage", SearchQuery.class, List.class, String.class);

		populateEmailMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "Test keywords");

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		SendGrid.Email email =
			(SendGrid.Email)populateEmailMessageMethod.invoke(
				_classInstance, searchQuery, searchResults, "test@test.com");

		Assert.assertEquals("test@test.com", email.getFrom());
		Assert.assertTrue(
			email.getSubject().contains("New Search Results - "));
		Assert.assertEquals(
			"Keywords: Test keywords\n\nItem: itemTitle\n" +
				"Auction Price: $14.99\nFixed Price: $29.99\n" +
				"URL: http://www.ebay.com/itm/1234\n\n",
			email.getText());

		String[] recipientAddresses = new String[1];

		recipientAddresses[0] = "test@test.com";

		Assert.assertArrayEquals(recipientAddresses, email.getTos());
	}

	@Test
	public void testPopulateiOSTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"_populateTextMessage", SearchResult.class, String.class,
			String.class);

		populateTextMessageMethod.setAccessible(true);

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		SendGrid.Email email =
			(SendGrid.Email)populateTextMessageMethod.invoke(
				_classInstance, searchResult, "1234567890@txt.att.net", "iOS");

		Assert.assertEquals("test@test.com", email.getFrom());
		Assert.assertEquals(
			"itemTitle\nebay://launch?itm=1234\n", email.getText());

		String[] recipientAddresses = new String[1];

		recipientAddresses[0] = "1234567890@txt.att.net";

		Assert.assertArrayEquals(recipientAddresses, email.getTos());
	}

	@Test
	public void testPopulateTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"_populateTextMessage", SearchResult.class, String.class,
			String.class);

		populateTextMessageMethod.setAccessible(true);

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		SendGrid.Email email =
			(SendGrid.Email)populateTextMessageMethod.invoke(
				_classInstance, searchResult, "1234567890@txt.att.net", "Other");

		Assert.assertEquals("test@test.com", email.getFrom());
		Assert.assertEquals(
			"itemTitle\nm.ebay.com/itm/1234\n", email.getText());

		String[] recipientAddresses = new String[1];

		recipientAddresses[0] = "1234567890@txt.att.net";

		Assert.assertArrayEquals(recipientAddresses, email.getTos());
	}

	private static Object _classInstance;
	private static Class _clazz;

}