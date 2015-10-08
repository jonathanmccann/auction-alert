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
import com.app.model.SearchQueryModel;
import com.app.test.BaseDatabaseTestCase;
import com.app.util.PropertiesUtil;
import com.app.util.SearchQueryUtil;

import java.net.URL;

import java.sql.SQLException;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchQueryUtilTest extends BaseDatabaseTestCase {

	@Before
	public void doSetUp() throws Exception {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());
	}

	@After
	public void tearDown() throws Exception {
		SearchQueryUtil.deleteSearchQueries();
	}

	@Test
	public void testIsExceedsTotalNumberOfSearchQueriesAllowed()
		throws Exception {

		Assert.assertFalse(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed());

		SearchQueryUtil.addSearchQuery("First test search query");
		SearchQueryUtil.addSearchQuery("Second test search query");

		Assert.assertTrue(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed());
	}

	@Test
	public void testSearchQueryUtil()
		throws DatabaseConnectionException, SQLException {

		// Test add

		SearchQueryUtil.addSearchQuery("First test search query");
		SearchQueryUtil.addSearchQuery("Second test search query");
		SearchQueryUtil.addSearchQuery(
			"Third test search query with category ID", "100");
		SearchQueryUtil.addSearchQuery(
			"Fourth test search query with category ID", "200");

		// Test get

		String searchQuery = SearchQueryUtil.getSearchQuery(1);

		Assert.assertEquals("First test search query", searchQuery);

		// Test get multiple

		List<SearchQueryModel> searchQueryModels =
			SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(4, searchQueryModels.size());

		// Test search queries without categories

		SearchQueryModel firstSearchQueryModel = searchQueryModels.get(0);
		SearchQueryModel secondSearchQueryModel = searchQueryModels.get(1);

		Assert.assertEquals(1, firstSearchQueryModel.getSearchQueryId());
		Assert.assertEquals(2, secondSearchQueryModel.getSearchQueryId());
		Assert.assertEquals(
			"First test search query", firstSearchQueryModel.getSearchQuery());
		Assert.assertEquals(
			"Second test search query",
			secondSearchQueryModel.getSearchQuery());

		// Test search queries with categories

		SearchQueryModel thirdSearchQueryModel = searchQueryModels.get(2);
		SearchQueryModel fourthSearchQueryModel = searchQueryModels.get(3);

		Assert.assertEquals(3, thirdSearchQueryModel.getSearchQueryId());
		Assert.assertEquals(4, fourthSearchQueryModel.getSearchQueryId());
		Assert.assertEquals(
			"Third test search query with category ID",
			thirdSearchQueryModel.getSearchQuery());
		Assert.assertEquals(
			"Fourth test search query with category ID",
			fourthSearchQueryModel.getSearchQuery());
		Assert.assertEquals("100", thirdSearchQueryModel.getCategoryId());
		Assert.assertEquals("200", fourthSearchQueryModel.getCategoryId());

		// Test count

		int searchQueryCount = SearchQueryUtil.getSearchQueryCount();

		Assert.assertEquals(4, searchQueryCount);

		// Test update

		SearchQueryUtil.updateSearchQuery(1, "Updated test search query");
		SearchQueryUtil.updateSearchQuery(
			3, "Updated test search query with category ID", "300");

		searchQueryModels = SearchQueryUtil.getSearchQueries();

		firstSearchQueryModel = searchQueryModels.get(0);
		thirdSearchQueryModel = searchQueryModels.get(2);

		Assert.assertEquals(
			"Updated test search query",
			firstSearchQueryModel.getSearchQuery());
		Assert.assertEquals(null, firstSearchQueryModel.getCategoryId());

		Assert.assertEquals(
			"Updated test search query with category ID",
			thirdSearchQueryModel.getSearchQuery());
		Assert.assertEquals("300", thirdSearchQueryModel.getCategoryId());

		// Test delete multiple

		SearchQueryUtil.deleteSearchQuery(1);
		SearchQueryUtil.deleteSearchQuery(2);
		SearchQueryUtil.deleteSearchQuery(3);
		SearchQueryUtil.deleteSearchQuery(4);

		searchQueryModels = SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(0, searchQueryModels.size());

		searchQueryCount = SearchQueryUtil.getSearchQueryCount();

		Assert.assertEquals(0, searchQueryCount);
	}

}