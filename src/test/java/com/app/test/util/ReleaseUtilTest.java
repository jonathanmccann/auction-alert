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
import com.app.test.BaseTestCase;
import com.app.util.ReleaseUtil;
import com.app.util.ValidatorUtil;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ReleaseUtilTest extends BaseTestCase {

	@After
	public void tearDown() throws DatabaseConnectionException, SQLException {
		ReleaseUtil.deleteReleases();
	}

	@Test
	public void testAddRelease() throws Exception {
		ReleaseUtil.addRelease("Test Release", "1.0");

		Assert.assertEquals(
			"1.0", ReleaseUtil.getReleaseVersion("Test Release"));
	}

	@Test
	public void testDeleteRelease() throws Exception {
		ReleaseUtil.addRelease("Test Release", "1.0");

		Assert.assertEquals(
			"1.0", ReleaseUtil.getReleaseVersion("Test Release"));

		ReleaseUtil.deleteRelease("Test Release");

		Assert.assertTrue(
			ValidatorUtil.isNull(
				ReleaseUtil.getReleaseVersion("Test Release")));
	}

	@Test
	public void testDeleteReleases() throws Exception {
		ReleaseUtil.addRelease("First Test Release", "1.0");
		ReleaseUtil.addRelease("Second Test Release", "2.0");

		Assert.assertEquals(
			"1.0", ReleaseUtil.getReleaseVersion("First Test Release"));
		Assert.assertEquals(
			"2.0", ReleaseUtil.getReleaseVersion("Second Test Release"));

		ReleaseUtil.deleteReleases();

		Assert.assertTrue(
			ValidatorUtil.isNull(
				ReleaseUtil.getReleaseVersion("First Test Release")));
		Assert.assertTrue(
			ValidatorUtil.isNull(
				ReleaseUtil.getReleaseVersion("Second Test Release")));
	}

	@Test(expected = SQLException.class)
	public void testDuplicateReleaseName()
		throws DatabaseConnectionException, SQLException {

		ReleaseUtil.addRelease("First Release", "100");
		ReleaseUtil.addRelease("First Release", "101");
	}

}