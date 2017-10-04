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

package com.app.test.shiro;

import com.app.shiro.UserSaltedAuthenticationInfo;

import com.app.test.BaseTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class UserSaltedAuthenticationInfoTest extends BaseTestCase {

	@Test
	public void testConstructor() throws Exception {
		UserSaltedAuthenticationInfo userSaltedAuthenticationInfo =
			new UserSaltedAuthenticationInfo(
				"test@test.com", "password", "salt");

		Assert.assertEquals(
			"test@test.com",
			userSaltedAuthenticationInfo.getPrincipals().toString());
		Assert.assertEquals(
			"password", userSaltedAuthenticationInfo.getCredentials());
		Assert.assertEquals(
			"salt",
			userSaltedAuthenticationInfo.getCredentialsSalt().toString());
	}

}