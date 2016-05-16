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

package com.app.test.dao;

import com.app.dao.NotificationPreferencesDAO;
import com.app.model.NotificationPreferences;
import com.app.test.BaseTestCase;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NotificationPreferencesDAOCacheTest extends BaseTestCase {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private NotificationPreferencesDAO notificationPreferencesDAO;

	@Before
	public void setUp() throws Exception {
		setUpDatabase();
	}

	@Test
	public void testGetNotificationPreferencesByUserId() throws Exception {
		Cache cache = cacheManager.getCache("notificationPreferences");

		StatisticsGateway statistics = cache.getStatistics();

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setUserId(_USER_ID);

		notificationPreferencesDAO.addNotificationPreferences(
			notificationPreferences);

		notificationPreferences =
			notificationPreferencesDAO.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertNotNull(notificationPreferences);

		notificationPreferences =
			notificationPreferencesDAO.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertNotNull(notificationPreferences);

		Assert.assertEquals(1, statistics.cacheMissCount());
		Assert.assertEquals(1, statistics.cacheHitCount());

		notificationPreferencesDAO.updateNotificationPreferences(
			notificationPreferences);

		notificationPreferences =
			notificationPreferencesDAO.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertNotNull(notificationPreferences);

		notificationPreferences =
			notificationPreferencesDAO.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertNotNull(notificationPreferences);

		Assert.assertEquals(2, statistics.cacheMissCount());
		Assert.assertEquals(2, statistics.cacheHitCount());

		notificationPreferences.setUserId(_USER_ID + 1);

		notificationPreferencesDAO.addNotificationPreferences(
			notificationPreferences);

		notificationPreferences =
			notificationPreferencesDAO.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertNotNull(notificationPreferences);

		notificationPreferences =
			notificationPreferencesDAO.getNotificationPreferencesByUserId(
				_USER_ID);

		Assert.assertNotNull(notificationPreferences);

		Assert.assertEquals(3, statistics.cacheMissCount());
		Assert.assertEquals(3, statistics.cacheHitCount());
	}

}