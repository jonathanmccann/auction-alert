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

import com.app.model.NotificationPreferences;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.MailUtil;

import freemarker.template.Template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.joda.time.DateTime;

import org.joda.time.DateTimeUtils;
import org.junit.After;
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

	@After
	public void tearDown() throws Exception {
		Field textTemplateField = _clazz.getDeclaredField("_textTemplate");

		textTemplateField.setAccessible(true);

		textTemplateField.set(null, null);

		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void testGetEmailTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod("getEmailTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance);

		Assert.assertNotNull(template);
		Assert.assertEquals("email_body.ftl", template.getName());
	}

	@Test
	public void testGetAndroidTextTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"getTextTemplate", String.class);

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance, "Android");

		Assert.assertNotNull(template);
		Assert.assertEquals("text_body_android.ftl", template.getName());
	}

	@Test
	public void testGetiOSTextTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"getTextTemplate", String.class);

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance, "iOS");

		Assert.assertNotNull(template);
		Assert.assertEquals("text_body_ios.ftl", template.getName());
	}

	@Test
	public void testGetTextTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"getTextTemplate", String.class);

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance, "Other");

		Assert.assertNotNull(template);
		Assert.assertEquals("text_body.ftl", template.getName());
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		Method populateEmailMessageMethod = _clazz.getDeclaredMethod(
			"populateEmailMessage", SearchQuery.class, List.class,
			String.class, String.class, Session.class);

		populateEmailMessageMethod.setAccessible(true);

		List<SearchResult> searchResults = new ArrayList<>();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords");

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		searchResults.add(searchResult);

		Method authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("authenticateOutboundEmailAddress");

		authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);

		Message message = (Message)populateEmailMessageMethod.invoke(
			_classInstance, searchQuery, searchResults,
			"test@test.com", "test@test.com", session);

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

	@Test
	public void testPopulateAndroidTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"populateTextMessage", SearchResult.class, String.class,
			String.class, Session.class);

		populateTextMessageMethod.setAccessible(true);

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		Method authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("authenticateOutboundEmailAddress");

		authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);

		Message message = (Message)populateTextMessageMethod.invoke(
			_classInstance, searchResult, "1234567890@txt.att.net", "Android",
			session);

		Assert.assertEquals(
			"itemTitle\neBay://item/view?id=1234\n", message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("1234567890@txt.att.net");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testPopulateiOSTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"populateTextMessage", SearchResult.class, String.class,
			String.class, Session.class);

		populateTextMessageMethod.setAccessible(true);

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		Method authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("authenticateOutboundEmailAddress");

		authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);

		Message message = (Message)populateTextMessageMethod.invoke(
			_classInstance, searchResult, "1234567890@txt.att.net", "iOS",
			session);

		Assert.assertEquals(
			"itemTitle\nebay://launch?itm=1234\n", message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("1234567890@txt.att.net");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testPopulateTextMessage() throws Exception {
		Method populateTextMessageMethod = _clazz.getDeclaredMethod(
			"populateTextMessage", SearchResult.class, String.class,
			String.class, Session.class);

		populateTextMessageMethod.setAccessible(true);

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg");

		Method authenticateOutboundEmailAddressMethod =
			_clazz.getDeclaredMethod("authenticateOutboundEmailAddress");

		authenticateOutboundEmailAddressMethod.setAccessible(true);

		Session session =
			(Session)authenticateOutboundEmailAddressMethod.invoke(
				_classInstance);

		Message message = (Message)populateTextMessageMethod.invoke(
			_classInstance, searchResult, "1234567890@txt.att.net", "Other",
			session);

		Assert.assertEquals(
			"itemTitle\nm.ebay.com/itm/1234\n", message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[1];

		internetAddresses[0] = new InternetAddress("1234567890@txt.att.net");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.TO));
	}

	@Test
	public void testSetNotificationDeliveryMethodsNotBasedOnTime()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(false);
		notificationPreferences.setEmailNotification(true);
		notificationPreferences.setTextNotification(true);

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsWeekdayAfterEndOfDay()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(_START_OF_DAY);
		notificationPreferences.setEndOfDay(_END_OF_DAY);
		notificationPreferences.setWeekdayNightEmailNotification(false);
		notificationPreferences.setWeekdayNightTextNotification(true);

		DateTime dateTime =
			new DateTime().withDayOfWeek(1).withHourOfDay(_END_OF_DAY + 1);

		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsWeekdayBeforeStartOfDay()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(_START_OF_DAY);
		notificationPreferences.setEndOfDay(_END_OF_DAY);
		notificationPreferences.setWeekdayNightEmailNotification(false);
		notificationPreferences.setWeekdayNightTextNotification(true);

		DateTime dateTime =
			new DateTime().withDayOfWeek(1).withHourOfDay(_START_OF_DAY - 1);

		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsWeekdayDuringDay()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(_START_OF_DAY);
		notificationPreferences.setEndOfDay(_END_OF_DAY);
		notificationPreferences.setWeekdayDayEmailNotification(true);
		notificationPreferences.setWeekdayDayTextNotification(false);

		DateTime dateTime =
			new DateTime().withDayOfWeek(1).withHourOfDay(_START_OF_DAY + 1);

		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsWeekendAfterEndOfDay()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(_START_OF_DAY);
		notificationPreferences.setEndOfDay(_END_OF_DAY);
		notificationPreferences.setWeekendNightEmailNotification(false);
		notificationPreferences.setWeekendNightTextNotification(true);

		DateTime dateTime =
			new DateTime().withDayOfWeek(_SATURDAY).withHourOfDay(_END_OF_DAY + 1);

		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsWeekendBeforeStartOfDay()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(_START_OF_DAY);
		notificationPreferences.setEndOfDay(_END_OF_DAY);
		notificationPreferences.setWeekendNightEmailNotification(false);
		notificationPreferences.setWeekendNightTextNotification(true);

		DateTime dateTime =
			new DateTime().withDayOfWeek(_SATURDAY).withHourOfDay(_START_OF_DAY - 1);

		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testSetNotificationDeliveryMethodsWeekendDuringDay()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(_START_OF_DAY);
		notificationPreferences.setEndOfDay(_END_OF_DAY);
		notificationPreferences.setWeekendDayEmailNotification(true);
		notificationPreferences.setWeekendDayTextNotification(false);

		DateTime dateTime =
			new DateTime().withDayOfWeek(_SATURDAY).withHourOfDay(_START_OF_DAY + 1);

		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());

		boolean[] notificationDeliverMethods = setNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);
	}

	private static boolean[] setNotificationDeliveryMethods(
			NotificationPreferences notificationPreferences)
		throws Exception {

		Method method = _clazz.getDeclaredMethod(
			"setNotificationDeliveryMethod", NotificationPreferences.class);

		method.setAccessible(true);

		return
			(boolean[])method.invoke(_classInstance, notificationPreferences);
	}

	private static Object _classInstance;
	private static Class _clazz;

	private static final int _END_OF_DAY = 17;
	private static final int _SATURDAY = 6;
	private static final int _START_OF_DAY = 7;
	private static final int _USER_ID = 1;

}