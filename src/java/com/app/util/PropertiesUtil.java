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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class PropertiesUtil {

	public static Properties getConfigurationProperties() {
		return _properties;
	}

	public static String getConfigurationProperty(String propertyKey) {
		return _properties.getProperty(propertyKey);
	}

	public static void loadConfigurationProperties() throws Exception {
		ClassLoader classLoader =
			Thread.currentThread().getContextClassLoader();

		URL resource = classLoader.getResource(_CONFIG_PROPERTIES);

		if (resource == null) {
			throw new IOException(
				"Unable to open resource '" + _CONFIG_PROPERTIES + "'");
		}

		loadConfigurationProperties(Paths.get(resource.toURI()).toString());
	}

	public static void loadConfigurationProperties(String propertiesFilePath)
		throws IOException {

		_log.info("Reading properties from {}", propertiesFilePath);

		Properties properties = new Properties();

		try (InputStream inputStream =
				new FileInputStream(propertiesFilePath)) {

			properties.load(inputStream);
		}

		_properties = properties;
	}

	public static void setConfigurationProperties(Properties properties) {
		_properties = properties;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PropertiesUtil.class);

	private static final String _CONFIG_PROPERTIES = "config.properties";

	private static Properties _properties;

}