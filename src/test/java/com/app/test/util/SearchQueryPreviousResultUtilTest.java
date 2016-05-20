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

import com.app.test.BaseTestCase;
import com.app.util.SearchQueryPreviousResultUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchQueryPreviousResultUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
	}

	@After
	public void tearDown() throws Exception {
		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(1);
	}

	@Test
	public void testAddSearchQueryPreviousResult() throws Exception {
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "1234");

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		Assert.assertEquals(1, searchQueryPreviousResults.size());
	}

	@Test
	public void testDeleteSearchQueryPreviousResult() throws Exception {
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "1234");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "2345");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "3456");

		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResult(1);

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		Assert.assertEquals(2, searchQueryPreviousResults.size());

		String firstSearchQueryPreviousResult = searchQueryPreviousResults.get(
			0);

		String secondSearchQueryPreviousResult = searchQueryPreviousResults.get(
			1);

		Assert.assertEquals("2345", firstSearchQueryPreviousResult);
		Assert.assertEquals("3456", secondSearchQueryPreviousResult);
	}

	@Test
	public void testDeleteSearchQueryPreviousResults() throws Exception {
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "1234");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "2345");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "3456");

		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(1);

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		Assert.assertEquals(0, searchQueryPreviousResults.size());
	}

	@Test
	public void testGetSearchQueryPreviousResultsCount() throws Exception {
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "1234");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "2345");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(1, "3456");

		int numberOfSearchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(1);

		Assert.assertEquals(3, numberOfSearchQueryPreviousResults);
	}

}