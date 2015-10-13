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

package com.app.test.util;

import com.app.util.PropertiesKeys;
import com.app.util.PropertiesUtil;
import com.app.util.PropertiesValues;

import java.io.IOException;

import java.net.URL;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class PropertiesUtilTest {

	@Test
	public void testLoadConfigurationProperties() throws IOException {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		Assert.assertEquals("Application ID", PropertiesValues.APPLICATION_ID);
		Assert.assertEquals("eBay Token", PropertiesValues.EBAY_TOKEN);
		Assert.assertEquals(
			"JDBC Default Password", PropertiesValues.JDBC_DEFAULT_PASSWORD);
		Assert.assertEquals(
			"JDBC Default URL", PropertiesValues.JDBC_DEFAULT_URL);
		Assert.assertEquals(
			"JDBC Default Username", PropertiesValues.JDBC_DEFAULT_USERNAME);
		Assert.assertEquals(5, PropertiesValues.NUMBER_OF_SEARCH_RESULTS);
		Assert.assertEquals(
			"test@test.com", PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		Assert.assertEquals(
			"test", PropertiesValues.OUTBOUND_EMAIL_ADDRESS_PASSWORD);
		Assert.assertEquals("true", PropertiesValues.MAIL_SMTP_AUTH);
		Assert.assertEquals("true", PropertiesValues.MAIL_SMTP_STARTTLS_ENABLE);
		Assert.assertEquals("smtp.gmail.com", PropertiesValues.MAIL_SMTP_HOST);
		Assert.assertEquals("587", PropertiesValues.MAIL_SMTP_PORT);
		Assert.assertEquals(
			"test@test.com,test2@test2.com",
			PropertiesValues.RECIPIENT_EMAIL_ADDRESSES);
		Assert.assertEquals(
			"1234567890,2345678901", PropertiesValues.RECIPIENT_PHONE_NUMBERS);
		Assert.assertEquals("AT&T", PropertiesValues.RECIPIENT_PHONE_CARRIER);
		Assert.assertEquals(
			true, PropertiesValues.SEND_NOTIFICATIONS_BASED_ON_TIME);
		Assert.assertEquals(
			1, PropertiesValues.TOTAL_NUMBER_OF_SEARCH_QUERIES_ALLOWED);
	}

	@Test(expected = IOException.class)
	public void testLoadInvalidConfigurationProperties() throws Exception {
		PropertiesUtil.loadConfigurationProperties();
	}

	@Test
	public void testSetConfigurationProperties() throws IOException {
		Properties properties = new Properties();

		properties.setProperty(
			PropertiesKeys.APPLICATION_ID, "Updated Application ID");
		properties.setProperty(
			PropertiesKeys.JDBC_DEFAULT_PASSWORD,
			"Updated JDBC Default Password");
		properties.setProperty(
			PropertiesKeys.JDBC_DEFAULT_URL, "Updated JDBC Default URL");
		properties.setProperty(
			PropertiesKeys.JDBC_DEFAULT_USERNAME,
			"Updated JDBC Default Username");

		PropertiesUtil.setConfigurationProperties(properties);

		properties = PropertiesUtil.getConfigurationProperties();

		Assert.assertEquals(
			"Updated Application ID",
			properties.getProperty(PropertiesKeys.APPLICATION_ID));
		Assert.assertEquals(
			"Updated JDBC Default Password",
			properties.getProperty(PropertiesKeys.JDBC_DEFAULT_PASSWORD));
		Assert.assertEquals(
			"Updated JDBC Default URL",
			properties.getProperty(PropertiesKeys.JDBC_DEFAULT_URL));
		Assert.assertEquals(
			"Updated JDBC Default Username",
			properties.getProperty(PropertiesKeys.JDBC_DEFAULT_USERNAME));
	}

}