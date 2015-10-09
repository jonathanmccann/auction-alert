/**
 * Copyright (c) 2015-present Jonathan McCann
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
import com.app.model.SearchQuery;
import com.app.test.BaseTestCase;
import com.app.util.SearchQueryUtil;

import java.sql.SQLException;

import java.util.List;

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
public class SearchQueryUtilTest extends BaseTestCase {

	@After
	public void tearDown() throws Exception {
		SearchQueryUtil.deleteSearchQueries();
	}

	@Test
	public void testIsExceedsTotalNumberOfSearchQueriesAllowed()
		throws Exception {

		Assert.assertFalse(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed());

		SearchQueryUtil.addSearchQuery("First test keywords");
		SearchQueryUtil.addSearchQuery("Second test keywords");

		Assert.assertTrue(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed());
	}

	@Test
	public void testSearchQueryUtil()
		throws DatabaseConnectionException, SQLException {

		// Test add

		SearchQueryUtil.addSearchQuery("First test keywords");
		SearchQueryUtil.addSearchQuery("Second test keywords");
		SearchQueryUtil.addSearchQuery(
			"Third test keywords with category ID", "100");
		SearchQueryUtil.addSearchQuery(
			"Fourth test keywords with category ID", "200");

		// Test get

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(1);

		Assert.assertEquals(
			"First test keywords", searchQuery.getKeywords());

		// Test get multiple

		List<SearchQuery> searchQueries =
			SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(4, searchQueries.size());

		// Test search queries without categories

		SearchQuery firstSearchQuery = searchQueries.get(0);
		SearchQuery secondSearchQuery = searchQueries.get(1);

		Assert.assertEquals(1, firstSearchQuery.getSearchQueryId());
		Assert.assertEquals(2, secondSearchQuery.getSearchQueryId());
		Assert.assertEquals(
			"First test keywords", firstSearchQuery.getKeywords());
		Assert.assertEquals(
			"Second test keywords",
			secondSearchQuery.getKeywords());

		// Test search queries with categories

		SearchQuery thirdSearchQuery = searchQueries.get(2);
		SearchQuery fourthSearchQuery = searchQueries.get(3);

		Assert.assertEquals(3, thirdSearchQuery.getSearchQueryId());
		Assert.assertEquals(4, fourthSearchQuery.getSearchQueryId());
		Assert.assertEquals(
			"Third test keywords with category ID",
			thirdSearchQuery.getKeywords());
		Assert.assertEquals(
			"Fourth test keywords with category ID",
			fourthSearchQuery.getKeywords());
		Assert.assertEquals("100", thirdSearchQuery.getCategoryId());
		Assert.assertEquals("200", fourthSearchQuery.getCategoryId());

		// Test count

		int searchQueryCount = SearchQueryUtil.getSearchQueryCount();

		Assert.assertEquals(4, searchQueryCount);

		// Test update

		SearchQueryUtil.updateSearchQuery(1, "Updated test keywords");
		SearchQueryUtil.updateSearchQuery(
			3, "Updated test keywords with category ID", "300");

		searchQueries = SearchQueryUtil.getSearchQueries();

		firstSearchQuery = searchQueries.get(0);
		thirdSearchQuery = searchQueries.get(2);

		Assert.assertEquals(
			"Updated test keywords",
			firstSearchQuery.getKeywords());
		Assert.assertEquals(null, firstSearchQuery.getCategoryId());

		Assert.assertEquals(
			"Updated test keywords with category ID",
			thirdSearchQuery.getKeywords());
		Assert.assertEquals("300", thirdSearchQuery.getCategoryId());

		// Test delete multiple

		SearchQueryUtil.deleteSearchQuery(1);
		SearchQueryUtil.deleteSearchQuery(2);
		SearchQueryUtil.deleteSearchQuery(3);
		SearchQueryUtil.deleteSearchQuery(4);

		searchQueries = SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(0, searchQueries.size());

		searchQueryCount = SearchQueryUtil.getSearchQueryCount();

		Assert.assertEquals(0, searchQueryCount);
	}

}