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

import com.app.mail.MailUtil;
import com.app.model.NotificationPreferences;
import com.app.test.BaseTestCase;

import freemarker.template.Template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
		DateTimeUtils.setCurrentMillisSystem();
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
	public void testGetEmailTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod("getEmailTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance);

		Assert.assertNotNull(template);
		Assert.assertEquals("email_body.ftl", template.getName());
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
	public void testGetNotificationDeliveryMethodsNotBasedOnTime()
		throws Exception {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setBasedOnTime(false);
		notificationPreferences.setEmailNotification(true);
		notificationPreferences.setTextNotification(true);

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testGetNotificationDeliveryMethodsWeekdayAfterEndOfDay()
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

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testGetNotificationDeliveryMethodsWeekdayBeforeStartOfDay()
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

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testGetNotificationDeliveryMethodsWeekdayDuringDay()
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

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);
	}

	@Test
	public void testGetNotificationDeliveryMethodsWeekendAfterEndOfDay()
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

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testGetNotificationDeliveryMethodsWeekendBeforeStartOfDay()
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

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertFalse(notificationDeliverMethods[0]);
		Assert.assertTrue(notificationDeliverMethods[1]);
	}

	@Test
	public void testGetNotificationDeliveryMethodsWeekendDuringDay()
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

		boolean[] notificationDeliverMethods = _getNotificationDeliveryMethods(
			notificationPreferences);

		Assert.assertTrue(notificationDeliverMethods[0]);
		Assert.assertFalse(notificationDeliverMethods[1]);
	}

	private static boolean[] _getNotificationDeliveryMethods(
		NotificationPreferences notificationPreferences)
		throws Exception {

		Method method = _clazz.getDeclaredMethod(
			"getNotificationDeliveryMethods", NotificationPreferences.class);

		method.setAccessible(true);

		return
			(boolean[])method.invoke(_classInstance, notificationPreferences);
	}

	private static final int _END_OF_DAY = 17;

	private static final int _SATURDAY = 6;

	private static final int _START_OF_DAY = 7;

	private static Object _classInstance;
	private static Class _clazz;

}