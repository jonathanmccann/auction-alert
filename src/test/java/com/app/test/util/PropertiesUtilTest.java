package com.app.test.util;

import com.app.util.PropertiesUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtilTest {

	@Test
	public void testLoadAndGetConfigurationProperties() throws Exception {
		PropertiesUtil.loadConfigurationProperties(
			getClass().getResource("/config.properties").getPath());

		Properties properties = PropertiesUtil.getConfigurationProperties();

		Assert.assertEquals(
			"", properties.getProperty(PropertiesUtil.APPLICATION_ID));

		String applicationId =
			System.getProperty(PropertiesUtil.APPLICATION_ID);

		properties.setProperty(
			PropertiesUtil.APPLICATION_ID, applicationId);

		Assert.assertEquals(
			applicationId,
			properties.getProperty(PropertiesUtil.APPLICATION_ID));
	}

	@Test(expected=IOException.class)
	public void testLoadInvalidConfigurationProperties() throws Exception {
		PropertiesUtil.loadConfigurationProperties();
	}
}