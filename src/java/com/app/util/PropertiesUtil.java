package com.app.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class PropertiesUtil {

	public static final String APPLICATION_ID = "application.id";

	public static final String DATABASE_PASSWORD = "jdbc.default.password";

	public static final String DATABASE_URL = "jdbc.default.url";

	public static final String DATABASE_USERNAME = "jdbc.default.username";

	public static Properties getConfigurationProperties() {
		return _properties;
	}

	public static void loadConfigurationProperties() throws IOException {
		String propertiesFilePath =
			System.getProperty("catalina.base") + "/" + "config.properties";

		loadConfigurationProperties(propertiesFilePath);
	}

	public static void loadConfigurationProperties(String propertiesFilePath)
		throws IOException {

		_log.debug("Reading properties from {}", propertiesFilePath);

		Properties properties = new Properties();

		try {
			InputStream inputStream = new FileInputStream(propertiesFilePath);

			properties.load(inputStream);
		}
		catch (IOException ioe) {
			_log.error(
				"Cannot find or load properties file: {}", propertiesFilePath);

			throw new IOException();
		}

		_properties = properties;
	}

	public static void setConfigurationProperties(Properties properties) {
		_properties = properties;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PropertiesUtil.class);

	private static Properties _properties;

}