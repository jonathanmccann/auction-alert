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

import com.app.dao.UserDAO;
import com.app.model.User;
import com.app.test.BaseTestCase;

import java.util.List;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserDAOCacheTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpDatabase();
	}

	@Test
	public void testGetUserByUserId() throws Exception {
		Cache cache = _cacheManager.getCache("userByUserId");

		StatisticsGateway statistics = cache.getStatistics();

		_userDAO.addUser("test@test.com", "password", "salt");

		User user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test@test.com");

		user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test@test.com");

		Assert.assertEquals(1, statistics.cacheMissCount());
		Assert.assertEquals(1, statistics.cacheHitCount());

		_userDAO.updateUser(
			1, "test2@test.com", "1234567890", "iOS", "@txt.att.net");

		user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test2@test.com");

		user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test2@test.com");

		Assert.assertEquals(2, statistics.cacheMissCount());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(1, statistics.cacheRemoveCount());

		_userDAO.deleteUserByUserId(1);

		user = _userDAO.getUserByUserId(1);

		Assert.assertNull(user);

		user = _userDAO.getUserByUserId(1);

		Assert.assertNull(user);

		Assert.assertEquals(3, statistics.cacheMissCount());
		Assert.assertEquals(3, statistics.cacheHitCount());
		Assert.assertEquals(2, statistics.cacheRemoveCount());
	}

	@Test
	public void testGetUserIds() throws Exception {
		Cache cache = _cacheManager.getCache("userIds");

		StatisticsGateway statistics = cache.getStatistics();

		_userDAO.addUser("test@test.com", "password", "salt");
		_userDAO.addUser("test2@test.com", "password", "salt");

		List<Integer> userIds = _userDAO.getUserIds();

		_assertUserIds(userIds, 2);

		userIds = _userDAO.getUserIds();

		_assertUserIds(userIds, 2);

		Assert.assertEquals(1, statistics.cacheMissCount());
		Assert.assertEquals(1, statistics.cacheHitCount());

		_userDAO.addUser("test3@test.com", "password", "salt");

		userIds = _userDAO.getUserIds();

		_assertUserIds(userIds, 3);

		userIds = _userDAO.getUserIds();

		_assertUserIds(userIds, 3);

		Assert.assertEquals(2, statistics.cacheMissCount());
		Assert.assertEquals(2, statistics.cacheHitCount());

		_userDAO.deleteUserByUserId(3);

		userIds = _userDAO.getUserIds();

		_assertUserIds(userIds, 2);

		userIds = _userDAO.getUserIds();

		_assertUserIds(userIds, 2);

		Assert.assertEquals(3, statistics.cacheMissCount());
		Assert.assertEquals(3, statistics.cacheHitCount());
	}

	private static void _assertUser(User user, String emailAddress) {
		Assert.assertEquals(1, user.getUserId());
		Assert.assertEquals(emailAddress, user.getEmailAddress());
	}

	private static void _assertUserIds(
		List<Integer> userIds, int expectedSize) {

		Assert.assertEquals(expectedSize, userIds.size());
		Assert.assertEquals(1, (int)userIds.get(0));
		Assert.assertEquals(2, (int)userIds.get(1));

		if (expectedSize == 3) {
			Assert.assertEquals(3, (int)userIds.get(2));
		}
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private UserDAO _userDAO;

}