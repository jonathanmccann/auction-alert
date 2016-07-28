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
import com.app.util.PropertiesUtil;
import com.app.util.UserUtil;
import com.app.util.EbayAPIUtil;

import java.net.URL;

import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@PrepareForTest(UserUtil.class)
@RunWith(PowerMockRunner.class)
@WebAppConfiguration
public abstract class BaseTestCase {

	protected static void setUpApiContext() {
		EbayAPIUtil.loadApiContext("ebay.token");
	}

	protected static void setUpDatabase() throws Exception {
		String databasePassword = System.getProperty("jdbc.default.password");
		String databaseURL = System.getProperty("jdbc.default.url");
		String databaseUsername = System.getProperty("jdbc.default.username");

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);

		Resource resource = new ClassPathResource(_TEST_DATABASE_PATH);

		ScriptUtils.executeSqlScript(
			DatabaseUtil.getDatabaseConnection(), resource);
	}

	protected static void setUpInvalidUserUtil() throws Exception {
		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			_INVALID_USER_ID
		).when(
			UserUtil.class, "getCurrentUserId"
		);
	}

	protected static void setUpProperties() throws Exception {
		Class<?> clazz = BaseTestCase.class;

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());
	}

	protected static void setUpServiceClient() {
		EbayAPIUtil.loadEbayServiceClient(System.getProperty("application.id"));
	}

	protected static void setUpUserUtil() throws Exception {
		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			_USER_ID
		).when(
			UserUtil.class, "getCurrentUserId"
		);
	}

	protected static final int _INVALID_USER_ID = 2;

	protected static final int _USER_ID = 1;

	private static final String _TEST_DATABASE_PATH = "/sql/testdb.sql";

}