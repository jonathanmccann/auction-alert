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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
public class DatabaseUtilTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(DatabaseUtil.class.getName());
	}

	@Test
	public void testGetDatabaseConnection() throws Exception {
		setUpDatabase();

		DatabaseUtil.getDatabaseConnection();
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetDatabaseConnectionWithoutPropertiesSet()
		throws Exception {

		Field field = _clazz.getDeclaredField("_isPropertiesSet");

		field.setAccessible(true);

		field.set(_clazz, false);

		DatabaseUtil.getDatabaseConnection();
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetInvalidDatabaseConnection()
		throws DatabaseConnectionException {

		DatabaseUtil.setDatabaseProperties("test", "test", "test");

		DatabaseUtil.getDatabaseConnection();
	}

	@Test
	public void testInitializeDatabase() throws Exception {
		setUpDatabase();

		String version = ReleaseUtil.getReleaseVersion(_APPLICATION_RELEASE_NAME);

		Assert.assertEquals("", version);

		setUpDatabaseUtil();

		DatabaseUtil.initializeDatabase();

		PowerMockito.doCallRealMethod().when(
			ReleaseUtil.class, "getReleaseVersion", Mockito.anyString()
		);

		version = ReleaseUtil.getReleaseVersion(_APPLICATION_RELEASE_NAME);

		Assert.assertEquals(_APPLICATION_VERSION, version);
	}

	@Test
	public void testInitializeDatabaseWithData() throws Exception {
		setUpDatabase();

		ReleaseUtil.addRelease(_APPLICATION_RELEASE_NAME, "2");

		DatabaseUtil.initializeDatabase();

		String version = ReleaseUtil.getReleaseVersion(_APPLICATION_RELEASE_NAME);

		Assert.assertEquals("2", version);
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

		Assert.assertNull(databasePassword.get(DatabaseUtil.class));
		Assert.assertNull(databaseURL.get(DatabaseUtil.class));
		Assert.assertNull(databaseUsername.get(DatabaseUtil.class));
		Assert.assertFalse((boolean)isPropertiesSet.get(DatabaseUtil.class));

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