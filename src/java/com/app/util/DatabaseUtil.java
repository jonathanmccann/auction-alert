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

		if (!_isPropertiesSet) {
			_log.error("The database properties are not set");

			throw new DatabaseConnectionException();
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");

			return DriverManager.getConnection(
				_databaseURL, _databaseUsername, _databasePassword);
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

		_databaseURL = properties.getProperty(PropertiesUtil.DATABASE_URL);
		_databasePassword = properties.getProperty(
			PropertiesUtil.DATABASE_PASSWORD);
		_databaseUsername = properties.getProperty(
			PropertiesUtil.DATABASE_USERNAME);

		_isPropertiesSet = true;
	}

	public static void setDatabaseProperties(
		String databaseURL, String databaseUsername, String databasePassword) {

		_databasePassword = databasePassword;
		_databaseURL = databaseURL;
		_databaseUsername = databaseUsername;

		_isPropertiesSet = true;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		DatabaseUtil.class);

	private static String _databasePassword;
	private static String _databaseURL;
	private static String _databaseUsername;
	private static boolean _isPropertiesSet;

}