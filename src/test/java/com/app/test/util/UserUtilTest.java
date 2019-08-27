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
import com.app.exception.PasswordResetException;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.ConstantsUtil;
import com.app.util.UserUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authc.CredentialsException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class UserUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();

		setUpDatabase();

		ConstantsUtil.init();

		_clazz = Class.forName(UserUtil.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@Before
	public void setUp() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByUserId(_FIRST_USER.getUserId());

		if (_SECOND_USER != null) {
			UserUtil.deleteUserByUserId(_SECOND_USER.getUserId());
		}
	}

	@Test
	public void testAddAndGetUserByEmailAddress() throws Exception {
		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertTrue(user.isEmailNotification());
	}

	@Test(expected = DuplicateEmailAddressException.class)
	public void testAddUserWithDuplicateEmailAddress() throws Exception {
		UserUtil.addUser("test@test.com", "updatedPassword");
	}

	@Test
	public void testDeactivateUser() throws Exception {
		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			true);

		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());

		UserUtil.deactivateUser("customerId");

		user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertFalse(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteUser() throws Exception {
		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUser("password", user);

		user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNull(user);
	}

	@Test
	public void testDeleteUserByUserId() throws Exception {
		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUserByUserId(user.getUserId());

		user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNull(user);
	}

	@Test(expected = CredentialsException.class)
	public void testDeleteUserWithInvalidCredentials() throws Exception {
		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUser("invalidPassword", user);
	}

	@Test
	public void testExceedsMaximumNumberOfUsers() throws Exception {
		setUpProperties();

		Assert.assertFalse(UserUtil.exceedsMaximumNumberOfUsers());

		_SECOND_USER = UserUtil.addUser("test2@test.com", "password");

		Assert.assertTrue(UserUtil.exceedsMaximumNumberOfUsers());
	}

	@Test
	public void testGetCurrentUserId() throws Exception {
		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		Assert.assertEquals(
			_FIRST_USER.getUserId(), UserUtil.getCurrentUserId());
	}

	@Test
	public void testGetUserByInvalidEmailAddress() throws Exception {
		User user = UserUtil.getUserByEmailAddress("invalid@test.com");

		Assert.assertNull(user);
	}

	@Test
	public void testGetUserByInvalidUserId() throws Exception {
		User user = UserUtil.getUserByUserId(100);

		Assert.assertNull(user);
	}

	@Test
	public void testGetUserIds() throws Exception {
		_SECOND_USER = UserUtil.addUser("test2@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), _FIRST_USER.getCustomerId(),
			_FIRST_USER.getSubscriptionId(), true,
			_FIRST_USER.isPendingCancellation());

		List<Integer> activeUserIds = UserUtil.getUserIds(true);

		Assert.assertEquals(1, activeUserIds.size());
		Assert.assertEquals(
			_FIRST_USER.getUserId(), (int) activeUserIds.get(0));

		List<Integer> inactiveUserIds = UserUtil.getUserIds(false);

		Assert.assertEquals(1, inactiveUserIds.size());
		Assert.assertEquals(
			_SECOND_USER.getUserId(), (int) inactiveUserIds.get(0));
	}

	@Test
	public void testIsCurrentUserActiveWithAuthenticatedAndActiveUser()
		throws Exception {

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		Assert.assertTrue(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testIsCurrentUserActiveWithAuthenticatedAndPendingCancellationUser()
		throws Exception {

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			true);

		Assert.assertTrue(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testIsCurrentUserActiveWithAuthenticatedAndInactiveUser()
		throws Exception {

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			false);

		Assert.assertFalse(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testIsCurrentUserActiveWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		Assert.assertFalse(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testResetEmailsSent() throws Exception {
		_SECOND_USER = UserUtil.addUser("test2@test.com", "password");

		UserUtil.updateEmailsSent(_FIRST_USER.getUserId(), 5);
		UserUtil.updateEmailsSent(_SECOND_USER.getUserId(), 10);

		User firstUser = UserUtil.getUserByUserId(_FIRST_USER.getUserId());
		User secondUser = UserUtil.getUserByUserId(_SECOND_USER.getUserId());

		Assert.assertEquals(5, firstUser.getEmailsSent());
		Assert.assertEquals(10, secondUser.getEmailsSent());

		UserUtil.resetEmailsSent();

		firstUser = UserUtil.getUserByUserId(firstUser.getUserId());
		secondUser = UserUtil.getUserByUserId(secondUser.getUserId());

		Assert.assertEquals(0, firstUser.getEmailsSent());
		Assert.assertEquals(0, secondUser.getEmailsSent());
	}

	@Test
	public void testResetPassword() throws Exception {
		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		String encryptedPassword = user.getPassword();

		UserUtil.resetPassword(
			user.getEmailAddress(), "updatedPassword",
			user.getPasswordResetToken());

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotEquals(encryptedPassword, user.getPassword());
	}

	@Test
	public void testResetPasswordAfterExpiration() throws Exception {
		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		String encryptedPassword = user.getPassword();

		Date date = new Date(0L);

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		user.setPasswordResetExpiration(
			new Timestamp(calendar.getTimeInMillis()));

		try {
			UserUtil.resetPassword(
				user.getEmailAddress(), "updatedPassword",
				"invalidPasswordResetToken");
		}
		catch (PasswordResetException pre) {
			Assert.assertEquals(PasswordResetException.class, pre.getClass());
		}

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertEquals(encryptedPassword, user.getPassword());
	}

	@Test
	public void testResetPasswordWithInvalidToken() throws Exception {
		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		String encryptedPassword = user.getPassword();

		try {
			UserUtil.resetPassword(
				user.getEmailAddress(), "updatedPassword",
				"invalidPasswordResetToken");
		}
		catch (PasswordResetException pre) {
			Assert.assertEquals(PasswordResetException.class, pre.getClass());
		}

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertEquals(encryptedPassword, user.getPassword());
	}

	@Test
	public void testUpdatePassword() throws Exception {
		String password = _FIRST_USER.getPassword();
		String salt = _FIRST_USER.getSalt();

		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		UserUtil.updatePassword(_FIRST_USER.getUserId(), "updatedPassword");

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotEquals(password, user.getPassword());
		Assert.assertNotEquals(salt, user.getSalt());
		Assert.assertNull(user.getPasswordResetToken());
	}

	@Test(expected = PasswordLengthException.class)
	public void testUpdatePasswordWithInvalidPassword() throws Exception {
		UserUtil.updatePassword(_FIRST_USER.getUserId(), "test");
	}

	@Test
	public void testUpdatePasswordResetToken() throws Exception {
		Assert.assertNull(_FIRST_USER.getPasswordResetToken());
		Assert.assertNull(_FIRST_USER.getPasswordResetExpiration());

		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user.getPasswordResetToken());
		Assert.assertNotNull(user.getPasswordResetExpiration());
	}

	@Test
	public void testUpdateEmailsSent() throws Exception {
		Assert.assertEquals(0, _FIRST_USER.getEmailsSent());

		UserUtil.updateEmailsSent(_FIRST_USER.getUserId(), 5);

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals(5, user.getEmailsSent());
	}
	@Test
	public void testUnsubscribeUserFromEmailNotifications() throws Exception {
		Assert.assertTrue(_FIRST_USER.isEmailNotification());

		UserUtil.unsubscribeUserFromEmailNotifications(
			_FIRST_USER.getEmailAddress());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertFalse(user.isEmailNotification());
	}

	@Test
	public void testUpdateUserDetails() throws Exception {
		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		String password = _FIRST_USER.getPassword();
		String salt = _FIRST_USER.getSalt();

		UserUtil.updateUserDetails(
			"update@test.com", "password", "newPassword",
			"http://www.ebay.ca/itm/", false);

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());
		Assert.assertEquals(
			"http://www.ebay.ca/itm/", user.getPreferredDomain());
		Assert.assertFalse(user.isEmailNotification());
		Assert.assertNotEquals(password, user.getPassword());
		Assert.assertNotEquals(salt, user.getSalt());
	}

	@Test(expected = PasswordLengthException.class)
	public void testUpdateUserDetailsWithInvalidPassword() throws Exception {
		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		String password = _FIRST_USER.getPassword();
		String salt = _FIRST_USER.getSalt();

		UserUtil.updateUserDetails(
			"update@test.com", "password", "short", "http://www.ebay.ca/itm/",
			false);

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());
		Assert.assertEquals(
			"http://www.ebay.ca/itm/", user.getPreferredDomain());
		Assert.assertFalse(user.isEmailNotification());
		Assert.assertNotEquals(password, user.getPassword());
		Assert.assertNotEquals(salt, user.getSalt());
	}

	@Test
	public void testUpdateUserDetailsWithNullPasswords()
		throws Exception {

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		String password = _FIRST_USER.getPassword();
		String salt = _FIRST_USER.getSalt();

		UserUtil.updateUserDetails(
			"update@test.com", "", "", "http://www.ebay.ca/itm/", false);

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("update@test.com", user.getEmailAddress());
		Assert.assertEquals(
			"http://www.ebay.ca/itm/", user.getPreferredDomain());
		Assert.assertFalse(user.isEmailNotification());
		Assert.assertEquals(password, user.getPassword());
		Assert.assertEquals(salt, user.getSalt());
	}

	@Test(expected = CredentialsException.class)
	public void testUpdateUserDetailsWithNullCurrentPassword()
		throws Exception {

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserDetails(
			"update@test.com", "currentPassword", "", "http://www.ebay.ca/itm/",
			false);
	}

	@Test(expected = CredentialsException.class)
	public void testUpdateUserDetailsWithNullNewPassword()
		throws Exception {

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserDetails(
			"test@test.com", "", "newPassword", "http://www.ebay.ca/itm/",
			false);
	}

	@Test
	public void testUpdateUserLoginDetails() throws Exception {
		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		Assert.assertNull(_FIRST_USER.getLastLoginDate());
		Assert.assertNull(_FIRST_USER.getLastLoginIpAddress());

		Timestamp date = new Timestamp(System.currentTimeMillis());

		UserUtil.updateUserLoginDetails(date, "127.0.0.1");

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user.getLastLoginDate());
		Assert.assertEquals("127.0.0.1", user.getLastLoginIpAddress());
	}

	@Test
	public void testUpdateUserSubscription() throws Exception {
		Assert.assertNull(_FIRST_USER.getCustomerId());
		Assert.assertNull(_FIRST_USER.getSubscriptionId());
		Assert.assertFalse(_FIRST_USER.isActive());
		Assert.assertFalse(_FIRST_USER.isPendingCancellation());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true, true);

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testValidateCredentials() throws Exception {
		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		validateCredentials.invoke(
			_classInstance, "test@test.com", _FIRST_USER.getPassword(),
			"password", _FIRST_USER.getSalt());
	}

	@Test
	public void testValidateCredentialsWithIncorrectPassword()
		throws Exception {

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		try {
			validateCredentials.invoke(
				_classInstance, "test@test.com", _FIRST_USER.getPassword(),
				"incorrectPassword", _FIRST_USER.getSalt());

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof CredentialsException);
		}
	}

	@Test
	public void testValidateCredentialsWithNullEncryptedPassword()
		throws Exception {

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		try {
			validateCredentials.invoke(
				_classInstance, "test@test.com", "", "password",
				_FIRST_USER.getSalt());

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof CredentialsException);
		}
	}

	@Test
	public void testValidateCredentialsWithNullPassword()
		throws Exception {

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		try {
			validateCredentials.invoke(
				_classInstance, "test@test.com", _FIRST_USER.getPassword(),
				"", _FIRST_USER.getSalt());

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof CredentialsException);
		}
	}

	@Test
	public void testValidateEmailAddress() throws Exception {
		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		validateEmailAddress.invoke(
			_classInstance, _FIRST_USER.getUserId(), "test3@test.com");
	}

	@Test
	public void testValidateEmailAddressWithDuplicateEmailAddress()
		throws Exception {

		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		try {
			validateEmailAddress.invoke(
				_classInstance, _FIRST_USER.getUserId() + 1, "test@test.com");

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof DuplicateEmailAddressException);
		}
	}

	@Test
	public void testValidateEmailAddressWithSameEmailAddress()
		throws Exception {

		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		validateEmailAddress.invoke(
			_classInstance, _FIRST_USER.getUserId(), "test@test.com");
	}

	@Test
	public void testValidateEmailAddressWithInvalidEmailAddress()
		throws Exception {

		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		try {
			validateEmailAddress.invoke(
				_classInstance, _FIRST_USER.getUserId(), "test");

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

	private static Class _clazz;
	private static Object _classInstance;
	private static User _FIRST_USER;
	private static User _SECOND_USER;

}