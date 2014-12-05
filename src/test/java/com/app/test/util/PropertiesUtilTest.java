package com.app.test.util;

import com.app.util.PropertiesUtil;

import java.io.IOException;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class PropertiesUtilTest {

	@Test
	public void testLoadAndGetConfigurationProperties() throws Exception {
		PropertiesUtil.loadConfigurationProperties(
			getClass().getResource("/config.properties").getPath());

		Properties properties = PropertiesUtil.getConfigurationProperties();

		Assert.assertEquals(
			"", properties.getProperty(PropertiesUtil.APPLICATION_ID));
		Assert.assertEquals(
			"", properties.getProperty(PropertiesUtil.DATABASE_PASSWORD));
		Assert.assertEquals(
			"", properties.getProperty(PropertiesUtil.DATABASE_URL));
		Assert.assertEquals(
			"", properties.getProperty(PropertiesUtil.DATABASE_USERNAME));

		String applicationId = System.getProperty(
			PropertiesUtil.APPLICATION_ID);
		String databasePassword = System.getProperty(
			PropertiesUtil.DATABASE_PASSWORD);
		String databaseURL = System.getProperty(PropertiesUtil.DATABASE_URL);
		String databaseUsername = System.getProperty(
			PropertiesUtil.DATABASE_USERNAME);

		properties.setProperty(PropertiesUtil.APPLICATION_ID, applicationId);
		properties.setProperty(
			PropertiesUtil.DATABASE_PASSWORD, databasePassword);
		properties.setProperty(PropertiesUtil.DATABASE_URL, databaseURL);
		properties.setProperty(
			PropertiesUtil.DATABASE_USERNAME, databaseUsername);

		Assert.assertEquals(
			applicationId,
			properties.getProperty(PropertiesUtil.APPLICATION_ID));
		Assert.assertEquals(
			databasePassword,
			properties.getProperty(PropertiesUtil.DATABASE_PASSWORD));
		Assert.assertEquals(
			databaseURL, properties.getProperty(PropertiesUtil.DATABASE_URL));
		Assert.assertEquals(
			databaseUsername,
			properties.getProperty(PropertiesUtil.DATABASE_USERNAME));
	}

	@Test(expected = IOException.class)
	public void testLoadInvalidConfigurationProperties() throws Exception {
		PropertiesUtil.loadConfigurationProperties();
	}

}