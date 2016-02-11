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

package com.app.test.util;

import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.MailUtil;

import com.app.util.PropertiesUtil;
import com.app.util.PropertiesValues;
import freemarker.template.Template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.joda.time.DateTime;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class MailUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(MailUtil.class.getName());

		_classInstance = _clazz.newInstance();

		setUpProperties();
	}

	@Test
	public void testConvertPhoneNumbersToEmailAddresses() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"convertPhoneNumbersToEmailAddresses", List.class);

		method.setAccessible(true);

		List<String> recipientPhoneNumbers = new ArrayList<>();

		recipientPhoneNumbers.add("1234567890");
		recipientPhoneNumbers.add("2345678901");

		method.invoke(_classInstance, recipientPhoneNumbers);

		Assert.assertEquals(
			"1234567890@txt.att.net", recipientPhoneNumbers.get(0));
		Assert.assertEquals(
			"2345678901@txt.att.net", recipientPhoneNumbers.get(1));
	}

	@Test
	public void testGetEmailTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod("getEmailTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance);

		Assert.assertNotNull(template);
	}

	@Test
	public void testGetRecipientEmailAddresses() throws Exception {
		Method method = _clazz.getDeclaredMethod("getRecipientEmailAddresses");

		method.setAccessible(true);

		List<String> recipientEmailAddresses = (List<String>)method.invoke(
			_classInstance);

		Assert.assertEquals("test@test.com", recipientEmailAddresses.get(0));
		Assert.assertEquals("test2@test2.com", recipientEmailAddresses.get(1));

		List<String> storedRecipientEmailAddresses =
			(List<String>)method.invoke(_classInstance);

		Assert.assertEquals(
			"test@test.com", storedRecipientEmailAddresses.get(0));
		Assert.assertEquals(
			"test2@test2.com", storedRecipientEmailAddresses.get(1));
	}

	@Test
	public void testGetRecipientPhoneNumbers() throws Exception {
		Method method = _clazz.getDeclaredMethod("getRecipientPhoneNumbers");

		method.setAccessible(true);

		List<String> recipientEmailAddresses = (List<String>)method.invoke(
			_classInstance);

		Assert.assertEquals(
			"1234567890@txt.att.net", recipientEmailAddresses.get(0));
		Assert.assertEquals(
			"2345678901@txt.att.net", recipientEmailAddresses.get(1));

		List<String> storedRecipientEmailAddresses =
			(List<String>)method.invoke(_classInstance);

		Assert.assertEquals(
			"1234567890@txt.att.net", storedRecipientEmailAddresses.get(0));
		Assert.assertEquals(
			"2345678901@txt.att.net", storedRecipientEmailAddresses.get(1));
	}

	@Test
	public void testGetTextTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod("getTextTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance);

		Assert.assertNotNull(template);
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		Method populateEmailMessageMethod = _clazz.getDeclaredMethod(
			"populateEmailMessage", SearchQuery.class, List.class,
			List.class, String.class, Session.class);

		populateEmailMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		Date endingTime = new Date();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords");

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg", endingTime, "Buy It Now");

		searchResults.add(searchResult);

		List<String> emailAddresses = new ArrayList<>();

		emailAddresses.add("test@test.com");
		emailAddresses.add("test2@test2.com");

		Method authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("authenticateOutboundEmailAddress");

		authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);

		Message message = (Message)populateEmailMessageMethod.invoke(
			_classInstance, searchQuery, searchResults,
			emailAddresses, "test@test.com", session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertTrue(
			message.getSubject().contains("New Search Results - "));
		Assert.assertEquals(
			"Keywords: Test keywords\n\nItem: itemTitle\n" +
				"Auction Price: $14.99\nFixed Price: $29.99\n" +
					"URL: http://www.ebay.com/itm/1234\n\n",
			message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[2];

		internetAddresses[0] = new InternetAddress("test@test.com");
		internetAddresses[1] = new InternetAddress("test2@test2.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.CC));
	}

	@Test
	public void testPopulateTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"populateTextMessage", List.class, List.class, Session.class);

		populateTextMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		Date endingTime = new Date();

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg", endingTime, "Buy It Now");

		searchResults.add(searchResult);

		List<String> phoneNumberEmailAddresses = new ArrayList<>();

		phoneNumberEmailAddresses.add("1234567890@txt.att.net");
		phoneNumberEmailAddresses.add("2345678901@txt.att.net");

		Method authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("authenticateOutboundEmailAddress");

		authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);

		Message message = (Message)populateTextMessageMethod.invoke(
			_classInstance, searchResults, phoneNumberEmailAddresses,
			session);

		Assert.assertEquals(
			"itemTitle\nm.ebay.com/itm/1234\n", message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[2];

		internetAddresses[0] = new InternetAddress("1234567890@txt.att.net");
		internetAddresses[1] = new InternetAddress("2345678901@txt.att.net");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.CC));
	}

	@Test
	public void testSetNotificationDeliveryMethodsAfterEndOfDay()
		throws Exception {

		DateTime dateTime =
			new DateTime().withDayOfWeek(1).withHourOfDay(_END_OF_DAY + 1);

		boolean[] notificationDeliverMethods =
			setNotificationDeliveryMethodsWithoutRecipients(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddress(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithPhoneNumber(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsBeforeStartOfDay()
		throws Exception {

		DateTime dateTime =
			new DateTime().withDayOfWeek(1).withHourOfDay(_START_OF_DAY - 1);

		boolean[] notificationDeliverMethods =
			setNotificationDeliveryMethodsWithoutRecipients(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddress(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithPhoneNumber(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsDuringDay()
		throws Exception {

		DateTime dateTime =
			new DateTime().withDayOfWeek(1).withHourOfDay(_START_OF_DAY + 1);

		boolean[] notificationDeliverMethods =
			setNotificationDeliveryMethodsWithoutRecipients(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddress(dateTime);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithPhoneNumber(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
				dateTime);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsNotBasedOnTime()
		throws Exception {

		Class clazz = Class.forName(PropertiesValues.class.getName());

		Field sendNotificationsBasedOnTimeField = clazz.getDeclaredField(
			"SEND_NOTIFICATIONS_BASED_ON_TIME");

		sendNotificationsBasedOnTimeField.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");

		modifiersField.setAccessible(true);

		modifiersField.setInt(
			sendNotificationsBasedOnTimeField,
			sendNotificationsBasedOnTimeField.getModifiers() & ~Modifier.FINAL);

		sendNotificationsBasedOnTimeField.set(clazz, false);

		boolean[] notificationDeliverMethods =
			setNotificationDeliveryMethodsWithoutRecipients(
				new DateTime());

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddress(
				new DateTime());

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithPhoneNumber(
				new DateTime());

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
				new DateTime());

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);

		sendNotificationsBasedOnTimeField.set(clazz, true);
	}

	@Test
	public void testSetNotificationDeliveryMethodsOnSaturday()
		throws Exception {

		DateTime dateTime = new DateTime().withDayOfWeek(_SATURDAY);

		boolean[] notificationDeliverMethods =
			setNotificationDeliveryMethodsWithoutRecipients(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddress(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithPhoneNumber(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsOnSunday()
		throws Exception {

		DateTime dateTime = new DateTime().withDayOfWeek(_SUNDAY);

		boolean[] notificationDeliverMethods =
			setNotificationDeliveryMethodsWithoutRecipients(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddress(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithPhoneNumber(dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);

		notificationDeliverMethods =
			setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
				dateTime);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testValidateEmailAddresses() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"validateEmailAddresses", List.class);

		method.setAccessible(true);

		List<String> recipientEmailAddresses = new ArrayList<>();

		recipientEmailAddresses.add("test@test.com");
		recipientEmailAddresses.add("invalidEmailAddress");
		recipientEmailAddresses.add("anotherInvalidEmailAddress");
		recipientEmailAddresses.add("test2@test2.com");

		method.invoke(_classInstance, recipientEmailAddresses);

		Assert.assertEquals(2, recipientEmailAddresses.size());
		Assert.assertEquals("test@test.com", recipientEmailAddresses.get(0));
		Assert.assertEquals("test2@test2.com", recipientEmailAddresses.get(1));
	}

	@Test
	public void testValidatePhoneNumbers() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"validatePhoneNumbers", List.class);

		method.setAccessible(true);

		List<String> recipientPhoneNumbers = new ArrayList<>();

		recipientPhoneNumbers.add("1234567890");
		recipientPhoneNumbers.add("1234");
		recipientPhoneNumbers.add("test");
		recipientPhoneNumbers.add("2345678901");

		method.invoke(_classInstance, recipientPhoneNumbers);

		Assert.assertEquals(2, recipientPhoneNumbers.size());
		Assert.assertEquals("1234567890", recipientPhoneNumbers.get(0));
		Assert.assertEquals("2345678901", recipientPhoneNumbers.get(1));
	}

	private static boolean[] setNotificationDeliveryMethodsWithoutRecipients(
			DateTime dateTime)
		throws Exception {

		return setNotificationDeliveryMethods(
			new ArrayList<String>(), new ArrayList<String>(), dateTime);
	}

	private static boolean[] setNotificationDeliveryMethodsWithEmailAddress(
			DateTime dateTime)
		throws Exception {

		List<String> recipientEmailAddresses = new ArrayList<>();

		recipientEmailAddresses.add("test@test.com");

		return setNotificationDeliveryMethods(
			recipientEmailAddresses, new ArrayList<String>(), dateTime);
	}

	private static boolean[] setNotificationDeliveryMethodsWithPhoneNumber(
			DateTime dateTime)
		throws Exception {

		List<String> recipientPhoneNumbers = new ArrayList<>();

		recipientPhoneNumbers.add("1234567890");

		return setNotificationDeliveryMethods(
			new ArrayList<String>(), recipientPhoneNumbers, dateTime);
	}

	private static boolean[] setNotificationDeliveryMethodsWithEmailAddressAndPhoneNumber(
			DateTime dateTime)
		throws Exception {

		List<String> recipientEmailAddresses = new ArrayList<>();
		List<String> recipientPhoneNumbers = new ArrayList<>();

		recipientEmailAddresses.add("test@test.com");
		recipientPhoneNumbers.add("1234567890");

		return setNotificationDeliveryMethods(
			recipientEmailAddresses, recipientPhoneNumbers, dateTime);
	}

	private static boolean[] setNotificationDeliveryMethods(
			List<String> recipientEmailAddresses,
			List<String> recipientPhoneNumbers, DateTime dateTime)
		throws Exception {

		Method method = _clazz.getDeclaredMethod(
			"setNotificationDeliveryMethods", List.class, List.class,
			DateTime.class);

		method.setAccessible(true);

		method.invoke(
			_classInstance, recipientEmailAddresses, recipientPhoneNumbers,
			dateTime);

		Field sendViaEmailField = _clazz.getDeclaredField("_sendViaEmail");
		Field sendViaTextField = _clazz.getDeclaredField("_sendViaText");

		sendViaEmailField.setAccessible(true);
		sendViaTextField.setAccessible(true);

		return new boolean[] {
			(boolean)sendViaEmailField.get(_clazz),
			(boolean)sendViaTextField.get(_clazz)
		};
	}

	private static Object _classInstance;
	private static Class _clazz;

	private static final int _END_OF_DAY = 17;
	private static final int _SATURDAY = 6;
	private static final int _START_OF_DAY = 7;
	private static final int _SUNDAY = 7;
	private static final int _USER_ID = 1;

}