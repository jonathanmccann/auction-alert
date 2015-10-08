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

import com.app.exception.DatabaseConnectionException;
import com.app.test.BaseDatabaseTestCase;
import com.app.util.ReleaseUtil;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class ReleaseUtilTest extends BaseDatabaseTestCase {

	@After
	public void tearDown() throws DatabaseConnectionException, SQLException {
		String firstVersion = ReleaseUtil.getReleaseVersion(
			"First Release");
		String secondVersion = ReleaseUtil.getReleaseVersion(
			"Second Release");

		if (!firstVersion.equals("")) {
			ReleaseUtil.deleteRelease("First Release");
		}

		if (!secondVersion.equals("")) {
			ReleaseUtil.deleteRelease("Second Release");
		}
	}

	@Override
	public void doSetUp() throws DatabaseConnectionException {
	}

	@Test
	public void testReleaseUtil()
		throws DatabaseConnectionException, SQLException {

		// Test add

		ReleaseUtil.addRelease("First Release", "100");
		ReleaseUtil.addRelease("Second Release", "101");

		// Test get

		String firstVersion = ReleaseUtil.getReleaseVersion(
			"First Release");
		String secondVersion = ReleaseUtil.getReleaseVersion(
			"Second Release");

		Assert.assertEquals("100", firstVersion);
		Assert.assertEquals("101", secondVersion);

		// Test delete

		ReleaseUtil.deleteRelease("First Release");
		ReleaseUtil.deleteRelease("Second Release");

		firstVersion = ReleaseUtil.getReleaseVersion("First Release");
		secondVersion = ReleaseUtil.getReleaseVersion("Second Release");

		Assert.assertEquals("", firstVersion);
		Assert.assertEquals("", secondVersion);
	}

	@Test(expected = SQLException.class)
	public void testDuplicateReleaseName()
		throws DatabaseConnectionException, SQLException {

		ReleaseUtil.addRelease("First Release", "100");
		ReleaseUtil.addRelease("First Release", "101");
	}

}