package com.app.test.util;

import com.app.util.PropertiesUtil;

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
	public void testLoadConfigurationProperties() throws Exception {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		Properties properties = PropertiesUtil.getConfigurationProperties();

		Assert.assertEquals(
			"Application ID",
			properties.getProperty(PropertiesUtil.APPLICATION_ID));
		Assert.assertEquals(
			"JDBC Default Password",
			properties.getProperty(PropertiesUtil.DATABASE_PASSWORD));
		Assert.assertEquals(
			"JDBC Default URL",
			properties.getProperty(PropertiesUtil.DATABASE_URL));
		Assert.assertEquals(
			"JDBC Default Username",
			properties.getProperty(PropertiesUtil.DATABASE_USERNAME));
	}

	@Test
	public void testSetConfigurationProperties() throws Exception {
		Properties properties = new Properties();

		properties.setProperty(
			PropertiesUtil.APPLICATION_ID, "Updated Application ID");
		properties.setProperty(
			PropertiesUtil.DATABASE_PASSWORD, "Updated JDBC Default Password");
		properties.setProperty(
			PropertiesUtil.DATABASE_URL, "Updated JDBC Default URL");
		properties.setProperty(
			PropertiesUtil.DATABASE_USERNAME, "Updated JDBC Default Username");

		PropertiesUtil.setConfigurationProperties(properties);

		properties = PropertiesUtil.getConfigurationProperties();

		Assert.assertEquals(
			"Updated Application ID",
			properties.getProperty(PropertiesUtil.APPLICATION_ID));
		Assert.assertEquals(
			"Updated JDBC Default Password",
			properties.getProperty(PropertiesUtil.DATABASE_PASSWORD));
		Assert.assertEquals(
			"Updated JDBC Default URL",
			properties.getProperty(PropertiesUtil.DATABASE_URL));
		Assert.assertEquals(
			"Updated JDBC Default Username",
			properties.getProperty(PropertiesUtil.DATABASE_USERNAME));
	}

	@Test(expected = IOException.class)
	public void testLoadInvalidConfigurationProperties() throws Exception {
		PropertiesUtil.loadConfigurationProperties();
	}

}