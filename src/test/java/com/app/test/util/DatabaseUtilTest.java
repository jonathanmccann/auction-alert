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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class DatabaseUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(DatabaseUtil.class.getName());
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

	private static Class _clazz;

}