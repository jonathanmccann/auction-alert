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
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesKeys;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class DatabaseUtilTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(DatabaseUtil.class.getName());
	}

	@After
	public void tearDown() {
		setDatabaseProperties();
	}

	@Test
	public void testGetDatabaseConnection()
		throws DatabaseConnectionException, SQLException {

		setDatabaseProperties();

		Connection connection = DatabaseUtil.getDatabaseConnection();

		Assert.assertNotNull(connection);
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

	private static void setDatabaseProperties() {
		String databasePassword = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_PASSWORD);
		String databaseURL = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_URL);
		String databaseUsername = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_USERNAME);

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);
	}

	private static Class _clazz;

}