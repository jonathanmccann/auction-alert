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
import com.app.util.SearchQueryPreviousResultUtil;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SearchQueryPreviousResultUtilTest extends BaseDatabaseTestCase {

	@Test
	public void testSearchQueryPreviousResultUtil()
		throws DatabaseConnectionException, SQLException {

		// Test add

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			1, "1234");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			1, "2345");
		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			1, "3456");

		// Test get

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		List<String> expectedSearchQueryPreviousResults = new ArrayList<>();

		expectedSearchQueryPreviousResults.add("1234");
		expectedSearchQueryPreviousResults.add("2345");
		expectedSearchQueryPreviousResults.add("3456");

		Assert.assertEquals(
			expectedSearchQueryPreviousResults, searchQueryPreviousResults);

		// Test get count

		int searchQueryPreviousResultsCount =
			SearchQueryPreviousResultUtil.
				getSearchQueryPreviousResultsCount(1);

		Assert.assertEquals(3, searchQueryPreviousResultsCount);

		// Test delete oldest entry

		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResult(1);

		searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		Assert.assertEquals(2, searchQueryPreviousResults.size());

		expectedSearchQueryPreviousResults.remove(0);

		Assert.assertEquals(
			expectedSearchQueryPreviousResults, searchQueryPreviousResults);

		// Test delete all entries

		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(1);

		searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		Assert.assertEquals(0, searchQueryPreviousResults.size());
	}

}