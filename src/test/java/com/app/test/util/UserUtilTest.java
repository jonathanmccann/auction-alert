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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.shiro.authc.CredentialsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
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
		setUpProperties();

		ConstantsUtil.init();

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
	public void testDeactivateUser() throws Exception {
		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, true);

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
		UserUtil.addUser("test@test.com", "password");

		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUser("password", user);

		user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNull(user);
	}

	@Test
	public void testDeleteUserByUserId() throws Exception {
		UserUtil.addUser("test@test.com", "password");

		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUserByUserId(user.getUserId());

		user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNull(user);
	}

	@Test(expected = CredentialsException.class)
	public void testDeleteUserWithInvalidCredentials() throws Exception {
		UserUtil.addUser("test@test.com", "password");

		User user = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertNotNull(user);
		Assert.assertEquals("test@test.com", user.getEmailAddress());

		UserUtil.deleteUser("invalidPassword", user);
	}

	@Test
	public void testExceedsMaximumNumberOfSearchQueries() throws Exception {
		setUpProperties();

		Assert.assertFalse(UserUtil.exceedsMaximumNumberOfUsers());

		UserUtil.addUser("test@test.com", "password");
		UserUtil.addUser("test2@test.com", "password");

		Assert.assertTrue(UserUtil.exceedsMaximumNumberOfUsers());
	}

	@Test
	public void testGetCurrentUserId() throws Exception {
		setUpSecurityUtils(true);

		Assert.assertEquals(_USER_ID, UserUtil.getCurrentUserId());
	}

	@Test(expected = NullPointerException.class)
	public void testGetCurrentUserIdWithNullUser() throws Exception {
		setUpNullSecurityUtils();

		UserUtil.getCurrentUserId();
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
			firstUser.getUserId(), firstUser.getCustomerId(),
			firstUser.getSubscriptionId(), true,
			firstUser.isPendingCancellation());

		List<Integer> activeUserIds = UserUtil.getUserIds(true);

		Assert.assertEquals(1, activeUserIds.size());
		Assert.assertEquals(firstUser.getUserId(), (int) activeUserIds.get(0));

		List<Integer> inactiveUserIds = UserUtil.getUserIds(false);

		Assert.assertEquals(1, inactiveUserIds.size());
		Assert.assertEquals(secondUser.getUserId(), (int) inactiveUserIds.get(0));
	}

	@Test
	public void testIsCurrentUserActiveWithAuthenticatedAndActiveUser()
		throws Exception {

		setUpSecurityUtils(true);
		setUpGetCurrentUserId();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		Assert.assertTrue(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testIsCurrentUserActiveWithAuthenticatedAndPendingCancellationUser()
		throws Exception {

		setUpSecurityUtils(true);
		setUpGetCurrentUserId();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", false, true);

		Assert.assertTrue(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testIsCurrentUserActiveWithAuthenticatedAndInactiveUser()
		throws Exception {

		setUpSecurityUtils(true);
		setUpGetCurrentUserId();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", false, false);

		Assert.assertFalse(UserUtil.isCurrentUserActive());
	}

	@Test
	public void testIsCurrentUserActiveWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtils(false);

		Assert.assertFalse(UserUtil.isCurrentUserActive());
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
	public void testResetPassword() throws Exception {
		User user = UserUtil.addUser("test@test.com", "password");

		UserUtil.updatePasswordResetToken(user.getUserId());

		user = UserUtil.getUserByUserId(user.getUserId());

		String encryptedPassword = user.getPassword();

		UserUtil.resetPassword(
			user.getEmailAddress(), "updatedPassword",
			user.getPasswordResetToken());

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotEquals(encryptedPassword, user.getPassword());
	}

	@Test
	public void testResetPasswordAfterExpiration() throws Exception {
		User user = UserUtil.addUser("test@test.com", "password");

		UserUtil.updatePasswordResetToken(user.getUserId());

		user = UserUtil.getUserByUserId(user.getUserId());

		String encryptedPassword = user.getPassword();

		Date date = new Date(0L);

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		user.setPasswordResetExpiration(
			new Timestamp(calendar.getTimeInMillis()));

		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			user
		).when(
			UserUtil.class, "getUserByEmailAddress", "test@test.com"
		);

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
		User user = UserUtil.addUser("test@test.com", "password");

		UserUtil.updatePasswordResetToken(user.getUserId());

		user = UserUtil.getUserByUserId(user.getUserId());

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
		setUpUserUtil();

		User user = UserUtil.addUser("test@test.com", "password");

		String password = user.getPassword();
		String salt = user.getSalt();

		UserUtil.updatePasswordResetToken(_USER_ID);

		UserUtil.updatePassword(_USER_ID, "updatedPassword");

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotEquals(password, user.getPassword());
		Assert.assertNotEquals(salt, user.getSalt());
		Assert.assertNull(user.getPasswordResetToken());
	}

	@Test(expected = PasswordLengthException.class)
	public void testUpdatePasswordWithInvalidPassword() throws Exception {
		setUpUserUtil();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updatePassword(_USER_ID, "test");
	}

	@Test
	public void testUpdatePasswordResetToken() throws Exception {
		setUpUserUtil();

		User user = UserUtil.addUser("test@test.com", "password");

		Assert.assertNull(user.getPasswordResetToken());
		Assert.assertNull(user.getPasswordResetExpiration());

		UserUtil.updatePasswordResetToken(_USER_ID);

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getPasswordResetToken());
		Assert.assertNotNull(user.getPasswordResetExpiration());
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

		User user = UserUtil.addUser("test@test.com", "password");

		String password = user.getPassword();
		String salt = user.getSalt();

		UserUtil.updateUserDetails(
			"update@test.com", "password", "newPassword",
			"http://www.ebay.ca/itm/", false);

		user = UserUtil.getUserByUserId(user.getUserId());

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
		setUpUserUtil();

		User user = UserUtil.addUser("test@test.com", "password");

		String password = user.getPassword();
		String salt = user.getSalt();

		UserUtil.updateUserDetails(
			"update@test.com", "password", "short", "http://www.ebay.ca/itm/",
			false);

		user = UserUtil.getUserByUserId(user.getUserId());

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

		setUpUserUtil();

		User user = UserUtil.addUser("test@test.com", "password");

		String password = user.getPassword();
		String salt = user.getSalt();

		UserUtil.updateUserDetails(
			"update@test.com", "", "", "http://www.ebay.ca/itm/", false);

		user = UserUtil.getUserByUserId(user.getUserId());

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

		setUpUserUtil();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserDetails(
			"update@test.com", "currentPassword", "", "http://www.ebay.ca/itm/",
			false);
	}

	@Test(expected = CredentialsException.class)
	public void testUpdateUserDetailsWithNullNewPassword()
		throws Exception {

		setUpUserUtil();

		UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserDetails(
			"update@test.com", "", "newPassword", "http://www.ebay.ca/itm/",
			false);
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
			user.getUserId(), "customerId", "subscriptionId", true, true);

		user = UserUtil.getUserByUserId(user.getUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testValidateCredentials() throws Exception {
		User user = UserUtil.addUser("test@test.com", "password");

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		validateCredentials.invoke(
			_classInstance, "test@test.com", user.getPassword(), "password",
			user.getSalt());
	}

	@Test
	public void testValidateCredentialsWithIncorrectPassword()
		throws Exception {

		User user = UserUtil.addUser("test@test.com", "password");

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		try {
			validateCredentials.invoke(
				_classInstance, "test@test.com", user.getPassword(),
				"incorrectPassword", user.getSalt());

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

		User user = UserUtil.addUser("test@test.com", "password");

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		try {
			validateCredentials.invoke(
				_classInstance, "test@test.com", "",
				"password", user.getSalt());

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

		User user = UserUtil.addUser("test@test.com", "password");

		Method validateCredentials = _clazz.getDeclaredMethod(
			"_validateCredentials", String.class, String.class, String.class,
			String.class);

		validateCredentials.setAccessible(true);

		try {
			validateCredentials.invoke(
				_classInstance, "test@test.com", user.getPassword(),
				"", user.getSalt());

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
	public void testValidateEmailAddressWithSameEmailAddress()
		throws Exception {

		UserUtil.addUser("test@test.com", "password");

		Method validateEmailAddress = _clazz.getDeclaredMethod(
			"_validateEmailAddress", int.class, String.class);

		validateEmailAddress.setAccessible(true);

		validateEmailAddress.invoke(_classInstance, _USER_ID, "test@test.com");
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

	private static final Pattern _BASE_64_PATTERN = Pattern.compile(
		"^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|" +
			"[A-Za-z0-9+/]{2}==)$");

}