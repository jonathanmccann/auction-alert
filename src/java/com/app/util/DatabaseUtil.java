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

package com.app.util;

import com.app.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * @author Jonathan McCann
 */
public class DatabaseUtil {

	public static Connection getDatabaseConnection()
		throws DatabaseConnectionException {

		if (!_isPropertiesSet) {
			_log.error("The database properties are not set");

			throw new DatabaseConnectionException();
		}

		try {
			return DriverManager.getConnection(
				_databaseURL, _databaseUsername, _databasePassword);
		}
		catch (SQLException exception) {
			_log.error(
				"Could not get a database connection. Please check your " +
					"database settings in 'config.properties'");

			throw new DatabaseConnectionException(exception);
		}
	}

	public static void initializeDatabase() throws DatabaseConnectionException {
		initializeDatabase(_DEFAULT_DATABASE_PATH);
	}

	public static void initializeDatabase(String path)
		throws DatabaseConnectionException {

		Resource resource = new ClassPathResource(path);

		ScriptUtils.executeSqlScript(getDatabaseConnection(), resource);
	}

	public static void loadDatabaseProperties() {
		_databaseURL = PropertiesValues.JDBC_DEFAULT_URL;
		_databasePassword = PropertiesValues.JDBC_DEFAULT_PASSWORD;
		_databaseUsername = PropertiesValues.JDBC_DEFAULT_USERNAME;

		_isPropertiesSet = true;
	}

	public static void setDatabaseProperties(
		String databaseURL, String databaseUsername, String databasePassword) {

		_databasePassword = databasePassword;
		_databaseURL = databaseURL;
		_databaseUsername = databaseUsername;

		_isPropertiesSet = true;
	}

	private static final String _DEFAULT_DATABASE_PATH = "/sql/defaultdb.sql";

	private static final Logger _log = LoggerFactory.getLogger(
		DatabaseUtil.class);

	private static String _databasePassword;
	private static String _databaseURL;
	private static String _databaseUsername;
	private static boolean _isPropertiesSet;

}