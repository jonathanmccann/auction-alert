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

import com.app.model.SearchQuery;
import com.app.test.BaseTestCase;
import com.app.util.SearchQueryUtil;

import java.sql.SQLException;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchQueryUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
		setUpProperties();
	}

	@After
	public void tearDown() throws Exception {
		SearchQueryUtil.deleteSearchQueries(_USER_ID);
	}

	@Test
	public void testActivateSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", false, false, false, false,
			false, false, false, 0.00, 0.00, false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchQueryUtil.activateSearchQuery(searchQueryId);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testAddSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", true, true, true, true, true,
			true, true, 5.00, 10.00, true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

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
		Assert.assertTrue(searchQuery.isActive());
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
			false, false, 5.00, 10.00, true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

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
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testDeactivateSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", false, false, false, false,
			false, false, false, 0.00, 0.00, true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchQueryUtil.deactivateSearchQuery(searchQueryId);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertFalse(searchQuery.isActive());
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
	public void testGetSearchQueries() throws Exception {
		SearchQueryUtil.addSearchQuery(_USER_ID, "First test keywords");
		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(2, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		searchQuery.setActive(false);

		SearchQueryUtil.updateSearchQuery(searchQuery);

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, searchQueries.size());
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
			false, false, false, 0.00, 0.00, false);

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
		Assert.assertFalse(searchQuery.isActive());

		searchQuery = new SearchQuery(
			searchQueryId, _USER_ID, "New test keywords", "101", true, true,
			true, false, false, true, false, 5.00, 10.00, true);

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
		Assert.assertTrue(searchQuery.isActive());;
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