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

package com.app.test;

import com.app.util.DatabaseUtil;
import com.app.util.PropertiesKeys;

import org.junit.Before;

/**
 * @author Jonathan McCann
 */
public abstract class BaseDatabaseTestCase {

	@Before
	public void setUp() throws Exception {
		if (!_isInitialized) {
			String databasePassword = System.getProperty(
				PropertiesKeys.JDBC_DEFAULT_PASSWORD);
			String databaseURL = System.getProperty(
				PropertiesKeys.JDBC_DEFAULT_URL);
			String databaseUsername = System.getProperty(
				PropertiesKeys.JDBC_DEFAULT_USERNAME);

			DatabaseUtil.setDatabaseProperties(
				databaseURL, databaseUsername, databasePassword);

			DatabaseUtil.initializeDatabase(_TEST_DATABASE_PATH);

			_isInitialized = true;
		}

		doSetUp();
	}

	protected void doSetUp() throws Exception {
	}

	private static final String _TEST_DATABASE_PATH = "/sql/testdb.sql";

	private static boolean _isInitialized;
}