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

		_userDAO.addUser(
			"test@test.com", "password", "salt", "http://www.ebay.com/itm/");
	}

	@Test
	public void testAddUserCacheEvict() throws Exception {
		_assertBeforeCacheEvictUserIds();

		_userDAO.addUser(
			"test2@test.com", "password", "salt", "http://www.ebay.com/itm/");

		_assertAfterCacheEvictUserIds();
	}

	@Test
	public void testDeactivateUserCacheEvict() throws Exception {
		_assertBeforeCacheEvictUserByUserId();
		_assertBeforeCacheEvictUserIds();

		_userDAO.deactivateUser("customerId");

		_assertAfterCacheEvictUserByUserId();
		_assertAfterCacheEvictUserIds();
	}

	@Test
	public void testDeleteUserByUserIdCacheEvict() throws Exception {
		_assertBeforeCacheEvictUserByUserId();
		_assertBeforeCacheEvictUserIds();

		_userDAO.deleteUserByUserId(1);

		_assertAfterCacheEvictUserByUserId();
		_assertAfterCacheEvictUserIds();
	}

	@Test
	public void testGetUserByUserId() throws Exception {
		Cache cache = _cacheManager.getCache("userByUserId");

		StatisticsGateway statistics = cache.getStatistics();

		User user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test@test.com", true, false);

		user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test@test.com", true, false);

		Assert.assertEquals(1, statistics.cacheMissCount());
		Assert.assertEquals(1, statistics.cacheHitCount());

		_userDAO.updateUser(
			1, "test2@test.com", false, "customerId", "subscriptionId", true,
			true);

		user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test2@test.com", false, true);

		user = _userDAO.getUserByUserId(1);

		_assertUser(user, "test2@test.com", false, true);

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

		_userDAO.addUser(
			"test2@test.com", "password", "salt", "http://www.ebay.com/itm/");

		List<Integer> userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		Assert.assertEquals(1, statistics.cacheMissCount());
		Assert.assertEquals(1, statistics.cacheHitCount());

		_userDAO.addUser(
			"test3@test.com", "password", "salt", "http://www.ebay.com/itm/");

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 3);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 3);

		Assert.assertEquals(2, statistics.cacheMissCount());
		Assert.assertEquals(2, statistics.cacheHitCount());

		_userDAO.deleteUserByUserId(3);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		userIds = _userDAO.getUserIds(false);

		_assertUserIds(userIds, 2);

		Assert.assertEquals(3, statistics.cacheMissCount());
		Assert.assertEquals(3, statistics.cacheHitCount());
	}

	@Test
	public void testResetEmailsSentCacheEvict() throws Exception {
		_assertBeforeCacheEvictUserByUserId();

		_userDAO.resetEmailsSent();

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUnsubscribeUserFromEmailNotificationsCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.unsubscribeUserFromEmailNotifications("test@test.com");

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateEmailsSentCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updateEmailsSent(1, 1);

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdatePasswordCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updatePassword(1, "password", "salt");

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdatePasswordResetTokenCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updatePasswordResetToken(1, "passwordResetToken");

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserDetailsCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updateUserDetails(
			1, "test@test.com", "password", "salt", "http://www.ebay.com/itm/",
			true);

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserEmailDetailsCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updateUserEmailDetails(
			1, "test@test.com", "http://www.ebay.com/itm/", true);

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserLoginDetailsCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updateUserLoginDetails(
			1, new Timestamp(System.currentTimeMillis()), "127.0.0.1");

		_assertAfterCacheEvictUserByUserId();
	}

	@Test
	public void testUpdateUserSubscriptionCacheEvict()
		throws Exception {

		_assertBeforeCacheEvictUserByUserId();

		_userDAO.updateUserSubscription(
			1, "customerId", "subscriptionId", true, false);

		_assertAfterCacheEvictUserByUserId();
	}

	private void _assertAfterCacheEvictUserByUserId() throws Exception {
		Cache userByUserIdCache = _cacheManager.getCache("userByUserId");

		StatisticsGateway userByUserIdStatistics =
			userByUserIdCache.getStatistics();

		_userDAO.getUserByUserId(1);

		Assert.assertEquals(2, userByUserIdStatistics.cacheMissCount());
		Assert.assertEquals(1, userByUserIdStatistics.cacheHitCount());
	}

	private void _assertAfterCacheEvictUserIds() throws Exception {
		Cache userIdsCache = _cacheManager.getCache("userIds");

		StatisticsGateway userIdsStatistics =
			userIdsCache.getStatistics();

		_userDAO.getUserIds(true);
		_userDAO.getUserIds(false);

		Assert.assertEquals(4, userIdsStatistics.cacheMissCount());
		Assert.assertEquals(2, userIdsStatistics.cacheHitCount());
	}

	private void _assertBeforeCacheEvictUserByUserId() throws Exception {
		Cache userByUserIdCache = _cacheManager.getCache("userByUserId");

		StatisticsGateway userByUserIdStatistics =
			userByUserIdCache.getStatistics();

		_userDAO.getUserByUserId(1);

		Assert.assertEquals(1, userByUserIdStatistics.cacheMissCount());
		Assert.assertEquals(0, userByUserIdStatistics.cacheHitCount());

		_userDAO.getUserByUserId(1);

		Assert.assertEquals(1, userByUserIdStatistics.cacheMissCount());
		Assert.assertEquals(1, userByUserIdStatistics.cacheHitCount());
	}

	private void _assertBeforeCacheEvictUserIds() throws Exception {
		Cache userIdsCache = _cacheManager.getCache("userIds");

		StatisticsGateway userIdsStatistics =
			userIdsCache.getStatistics();

		_userDAO.getUserIds(true);
		_userDAO.getUserIds(false);

		Assert.assertEquals(2, userIdsStatistics.cacheMissCount());
		Assert.assertEquals(0, userIdsStatistics.cacheHitCount());

		_userDAO.getUserIds(true);
		_userDAO.getUserIds(false);

		Assert.assertEquals(2, userIdsStatistics.cacheMissCount());
		Assert.assertEquals(2, userIdsStatistics.cacheHitCount());
	}

	private static void _assertUser(
			User user, String emailAddress, boolean emailNotification,
			boolean active) {

		Assert.assertEquals(1, user.getUserId());
		Assert.assertEquals(emailAddress, user.getEmailAddress());
		Assert.assertEquals(emailNotification, user.isEmailNotification());
		Assert.assertEquals(active, user.isActive());
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