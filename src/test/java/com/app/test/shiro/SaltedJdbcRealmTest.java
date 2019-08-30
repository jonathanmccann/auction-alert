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

import com.app.model.User;
import com.app.shiro.SaltedJdbcRealm;
import com.app.util.ConstantsUtil;
import com.app.util.UserUtil;

import com.app.test.BaseTestCase;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SaltedJdbcRealmTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		setUpProperties();

		ConstantsUtil.init();

		Class clazz = Class.forName(SaltedJdbcRealm.class.getName());

		_classInstance = clazz.newInstance();

		_doGetAuthenticationInfo = clazz.getDeclaredMethod(
			"doGetAuthenticationInfo", AuthenticationToken.class);

		_doGetAuthenticationInfo.setAccessible(true);
	}

	@After
	public void tearDown() throws Exception {
		if (_user != null) {
			UserUtil.deleteUserByUserId(_user.getUserId());

			_user = null;
		}

		setUpDatabaseProperties();
	}

	@Test
	public void testDoGetAuthenticationInfo() throws Exception {
		_user = UserUtil.addUser("test@test.com", "password");

		UsernamePasswordToken usernamePasswordToken =
			new UsernamePasswordToken();

		usernamePasswordToken.setUsername("test@test.com");

		AuthenticationInfo authenticationInfo =
			(AuthenticationInfo)_doGetAuthenticationInfo.invoke(
				_classInstance, usernamePasswordToken);

		Assert.assertEquals(
			"test@test.com", authenticationInfo.getPrincipals().toString());

		Assert.assertEquals(
			_user.getPassword(), authenticationInfo.getCredentials());
	}

	@Test
	public void testDoGetAuthenticationInfoWithException() throws Exception {
		setUpInvalidDatabaseProperties();

		UsernamePasswordToken usernamePasswordToken =
			new UsernamePasswordToken();

		usernamePasswordToken.setUsername("test@test.com");

		try {
			_doGetAuthenticationInfo.invoke(
				_classInstance, usernamePasswordToken);
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof AuthenticationException);
		}
	}

	@Test
	public void testDoGetAuthenticationInfoWithNoUser() throws Exception {
		UsernamePasswordToken usernamePasswordToken =
			new UsernamePasswordToken();

		usernamePasswordToken.setUsername("test@test.com");

		AuthenticationInfo authenticationInfo =
			(AuthenticationInfo)_doGetAuthenticationInfo.invoke(
				_classInstance, usernamePasswordToken);

		Assert.assertNull(authenticationInfo);
	}

	@Test
	public void testDoGetAuthenticationInfoWithNullEmailAddress()
		throws Exception {

		UsernamePasswordToken usernamePasswordToken =
			new UsernamePasswordToken();

		usernamePasswordToken.setUsername("");

		AuthenticationInfo authenticationInfo =
			(AuthenticationInfo)_doGetAuthenticationInfo.invoke(
				_classInstance, usernamePasswordToken);

		Assert.assertNull(authenticationInfo);
	}

	private static Object _classInstance;
	private static Method _doGetAuthenticationInfo;
	private static User _user;

}