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
import com.app.exception.PasswordLengthException;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.UserUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.modules.junit4.rule.PowerMockRule;

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

	@Rule
	public PowerMockRule rule = new PowerMockRule();

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
		setUpUserUtil();

		User firstUser = UserUtil.addUser("test@test.com", "password");
		User secondUser = UserUtil.addUser("test2@test.com", "password");

		UserUtil.updateUserSubscription(
			firstUser.getUserId(), firstUser.getUnsubscribeToken(),
			firstUser.getCustomerId(), firstUser.getSubscriptionId(), true,
			firstUser.isPendingCancellation());

		List<Integer> activeUserIds = UserUtil.getUserIds(true);

		Assert.assertEquals(1, activeUserIds.size());
		Assert.assertEquals(firstUser.getUserId(), (int) activeUserIds.get(0));

		List<Integer> inactiveUserIds = UserUtil.getUserIds(false);

		Assert.assertEquals(1, inactiveUserIds.size());
		Assert.assertEquals(secondUser.getUserId(), (int) inactiveUserIds.get(0));
	}

	@Test
	public void testResetEmailsSent() throws Exception {
		User firstUser = UserUtil.addUser("test@test.com", "password");
		User secondUser = UserUtil.addUser("test2@test.com", "password");

		UserUtil.updateEmailsSent(firstUser.getUserId(), 5);
		UserUtil.updateEmailsSent(secondUser.getUserId(), 10);

		firstUser = UserUtil.getUserByUserId(firstUser.getUserId());
		secondUser = UserUtil.getUserByUserId(secondUser.getUserId());

		Assert.assertEquals(5, firstUser.getEmailsSent());
		Assert.assertEquals(10, secondUser.getEmailsSent());

		UserUtil.resetEmailsSent();

		firstUser = UserUtil.getUserByUserId(firstUser.getUserId());
		secondUser = UserUtil.getUserByUserId(secondUser.getUserId());

		Assert.assertEquals(0, firstUser.getEmailsSent());
		Assert.assertEquals(0, secondUser.getEmailsSent());
	}

	@Test
	public void testUpdateEmailsSent() throws Exception {
		User user = UserUtil.addUser("test@test.com", "password");

		Assert.assertEquals(0, user.getEmailsSent());

		UserUtil.updateEmailsSent(user.getUserId(), 5);

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertEquals(5, user.getEmailsSent());
	}
	@Test
	public void testUnsubscribeUserFromEmailNotifications() throws Exception {
		User user = UserUtil.addUser("test@test.com", "password");

		Assert.assertTrue(user.isEmailNotification());

		UserUtil.unsubscribeUserFromEmailNotifications(user.getEmailAddress());

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertFalse(user.isEmailNotification());
	}

	@Test
	public void testUpdateUserDetails() throws Exception {
		setUpUserUtil();

		User user = UserUtil.addUser("update@test.com", "password");

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());
		Assert.assertTrue(user.isEmailNotification());

		UserUtil.updateUserDetails("test@test.com", false);

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertFalse(user.isEmailNotification());
	}

	@Test
	public void testUpdateUserLoginDetails() throws Exception {
		setUpUserUtil();

		User user = UserUtil.addUser("test@test.com", "password");

		Assert.assertNull(user.getLastLoginDate());
		Assert.assertNull(user.getLastLoginIpAddress());

		Timestamp date = new Timestamp(System.currentTimeMillis());

		UserUtil.updateUserLoginDetails(date, "127.0.0.1");

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotNull(user.getLastLoginDate());
		Assert.assertEquals("127.0.0.1", user.getLastLoginIpAddress());
	}

	@Test
	public void testUpdateUserSubscription() throws Exception {
		setUpUserUtil();

		User user = UserUtil.addUser("update@test.com", "password");

		Assert.assertNotNull(user);
		Assert.assertNull(user.getCustomerId());
		Assert.assertNull(user.getSubscriptionId());
		Assert.assertFalse(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());

		UserUtil.updateUserSubscription(
			user.getUserId(), "unsubscribeToken", "customerId",
			"subscriptionId", true, true);

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("unsubscribeToken", user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
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

	@Test
	public void testValidatePassword() throws Exception {
		Method validatePassword = _clazz.getDeclaredMethod(
			"_validatePassword", String.class);

		validatePassword.setAccessible(true);

		validatePassword.invoke(_classInstance, "password");
	}

	@Test
	public void testValidatePasswordWithNullPassword() throws Exception {
		Method validatePassword = _clazz.getDeclaredMethod(
			"_validatePassword", String.class);

		validatePassword.setAccessible(true);

		try {
			validatePassword.invoke(_classInstance, "");

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof PasswordLengthException);
		}
	}

	@Test
	public void testValidatePasswordWithShortPassword() throws Exception {
		Method validatePassword = _clazz.getDeclaredMethod(
			"_validatePassword", String.class);

		validatePassword.setAccessible(true);

		try {
			validatePassword.invoke(_classInstance, "short");

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof PasswordLengthException);
		}
	}

	private static Object _classInstance;
	private static Class _clazz;

}