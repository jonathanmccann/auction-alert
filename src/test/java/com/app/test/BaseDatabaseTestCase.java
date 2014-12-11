/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.app.test;

import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;

import org.junit.Before;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;

/**
 * @author Jonathan McCann
 */
public abstract class BaseDatabaseTestCase {

	@Before
	public void setUp() throws Exception {
		String databasePassword = System.getProperty(
			PropertiesUtil.DATABASE_PASSWORD);
		String databaseURL = System.getProperty(PropertiesUtil.DATABASE_URL);
		String databaseUsername = System.getProperty(
			PropertiesUtil.DATABASE_USERNAME);

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);

		DatabaseUtil.initializeDatabase();

		Connection connection = DatabaseUtil.getDatabaseConnection();

		Resource resource = new ClassPathResource("/sql/testdb.sql");

		ScriptUtils.executeSqlScript(connection, resource);

		connection.close();

		doSetUp();
	}

	protected abstract void doSetUp() throws Exception;

}