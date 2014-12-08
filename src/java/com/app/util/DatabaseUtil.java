package com.app.util;

import com.app.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

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

		if (!_IS_PROPERTIES_SET) {
			_log.error(
				"The database properties are not set");

			throw new DatabaseConnectionException();
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");

			return DriverManager.getConnection(
				_DATABASE_URL, _DATABASE_USERNAME, _DATABASE_PASSWORD);
		}
		catch (ClassNotFoundException | SQLException exception) {
			_log.error(
				"Could not get a database connection. Please check your " +
					"database settings in 'config.properties'");

			throw new DatabaseConnectionException(exception);
		}
	}

	public static void initializeDatabase() throws DatabaseConnectionException {
		Resource resource = new ClassPathResource("/sql/defaultdb.sql");

		ScriptUtils.executeSqlScript(getDatabaseConnection(), resource);
	}

	public static void loadDatabaseProperties() {
		Properties properties = PropertiesUtil.getConfigurationProperties();

		_DATABASE_URL = properties.getProperty(PropertiesUtil.DATABASE_URL);
		_DATABASE_PASSWORD = properties.getProperty(
			PropertiesUtil.DATABASE_PASSWORD);
		_DATABASE_USERNAME = properties.getProperty(
			PropertiesUtil.DATABASE_USERNAME);

		_IS_PROPERTIES_SET = true;
	}

	public static void setDatabaseProperties(
		String databaseURL, String databaseUsername, String databasePassword) {

		_DATABASE_PASSWORD = databasePassword;
		_DATABASE_URL = databaseURL;
		_DATABASE_USERNAME = databaseUsername;

		_IS_PROPERTIES_SET = true;
	}

	private static boolean _IS_PROPERTIES_SET = false;

	private static String _DATABASE_PASSWORD;

	private static String _DATABASE_URL;

	private static String _DATABASE_USERNAME;

	private static final Logger _log = LoggerFactory.getLogger(
		DatabaseUtil.class);

}