package com.app.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @author Jonathan McCann
 */
public class DatabaseUtil {

	public static Connection getDatabaseConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");

		return DriverManager.getConnection(
			_DATABASE_URL, _DATABASE_USERNAME, _DATABASE_PASSWORD);
	}

	public static void initializeDatabase() throws Exception {
		Resource resource = new ClassPathResource(
			"/sql/defaultdb.sql");

		ScriptUtils.executeSqlScript(getDatabaseConnection(), resource);
	}

	public static void loadDatabaseProperties() {
		Properties properties = PropertiesUtil.getConfigurationProperties();

		_DATABASE_URL = properties.getProperty(PropertiesUtil.DATABASE_URL);
		_DATABASE_PASSWORD =
			properties.getProperty(PropertiesUtil.DATABASE_PASSWORD);
		_DATABASE_USERNAME =
			properties.getProperty(PropertiesUtil.DATABASE_USERNAME);
	}

	public static void setDatabaseProperties(
		String databaseURL, String databaseUsername, String databasePassword) {

		_DATABASE_PASSWORD = databasePassword;
		_DATABASE_URL = databaseURL;
		_DATABASE_USERNAME = databaseUsername;
	}

	private static String _DATABASE_PASSWORD;
	private static String _DATABASE_URL;
	private static String _DATABASE_USERNAME;

}