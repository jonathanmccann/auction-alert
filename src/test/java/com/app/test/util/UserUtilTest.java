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

import com.app.exception.DuplicateEmailAddressException;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.UserUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class UserUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByEmailAddress("test@test.com");
		UserUtil.deleteUserByEmailAddress("test2@test.com");
	}

	@Test
	public void testAddAndGetUserByEmailAddress() throws Exception {
		UserUtil.addUser("test@test.com", "password");

		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());
	}

	@Test(expected=DuplicateEmailAddressException.class)
	public void testAddUserWithDuplicateEmailAddress() throws Exception {
		UserUtil.addUser("test@test.com", "password");
		UserUtil.addUser("test@test.com", "updatedPassword");
	}

	@Test
	public void testGetUserByInvalidEmailAddress() throws Exception {
		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNull(user);
	}

	@Test
	public void testGetUserByInvalidUserId() throws Exception {
		User user = UserUtil.getUserByUserId(100);

		Assert.assertNull(user);
	}

	@Test
	public void testGetUserIds() throws Exception {
		Integer firstUserId = UserUtil.addUser("test@test.com", "password");
		Integer secondUserId = UserUtil.addUser("test2@test.com", "password");

		List<Integer> userIds = UserUtil.getUserIds();

		Assert.assertEquals(2, userIds.size());
		Assert.assertEquals(firstUserId, userIds.get(0));
		Assert.assertEquals(secondUserId, userIds.get(1));
	}

	@Test
	public void testDeleteUser() throws Exception {
		UserUtil.addUser("test@test.com", "password");

		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUserByUserId(user.getUserId());

		user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNull(user);
	}

	@Test
	public void testUpdateUser() throws Exception {
		int userId = UserUtil.addUser("update@test.com", "password");

		User user = UserUtil.getUserByUserId(userId);

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());

		UserUtil.updateUser(user.getUserId(), "test@test.com");

		user = UserUtil.getUserByUserId(userId);

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());
	}

}