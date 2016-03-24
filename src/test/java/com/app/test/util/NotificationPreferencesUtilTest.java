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
import com.app.test.BaseTestCase;
import com.app.util.NotificationPreferencesUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationPreferencesUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
	}

	@After
	public void tearDown() throws Exception {
		NotificationPreferencesUtil.deleteNotificationPreferencesByUserId(
			_USER_ID);
	}

	@Test
	public void testAddNotificationPreferences() throws Exception {
		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setUserId(_USER_ID);
		notificationPreferences.setEmailNotification(true);
		notificationPreferences.setTextNotification(true);
		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(1);
		notificationPreferences.setEndOfDay(2);
		notificationPreferences.setTimeZone("PST");
		notificationPreferences.setWeekdayDayEmailNotification(true);
		notificationPreferences.setWeekdayDayTextNotification(true);
		notificationPreferences.setWeekdayNightEmailNotification(true);
		notificationPreferences.setWeekdayNightTextNotification(true);
		notificationPreferences.setWeekendDayEmailNotification(true);
		notificationPreferences.setWeekendDayTextNotification(true);
		notificationPreferences.setWeekendNightEmailNotification(true);
		notificationPreferences.setWeekendNightTextNotification(true);
		notificationPreferences.setMobileOperatingSystem("Android");
		notificationPreferences.setMobileCarrierSuffix("@txt.att.net");

		NotificationPreferencesUtil.addNotificationPreferences(
			notificationPreferences);

		notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertEquals(_USER_ID, notificationPreferences.getUserId());
		Assert.assertTrue(notificationPreferences.isEmailNotification());
		Assert.assertTrue(notificationPreferences.isTextNotification());
		Assert.assertTrue(notificationPreferences.isBasedOnTime());
		Assert.assertEquals(1, notificationPreferences.getStartOfDay());
		Assert.assertEquals(2, notificationPreferences.getEndOfDay());
		Assert.assertEquals("PST", notificationPreferences.getTimeZone());
		Assert.assertTrue(
			notificationPreferences.isWeekdayDayEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekdayDayTextNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekdayNightEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekdayNightTextNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendDayEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendDayTextNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendNightEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendNightTextNotification());
		Assert.assertEquals(
			"Android", notificationPreferences.getMobileOperatingSystem());
		Assert.assertEquals(
			"@txt.att.net", notificationPreferences.getMobileCarrierSuffix());
	}

	@Test
	public void testUpdateNotificationPreferences() throws Exception {
		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setUserId(_USER_ID);
		notificationPreferences.setEmailNotification(true);
		notificationPreferences.setTextNotification(true);
		notificationPreferences.setBasedOnTime(true);
		notificationPreferences.setStartOfDay(1);
		notificationPreferences.setEndOfDay(2);
		notificationPreferences.setTimeZone("PST");
		notificationPreferences.setWeekdayDayEmailNotification(true);
		notificationPreferences.setWeekdayDayTextNotification(true);
		notificationPreferences.setWeekdayNightEmailNotification(true);
		notificationPreferences.setWeekdayNightTextNotification(true);
		notificationPreferences.setWeekendDayEmailNotification(true);
		notificationPreferences.setWeekendDayTextNotification(true);
		notificationPreferences.setWeekendNightEmailNotification(true);
		notificationPreferences.setWeekendNightTextNotification(true);
		notificationPreferences.setMobileOperatingSystem("Android");
		notificationPreferences.setMobileCarrierSuffix("@txt.att.net");

		NotificationPreferencesUtil.addNotificationPreferences(
			notificationPreferences);

		notificationPreferences.setEmailNotification(false);
		notificationPreferences.setTextNotification(false);
		notificationPreferences.setBasedOnTime(false);
		notificationPreferences.setStartOfDay(2);
		notificationPreferences.setEndOfDay(3);
		notificationPreferences.setTimeZone("MST");
		notificationPreferences.setWeekdayDayEmailNotification(false);
		notificationPreferences.setWeekdayDayTextNotification(false);
		notificationPreferences.setWeekdayNightEmailNotification(false);
		notificationPreferences.setWeekdayNightTextNotification(false);
		notificationPreferences.setWeekendDayEmailNotification(false);
		notificationPreferences.setWeekendDayTextNotification(false);
		notificationPreferences.setWeekendNightEmailNotification(false);
		notificationPreferences.setWeekendNightTextNotification(false);
		notificationPreferences.setMobileOperatingSystem("iOS");
		notificationPreferences.setMobileCarrierSuffix("@vtext.com");

		NotificationPreferencesUtil.updateNotificationPreferences(
			notificationPreferences);

		notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertEquals(_USER_ID, notificationPreferences.getUserId());
		Assert.assertFalse(notificationPreferences.isEmailNotification());
		Assert.assertFalse(notificationPreferences.isTextNotification());
		Assert.assertFalse(notificationPreferences.isBasedOnTime());
		Assert.assertEquals(2, notificationPreferences.getStartOfDay());
		Assert.assertEquals(3, notificationPreferences.getEndOfDay());
		Assert.assertEquals("MST", notificationPreferences.getTimeZone());
		Assert.assertFalse(
			notificationPreferences.isWeekdayDayEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekdayDayTextNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekdayNightEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekdayNightTextNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendDayEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendDayTextNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendNightEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendNightTextNotification());
		Assert.assertEquals(
			"iOS", notificationPreferences.getMobileOperatingSystem());
		Assert.assertEquals(
			"@vtext.com", notificationPreferences.getMobileCarrierSuffix());
	}

}