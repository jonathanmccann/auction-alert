package com.app.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static final String APPLICATION_ID = "application.id";

	public static Properties getConfigurationProperties() {
		String propertiesFilePath =
			System.getProperty("catalina.base") + "/" + "config.properties";

		return loadConfigurationProperties(propertiesFilePath);
	}

	public Properties getConfigurationProperties(String propertiesFilePath) {
		return loadConfigurationProperties(propertiesFilePath);
	}

	private static Properties loadConfigurationProperties(
		String propertiesFilePath) {

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

		return properties;
	}
}