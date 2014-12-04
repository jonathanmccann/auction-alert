package com.app.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static final String APPLICATION_ID = "application.id";

	public static Properties getConfigurationProperties() {
		return _properties;
	}

	public static void loadConfigurationProperties() {
		String propertiesFilePath =
			System.getProperty("catalina.base") + "/" + "config.properties";

		loadConfigurationProperties(propertiesFilePath);
	}

	public static void loadConfigurationProperties(String propertiesFilePath) {
		Properties properties = new Properties();

		try {
			InputStream inputStream = new FileInputStream(propertiesFilePath);

			if (inputStream == null) {
				throw new FileNotFoundException();
			}

			properties.load(inputStream);
		}
		catch (IOException ioe) {
			System.out.println(
				"Cannot find or load properties file: " + propertiesFilePath);
		}

		_properties = properties;
	}

	private static Properties _properties;
}