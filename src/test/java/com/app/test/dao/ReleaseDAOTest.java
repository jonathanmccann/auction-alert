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

package com.app.test.dao;

import com.app.dao.impl.ReleaseDAOImpl;
import com.app.exception.DatabaseConnectionException;
import com.app.test.BaseDatabaseTestCase;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class ReleaseDAOTest extends BaseDatabaseTestCase {

	@Override
	public void doSetUp() throws DatabaseConnectionException {
		_releaseDAOImpl = new ReleaseDAOImpl();
	}

	@After
	public void after()
		throws DatabaseConnectionException, SQLException {

		String firstVersion = _releaseDAOImpl.getReleaseVersion(
			"First Release");
		String secondVersion = _releaseDAOImpl.getReleaseVersion(
			"Second Release");

		if (!firstVersion.equals("")) {
			_releaseDAOImpl.deleteRelease("First Release");
		}

		if (!secondVersion.equals("")) {
			_releaseDAOImpl.deleteRelease("Second Release");
		}
	}

	@Test
	public void testCategoryDAO()
		throws DatabaseConnectionException, SQLException {

		// Test add

		_releaseDAOImpl.addRelease("First Release", "100");
		_releaseDAOImpl.addRelease("Second Release", "101");

		// Test get

		String firstVersion = _releaseDAOImpl.getReleaseVersion(
			"First Release");
		String secondVersion = _releaseDAOImpl.getReleaseVersion(
			"Second Release");

		Assert.assertEquals("100", firstVersion);
		Assert.assertEquals("101", secondVersion);

		// Test delete

		_releaseDAOImpl.deleteRelease("First Release");
		_releaseDAOImpl.deleteRelease("Second Release");

		firstVersion = _releaseDAOImpl.getReleaseVersion(
			"First Release");
		secondVersion = _releaseDAOImpl.getReleaseVersion(
			"Second Release");

		Assert.assertEquals("", firstVersion);
		Assert.assertEquals("", secondVersion);
	}

	@Test(expected = SQLException.class)
	public void testDuplicateReleaseName()
		throws DatabaseConnectionException, SQLException {

		_releaseDAOImpl.addRelease("First Release", "100");
		_releaseDAOImpl.addRelease("First Release", "101");
	}

	private static ReleaseDAOImpl _releaseDAOImpl;

}