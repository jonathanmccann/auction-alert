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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserUtilTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpDatabase();
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
		User firstUser = UserUtil.addUser("test@test.com", "password");
		User secondUser = UserUtil.addUser("test2@test.com", "password");

		List<Integer> userIds = UserUtil.getUserIds();

		Assert.assertEquals(2, userIds.size());
		Assert.assertEquals(firstUser.getUserId(), (int)userIds.get(0));
		Assert.assertEquals(secondUser.getUserId(), (int)userIds.get(1));
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
		User user = UserUtil.addUser("update@test.com", "password");

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());
		Assert.assertNull(user.getPhoneNumber());

		user.setEmailAddress("test@test.com");
		user.setPhoneNumber("2345678901");
		user.setMobileOperatingSystem("Android");
		user.setMobileCarrierSuffix("@txt.att.net");

		UserUtil.updateUser(user);

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertEquals("2345678901", user.getPhoneNumber());
		Assert.assertEquals("Android", user.getMobileOperatingSystem());
		Assert.assertEquals("@txt.att.net", user.getMobileCarrierSuffix());
	}

}