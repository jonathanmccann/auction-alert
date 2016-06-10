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
import com.app.exception.InvalidEmailAddressException;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.UserUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(UserUtil.class.getName());

		_classInstance = _clazz.newInstance();
	}

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
		Assert.assertTrue(user.isEmailNotification());
	}

	@Test(expected = DuplicateEmailAddressException.class)
	public void testAddUserWithDuplicateEmailAddress() throws Exception {
		UserUtil.addUser("test@test.com", "password");
		UserUtil.addUser("test@test.com", "updatedPassword");
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
		Assert.assertEquals(firstUser.getUserId(), (int) userIds.get(0));
		Assert.assertEquals(secondUser.getUserId(), (int) userIds.get(1));
	}

	@Test
	public void testUpdateUser() throws Exception {
		User user = UserUtil.addUser("update@test.com", "password");

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());
		Assert.assertTrue(user.isEmailNotification());
		Assert.assertFalse(user.isActive());

		user.setEmailAddress("test@test.com");
		user.setEmailNotification(false);
		user.setActive(true);

		UserUtil.updateUser(user);

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertFalse(user.isEmailNotification());
		Assert.assertTrue(user.isActive());
	}

	@Test
	public void testValidateEmailAddress() throws Exception {
		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		validateEmailAddress.invoke(_classInstance, _USER_ID, "test@test.com");
	}

	@Test
	public void testValidateEmailAddressWithDuplicateEmailAddress()
		throws Exception {

		User user = UserUtil.addUser("test@test.com", "password");

		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		try {
			validateEmailAddress.invoke(
				_classInstance, user.getUserId() + 1, "test@test.com");

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof DuplicateEmailAddressException);
		}
	}

	@Test
	public void testValidateEmailAddressWithInvalidEmailAddress()
		throws Exception {

		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		try {
			validateEmailAddress.invoke(_classInstance, _USER_ID, "test");

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof InvalidEmailAddressException);
		}
	}

	private static Object _classInstance;
	private static Class _clazz;

}