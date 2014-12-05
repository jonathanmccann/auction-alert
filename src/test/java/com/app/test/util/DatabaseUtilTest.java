package com.app.test.util;

import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class DatabaseUtilTest {

	@Test
	public void testGetDatabaseConnection() throws Exception {
		String databasePassword = System.getProperty(
			PropertiesUtil.DATABASE_PASSWORD);
		String databaseURL = System.getProperty(PropertiesUtil.DATABASE_URL);
		String databaseUsername = System.getProperty(
			PropertiesUtil.DATABASE_USERNAME);

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);

		Connection connection = DatabaseUtil.getDatabaseConnection();

		Assert.assertNotNull(connection);
	}

	@Test(expected = SQLException.class)
	public void testGetInvalidDatabaseConnection() throws Exception {
		DatabaseUtil.setDatabaseProperties(
			"test", "test", "test");

		DatabaseUtil.getDatabaseConnection();
	}

}