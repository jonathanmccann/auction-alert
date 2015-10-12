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
	public void testAddSearchQueryWithKeywords() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			"First test keywords");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertNull(searchQuery.getCategoryId());
	}

	@Test
	public void testAddSearchQueryWithKeywordsAndCategory() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			"First test keywords", "100");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
	}

	@Test
	public void testDeleteSearchQueries() throws Exception {
		SearchQueryUtil.addSearchQuery("First test keywords");
		SearchQueryUtil.addSearchQuery("Second test keywords");

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(2, searchQueries.size());

		SearchQueryUtil.deleteSearchQueries();

		searchQueries = SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(0, searchQueries.size());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		int firstSearchQueryId = SearchQueryUtil.addSearchQuery(
			"First test keywords");

		SearchQueryUtil.addSearchQuery("Second test keywords");

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(2, searchQueries.size());

		SearchQueryUtil.deleteSearchQuery(firstSearchQueryId);

		searchQueries = SearchQueryUtil.getSearchQueries();

		Assert.assertEquals(1, searchQueries.size());
	}

	@Test(expected = SQLException.class)
	public void testGetNonExistantSearchQuery() throws Exception {
		SearchQueryUtil.getSearchQuery(1);
	}

	@Test
	public void testGetSearchQueryCount() throws Exception {
		SearchQueryUtil.addSearchQuery("First test keywords");
		SearchQueryUtil.addSearchQuery("Second test keywords");

		int numberOfSearchQueries = SearchQueryUtil.getSearchQueryCount();

		Assert.assertEquals(2, numberOfSearchQueries);
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
	public void testUpdateSearchQueryWithKeywords() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			"First test keywords");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertNull(searchQuery.getCategoryId());

		SearchQueryUtil.updateSearchQuery(searchQueryId, "New test keywords");

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("New test keywords", searchQuery.getKeywords());
		Assert.assertNull(searchQuery.getCategoryId());
	}

	@Test
	public void testUpdateSearchQueryWithKeywordsAndCategory()
		throws Exception {

		int searchQueryId = SearchQueryUtil.addSearchQuery(
			"First test keywords", "100");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());

		SearchQueryUtil.updateSearchQuery(
			searchQueryId, "New test keywords", "200");

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("New test keywords", searchQuery.getKeywords());
		Assert.assertEquals("200", searchQuery.getCategoryId());
	}

}