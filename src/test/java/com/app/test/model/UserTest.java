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

package com.app.test.model;

import com.app.model.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * @author Jonathan McCann
 */
public class UserTest {

	@Before
	public void setUp() {
		_user = new User();
	}

	@Test
	public void testConstructor() {
		User user = new User(1, "test@test.com", "password", "salt", true);

		Assert.assertEquals(1, user.getUserId());
		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertEquals("password", user.getPassword());
		Assert.assertEquals("salt", user.getSalt());
		Assert.assertTrue(user.isEmailNotification());
	}

	@Test
	public void testIsActive() {
		_user.setActive(true);

		Assert.assertTrue(_user.isActive());
	}

	@Test
	public void testIsEmailNotification() {
		_user.setEmailNotification(true);

		Assert.assertTrue(_user.isEmailNotification());
	}

	@Test
	public void testIsPendingCancellation() {
		_user.setPendingCancellation(true);

		Assert.assertTrue(_user.isPendingCancellation());
	}

	@Test
	public void testSetAndGetCurrentPassword() {
		_user.setCurrentPassword("currentPassword");

		Assert.assertEquals("currentPassword", _user.getCurrentPassword());
	}

	@Test
	public void testSetAndGetCustomerId() {
		_user.setCustomerId("customerId");

		Assert.assertEquals("customerId", _user.getCustomerId());
	}

	@Test
	public void testSetAndGetEmailAddress() {
		_user.setEmailAddress("test@test.com");

		Assert.assertEquals("test@test.com", _user.getEmailAddress());
	}

	@Test
	public void testSetAndGetEmailsSent() {
		_user.setEmailsSent(10);

		Assert.assertEquals(10, _user.getEmailsSent());
	}

	@Test
	public void testSetAndGetLastLoginDate() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		_user.setLastLoginDate(timestamp);

		Assert.assertEquals(timestamp, _user.getLastLoginDate());
	}

	@Test
	public void testSetAndGetLastLoginIpAddress() {
		_user.setLastLoginIpAddress("127.0.0.1");

		Assert.assertEquals("127.0.0.1", _user.getLastLoginIpAddress());
	}

	@Test
	public void testSetAndGetNewPassword() {
		_user.setNewPassword("newPassword");

		Assert.assertEquals("newPassword", _user.getNewPassword());
	}

	@Test
	public void testSetAndGetPassword() {
		_user.setPassword("password");

		Assert.assertEquals("password", _user.getPassword());
	}

	@Test
	public void testSetAndGetPasswordResetExpiration() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		_user.setPasswordResetExpiration(timestamp);

		Assert.assertEquals(timestamp, _user.getPasswordResetExpiration());
	}

	@Test
	public void testSetAndGetPasswordResetToken() {
		_user.setPasswordResetToken("passwordResetToken");

		Assert.assertEquals("passwordResetToken", _user.getPasswordResetToken());
	}

	@Test
	public void testSetAndGetPreferredDomain() {
		_user.setPreferredDomain("http://www.ebay.com/itm/");

		Assert.assertEquals("http://www.ebay.com/itm/", _user.getPreferredDomain());
	}

	@Test
	public void testSetAndGetSalt() {
		_user.setSalt("salt");

		Assert.assertEquals("salt", _user.getSalt());
	}

	@Test
	public void testGetSubscriptionId() {
		_user.setSubscriptionId("subscriptionId");

		Assert.assertEquals("subscriptionId", _user.getSubscriptionId());
	}

	@Test
	public void testGetAndSetUnsubscribeToken() {
		_user.setUnsubscribeToken("unsubscribeToken");

		Assert.assertEquals("unsubscribeToken", _user.getUnsubscribeToken());
	}

	@Test
	public void testGetAndSetUserId() {
		_user.setUserId(1);

		Assert.assertEquals(1, _user.getUserId());
	}

	private static User _user;

}