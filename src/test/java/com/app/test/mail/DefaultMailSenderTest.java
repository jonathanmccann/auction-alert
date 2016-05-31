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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class DefaultMailSenderTest extends BaseTestCase {

	@Test
	public void testPopulateEmailMessage() throws Exception {
		setUpProperties();

		Class clazz = Class.forName(DefaultMailSender.class.getName());

		Object classInstance = clazz.newInstance();

		Method populateEmailMessageMethod = clazz.getDeclaredMethod(
			"_populateEmailMessage", SearchQuery.class, List.class, String.class,
			String.class, Session.class);

		populateEmailMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "Test keywords");

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		Method _authenticateOutboundEmailAddressMethod =
			clazz.getDeclaredMethod("_authenticateOutboundEmailAddress");

		_authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)_authenticateOutboundEmailAddressMethod.invoke(
				classInstance);

		Message message = (Message)populateEmailMessageMethod.invoke(
			classInstance, searchQuery, searchResults, "test@test.com",
			"test@test.com", session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertTrue(
			message.getSubject().contains("New Search Results - "));
		Assert.assertEquals(
			"Keywords: Test keywords\n\nItem: itemTitle\n" +
				"Auction Price: $14.99\nFixed Price: $29.99\n" +
				"URL: http://www.ebay.com/itm/1234\n\n",
			message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("test@test.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

}