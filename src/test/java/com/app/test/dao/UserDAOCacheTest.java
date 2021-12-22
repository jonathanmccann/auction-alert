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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserDAOCacheTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
	}

	@After
	public void tearDown() throws Exception {
		for (int userId : _userIds) {
			_userDAO.deleteUserByUserId(userId);
		}

		_userIds.clear();
	}

	@Test
	public void testAddUserCacheEvict() throws Exception {
		User user = _userDAO.addUser(
			"test@test.com", "password", "salt", "EBAY_US");

		_userIds.add(user.getUserId());

		_assertCacheEvictUserIds();
	}

	@Test
	public void testDeactivateUserCacheEvict() throws Exception {
		_userDAO.deactivateUser("customerId");

		_assertCacheEvictUserByUserId();
		_assertCacheEvictUserIds();
	}

	@Test
	public void testDeleteUserByUserIdCacheEvict() throws Exception {
		_userDAO.deleteUserByUserId(1);

		_assertCacheEvictUserByUserId();
		_assertCacheEvictUserIds();
	}

	@Test
	public void testGetUserByUserId() throws Exception {
		Cache cache = _cacheManager.getCache("userByUserId");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();
		long removeCount = statistics.cacheRemoveCount();

		User user = _userDAO.addUser(
			"test@test.com", "password", "salt", "EBAY_US");

		_userIds.add(user.getUserId());

		user = _userDAO.getUserByUserId(_userIds.get(0));

		_assertUser(user, "test@test.com", true, false);

		user = _userDAO.getUserByUserId(_userIds.get(0));

		_assertUser(user, "test@test.com", true, false);

		Assert.assertEquals(missCount + 1, statistics.cacheHitCount());
		Assert.assertEquals(hitCount + 1, statistics.cacheMissCount());

		_userDAO.updateUser(
			1, "test2@test.com", false, "customerId", "subscriptionId", true,
			true);

		user = _userDAO.getUserByUserId(_userIds.get(0));

		_assertUser(user, "test2@test.com", false, true);

		user = _userDAO.getUserByUserId(_userIds.get(0));

		_assertUser(user, "test2@test.com", false, true);

		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
		Assert.assertEquals(removeCount + 1, statistics.cacheRemoveCount());

		_userDAO.deleteUserByUserId(_userIds.get(0));

		user = _userDAO.getUserByUserId(_userIds.get(0));

		Assert.assertNull(user);

		user = _userDAO.getUserByUserId(_userIds.get(0));

		Assert.assertNull(user);

		Assert.assertEquals(hitCount + 3, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 3, statistics.cacheMissCount());
		Assert.assertEquals(removeCount + 2, statistics.cacheRemoveCount());
	}

	@Test
	public void testGetUserIds() throws Exception {
		Cache cache = _cacheManager.getCache("userIds");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();
		long removeCount = statistics.cacheRemoveCount();

		User user = _userDAO.addUser(
			"test@test.com", "password", "salt", "EBAY_US");

		_userIds.add(user.getUserId());

		user = _userDAO.addUser(
			"test2@test.com", "password", "salt", "EBAY_US");

		_userIds.add(user.getUserId());

		List<Integer> userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		Assert.assertEquals(hitCount + 1, statistics.cacheMissCount());
		Assert.assertEquals(missCount + 1, statistics.cacheHitCount());

		user = _userDAO.addUser(
			"test3@test.com", "password", "salt", "EBAY_US");

		_userIds.add(user.getUserId());

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 3);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 3);

		Assert.assertEquals(hitCount + 2, statistics.cacheMissCount());
		Assert.assertEquals(missCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(removeCount + 6, statistics.cacheRemoveCount());

		_userDAO.deleteUserByUserId(_userIds.get(2));

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		Assert.assertEquals(hitCount + 3, statistics.cacheMissCount());
		Assert.assertEquals(missCount + 3, statistics.cacheHitCount());
		Assert.assertEquals(removeCount + 8, statistics.cacheRemoveCount());
	}

	@Test
	public void testResetEmailsSentCacheEvict() throws Exception {
		_userDAO.resetEmailsSent();

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUnsubscribeUserFromEmailNotificationsCacheEvict()
		throws Exception {

		_userDAO.unsubscribeUserFromEmailNotifications("test@test.com");

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateEmailsSentCacheEvict()
		throws Exception {

		_userDAO.updateEmailsSent(1, 1);

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdatePasswordCacheEvict()
		throws Exception {

		_userDAO.updatePassword(1, "password", "salt");

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdatePasswordResetTokenCacheEvict()
		throws Exception {

		_userDAO.updatePasswordResetToken(1, "passwordResetToken");

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserDetailsCacheEvict()
		throws Exception {

		_userDAO.updateUserDetails(
			1, "test@test.com", "password", "salt", "EBAY_US",
			true);

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserEmailDetailsCacheEvict()
		throws Exception {

		_userDAO.updateUserEmailDetails(
			1, "test@test.com", "EBAY_US", true);

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserLoginDetailsCacheEvict()
		throws Exception {

		_userDAO.updateUserLoginDetails(
			1, new Timestamp(System.currentTimeMillis()), "127.0.0.1");

		_assertCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserSubscriptionCacheEvict()
		throws Exception {

		_userDAO.updateUserSubscription(
			1, "customerId", "subscriptionId", true, false);

		_assertCacheEvictUserByUserId();
	}

	private void _assertCacheEvictUserByUserId() throws Exception {
		Cache userByUserIdCache = _cacheManager.getCache("userByUserId");

		StatisticsGateway userByUserIdStatistics =
			userByUserIdCache.getStatistics();

		long hitCount = userByUserIdStatistics.cacheHitCount();
		long missCount = userByUserIdStatistics.cacheMissCount();

		_userDAO.getUserByUserId(1);

		Assert.assertEquals(hitCount, userByUserIdStatistics.cacheHitCount());
		Assert.assertEquals(
			missCount + 1, userByUserIdStatistics.cacheMissCount());

		_userDAO.getUserByUserId(1);

		Assert.assertEquals(
			hitCount + 1, userByUserIdStatistics.cacheHitCount());
		Assert.assertEquals(
			missCount + 1, userByUserIdStatistics.cacheMissCount());
	}

	private void _assertCacheEvictUserIds() throws Exception {
		Cache userIdsCache = _cacheManager.getCache("userIds");

		StatisticsGateway userIdsStatistics =
			userIdsCache.getStatistics();

		long hitCount = userIdsStatistics.cacheHitCount();
		long missCount = userIdsStatistics.cacheMissCount();

		_userDAO.getUserIds(true);
		_userDAO.getUserIds(false);

		Assert.assertEquals(hitCount, userIdsStatistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, userIdsStatistics.cacheMissCount());

		_userDAO.getUserIds(true);
		_userDAO.getUserIds(false);

		Assert.assertEquals(hitCount + 2, userIdsStatistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, userIdsStatistics.cacheMissCount());
	}

	private static void _assertUser(
			User user, String emailAddress, boolean emailNotification,
			boolean active) {

		Assert.assertEquals((int)_userIds.get(0), user.getUserId());
		Assert.assertEquals(emailAddress, user.getEmailAddress());
		Assert.assertEquals(emailNotification, user.isEmailNotification());
		Assert.assertEquals(active, user.isActive());
	}

	private static void _assertUserIds(
		List<Integer> userIds, int expectedSize) {

		Assert.assertEquals(expectedSize, userIds.size());

		Assert.assertEquals(_userIds.get(0), userIds.get(0));
		Assert.assertEquals(_userIds.get(1), userIds.get(1));

		if (expectedSize == 3) {
			Assert.assertEquals(_userIds.get(2), userIds.get(2));
		}
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private UserDAO _userDAO;

	private static List<Integer> _userIds = new ArrayList<>();

}