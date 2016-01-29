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
		SearchQueryUtil.deleteSearchQueries(_USER_ID);
	}

	@Test
	public void testAddSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", true, true, true, true, true,
			true, true, 5.00, 10.00);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(1, searchQuery.getSearchQueryId());
		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
	}

	@Test
	public void testAddSearchQueryWithKeywords() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertNull(searchQuery.getCategoryId());
	}

	@Test
	public void testAddSearchQueryWithKeywordsAndCategory() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords", "100");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
	}

	@Test
	public void testAddSearchQueryWithNormalizedValues() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", true, true, false, false, false,
			false, false, 5.00, 10.00);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(1, searchQuery.getSearchQueryId());
		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
	}

	@Test
	public void testDeleteSearchQueries() throws Exception {
		SearchQueryUtil.addSearchQuery(_USER_ID, "First test keywords");
		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(2, searchQueries.size());

		SearchQueryUtil.deleteSearchQueries(_USER_ID);

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID);

		Assert.assertEquals(0, searchQueries.size());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		int firstSearchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(2, searchQueries.size());

		SearchQueryUtil.deleteSearchQuery(firstSearchQueryId);

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID);

		Assert.assertEquals(1, searchQueries.size());
	}

	@Test(expected = SQLException.class)
	public void testGetNonExistantSearchQuery() throws Exception {
		SearchQueryUtil.getSearchQuery(1);
	}

	@Test
	public void testGetSearchQueryCount() throws Exception {
		SearchQueryUtil.addSearchQuery(_USER_ID, "First test keywords");
		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		int numberOfSearchQueries = SearchQueryUtil.getSearchQueryCount(
			_USER_ID);

		Assert.assertEquals(2, numberOfSearchQueries);
	}

	@Test
	public void testIsExceedsTotalNumberOfSearchQueriesAllowed()
		throws Exception {

		Assert.assertFalse(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed(
				_USER_ID));

		SearchQueryUtil.addSearchQuery(_USER_ID, "First test keywords");
		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		Assert.assertTrue(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed(
				_USER_ID));
	}

	@Test
	public void testUpdateSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", false, false, false, false,
			false, false, false, 0.00, 0.00);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);

		searchQuery = new SearchQuery(
			1, _USER_ID, "New test keywords", "101", true, true, true, false,
			false, true, false, 5.00, 10.00);

		SearchQueryUtil.updateSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("New test keywords", searchQuery.getKeywords());
		Assert.assertEquals("101", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
	}

	@Test
	public void testUpdateSearchQueryWithKeywords() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

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
			_USER_ID, "First test keywords", "100");

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());

		SearchQueryUtil.updateSearchQuery(
			searchQueryId, "New test keywords", "200");

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("New test keywords", searchQuery.getKeywords());
		Assert.assertEquals("200", searchQuery.getCategoryId());
	}

	private static final int _USER_ID = 1;
}