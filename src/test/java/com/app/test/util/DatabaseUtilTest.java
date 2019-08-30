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

import com.app.exception.DatabaseConnectionException;
import com.app.test.BaseTestCase;
import com.app.util.DatabaseUtil;

import java.lang.reflect.Field;

import com.app.util.ReleaseUtil;
import org.junit.After;
import org.junit.Assert;
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
public class DatabaseUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		_clazz = Class.forName(DatabaseUtil.class.getName());
	}

	@After
	public void tearDown() throws Exception {
		setUpDatabaseProperties();
	}

	@Test
	public void testGetDatabaseConnection() throws Exception {
		DatabaseUtil.getDatabaseConnection();
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetDatabaseConnectionWithoutPropertiesSet()
		throws Exception {

		Field field = _clazz.getDeclaredField("_isPropertiesSet");

		field.setAccessible(true);

		field.set(_clazz, false);

		DatabaseUtil.getDatabaseConnection();

		field.set(_clazz, true);
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetInvalidDatabaseConnection()
		throws DatabaseConnectionException {

		DatabaseUtil.setDatabaseProperties("test", "test", "test");

		DatabaseUtil.getDatabaseConnection();
	}

	@Test
	public void testInitializeDatabaseWithRelease() throws Exception {
		ReleaseUtil.addRelease(
			_APPLICATION_RELEASE_NAME, _APPLICATION_VERSION + 1);

		DatabaseUtil.initializeDatabase();

		String version = ReleaseUtil.getReleaseVersion(
			_APPLICATION_RELEASE_NAME);

		Assert.assertEquals(_APPLICATION_VERSION + 1, version);
	}

	@Test
	public void testLoadDatabaseProperties() throws Exception {
		setUpProperties();

		Field databasePassword = _clazz.getDeclaredField("_databasePassword");
		Field databaseURL = _clazz.getDeclaredField("_databaseURL");
		Field databaseUsername = _clazz.getDeclaredField("_databaseUsername");
		Field isPropertiesSet = _clazz.getDeclaredField("_isPropertiesSet");

		databasePassword.setAccessible(true);
		databaseURL.setAccessible(true);
		databaseUsername.setAccessible(true);
		isPropertiesSet.setAccessible(true);

		DatabaseUtil.loadDatabaseProperties();

		Assert.assertEquals(
			"JDBC Default Password", databasePassword.get(DatabaseUtil.class));
		Assert.assertEquals(
			"JDBC Default URL", databaseURL.get(DatabaseUtil.class));
		Assert.assertEquals(
			"JDBC Default Username", databaseUsername.get(DatabaseUtil.class));
		Assert.assertTrue((boolean)isPropertiesSet.get(DatabaseUtil.class));
	}

	private static Class _clazz;

	private static final String _APPLICATION_RELEASE_NAME = "application";

	private static final String _APPLICATION_VERSION = "1";

}