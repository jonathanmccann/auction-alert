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

package com.app.test.exception;

import com.app.exception.DatabaseConnectionException;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class DatabaseConnectionExceptionTest {

	@Test
	public void testDatabaseConnectionException() {
		try {
			throw new DatabaseConnectionException();
		}
		catch (DatabaseConnectionException dce) {
			Assert.assertEquals(
				DatabaseConnectionException.class, dce.getClass());
		}
	}

	@Test
	public void testDatabaseConnectionExceptionWithMessage() {
		try {
			throw new DatabaseConnectionException("Error message");
		}
		catch (DatabaseConnectionException dce) {
			Assert.assertEquals(
				DatabaseConnectionException.class, dce.getClass());

			Assert.assertEquals("Error message", dce.getMessage());
		}
	}

	@Test
	public void testDatabaseConnectionExceptionWithMessageAndCause() {
		try {
			throw new DatabaseConnectionException(
				"Error message", new SQLException());
		}
		catch (DatabaseConnectionException dce) {
			Assert.assertEquals(
				DatabaseConnectionException.class, dce.getClass());

			Assert.assertEquals("Error message", dce.getMessage());

			Assert.assertEquals(SQLException.class, dce.getCause().getClass());
		}
	}

	@Test
	public void testDatabaseConnectionExceptionWithCause() {
		try {
			throw new DatabaseConnectionException(new SQLException());
		}
		catch (DatabaseConnectionException dce) {
			Assert.assertEquals(
				DatabaseConnectionException.class, dce.getClass());

			Assert.assertEquals(SQLException.class, dce.getCause().getClass());
		}
	}
}