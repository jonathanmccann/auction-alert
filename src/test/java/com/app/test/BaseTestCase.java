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

package com.app.test;

import com.app.util.DatabaseUtil;
import com.app.util.PropertiesKeys;

import com.app.util.PropertiesUtil;
import com.app.util.UserUtil;
import com.app.util.eBayAPIUtil;

import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URL;

/**
 * @author Jonathan McCann
 */
@PrepareForTest(UserUtil.class)
@RunWith(PowerMockRunner.class)
@WebAppConfiguration
public abstract class BaseTestCase {

	protected static void setUpApiContext() {
		eBayAPIUtil.loadApiContext(
			System.getProperty(PropertiesKeys.EBAY_TOKEN));
	}

	protected static void setUpDatabase() throws Exception {
		String databasePassword = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_PASSWORD);
		String databaseURL = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_URL);
		String databaseUsername = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_USERNAME);

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);

		DatabaseUtil.initializeDatabase(_TEST_DATABASE_PATH);
	}

	protected static void setUpProperties() throws Exception {
		Class<?> clazz = BaseTestCase.class;

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());
	}

	protected static void setUpServiceClient() {
		eBayAPIUtil.loadeBayServiceClient(
			System.getProperty(PropertiesKeys.APPLICATION_ID));
	}

	protected static void setUpUserUtil() throws Exception {
		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			_USER_ID
		).when(
			UserUtil.class, "getCurrentUserId"
		);
	}

	protected static void setUpInvalidUserUtil() throws Exception {
		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			_INCORRECT_USER_ID
		).when(
			UserUtil.class, "getCurrentUserId"
		);
	}

	protected static final int _USER_ID = 1;
	protected static final int _INCORRECT_USER_ID = 2;

	private static final String _TEST_DATABASE_PATH = "/sql/testdb.sql";

}