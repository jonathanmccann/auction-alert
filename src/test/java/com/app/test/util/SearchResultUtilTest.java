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
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SearchResultUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(SearchResultUtil.class.getName());

		_classInstance = _clazz.newInstance();

		setUpDatabase();
		setUpProperties();
	}

	@After
	public void tearDown() throws Exception {
		SearchQueryUtil.deleteSearchQueries(_USER_ID);

		SearchResultUtil.deleteSearchQueryResults(_SEARCH_QUERY_ID);
	}

	@Test
	public void testAddSearchResult() throws Exception {
		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals(_SEARCH_QUERY_ID, searchResult.getSearchQueryId());
		Assert.assertEquals(_USER_ID, searchResult.getUserId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals("$10.00", searchResult.getAuctionPrice());
		Assert.assertEquals("$14.99", searchResult.getFixedPrice());
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertTrue(searchResult.isDelivered());
	}

	@Test
	public void testAddUndeliveredSearchResultsWithNoUndeliveredResults()
		throws Exception {

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		SearchResultUtil.applyUndeliveredSearchResults(
			_USER_ID, searchQueryResultMap);

		Assert.assertTrue(searchQueryResultMap.isEmpty());
	}

	@Test
	public void testAddUndeliveredSearchResultsWithExistingQuery()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test Keywords");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery.setSearchQueryId(searchQueryId);

		List<SearchResult> undeliveredSearchResults = new ArrayList<>();

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(searchQueryId);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("1234");
		searchResult.setDelivered(false);

		undeliveredSearchResults.add(searchResult);

		SearchResultUtil.addSearchResults(
			searchQueryId, undeliveredSearchResults);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(searchQueryId);

		List<Integer> searchResultIds = new ArrayList<>();

		searchResultIds.add(searchResults.get(0).getSearchResultId());

		SearchResultUtil.updateSearchResultsDeliveredStatus(
			searchResultIds, false);

		searchResults = new ArrayList<>();

		searchResult = new SearchResult();

		searchResult.setSearchQueryId(searchQueryId);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("2345");
		searchResult.setDelivered(true);

		searchResults.add(searchResult);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		searchQueryResultMap.put(searchQuery, searchResults);

		SearchResultUtil.applyUndeliveredSearchResults(
			_USER_ID, searchQueryResultMap);

		Assert.assertEquals(1, searchQueryResultMap.size());

		searchResults = searchQueryResultMap.get(searchQuery);

		Assert.assertEquals(2, searchResults.size());

		searchResults = SearchResultUtil.getUndeliveredSearchResults(_USER_ID);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testAddUndeliveredSearchResultsWithoutExistingQuery()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test Keywords");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery.setSearchQueryId(searchQueryId);

		List<SearchResult> undeliveredSearchResults = new ArrayList<>();

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(searchQueryId);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("1234");
		searchResult.setDelivered(false);

		undeliveredSearchResults.add(searchResult);

		SearchResultUtil.addSearchResults(
			searchQueryId, undeliveredSearchResults);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(searchQueryId);

		List<Integer> searchResultIds = new ArrayList<>();

		searchResultIds.add(searchResults.get(0).getSearchResultId());

		SearchResultUtil.updateSearchResultsDeliveredStatus(
			searchResultIds, false);

		searchResults = new ArrayList<>();

		searchResult = new SearchResult();

		searchResult.setSearchQueryId(searchQueryId);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("2345");
		searchResult.setDelivered(true);

		searchResults.add(searchResult);

		Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
			new HashMap<>();

		SearchResultUtil.applyUndeliveredSearchResults(
			_USER_ID, searchQueryResultMap);

		Assert.assertEquals(1, searchQueryResultMap.size());

		searchResults = searchQueryResultMap.get(searchQuery);

		Assert.assertEquals(1, searchResults.size());

		searchResults = SearchResultUtil.getUndeliveredSearchResults(_USER_ID);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithAllNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", int.class, int.class, int.class);

		method.setAccessible(true);

		_addSearchResult("1234");
		_addSearchResult("2345");
		_addSearchResult("3456");
		_addSearchResult("4567");
		_addSearchResult("5678");

		method.invoke(_classInstance, _SEARCH_QUERY_ID, 5, 5);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithNoNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", int.class, int.class, int.class);

		method.setAccessible(true);

		_addSearchResult("1234");
		_addSearchResult("2345");
		_addSearchResult("3456");
		_addSearchResult("4567");
		_addSearchResult("5678");

		method.invoke(_classInstance, _SEARCH_QUERY_ID, 5, 0);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(5, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithPartialNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", int.class, int.class, int.class);

		method.setAccessible(true);

		_addSearchResult("1234");
		_addSearchResult("2345");
		_addSearchResult("3456");
		_addSearchResult("4567");
		_addSearchResult("5678");

		method.invoke(_classInstance, _SEARCH_QUERY_ID, 5, 3);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(2, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals("5678", searchResult.getItemId());
	}

	@Test
	public void testDeleteSearchQueryResults() throws Exception {
		_addSearchResult("1234");
		_addSearchResult("2345");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(2, searchResults.size());

		SearchResultUtil.deleteSearchQueryResults(_SEARCH_QUERY_ID);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteSearchResult() throws Exception {
		_addSearchResult("1234");
		_addSearchResult("2345");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(2, searchResults.size());

		SearchResultUtil.deleteSearchResults(_SEARCH_QUERY_ID, 1);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals("2345", searchResult.getItemId());
	}

	@Test
	public void testFilterSearchResultsWithCompletePreviousResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(_SEARCH_QUERY_ID);
		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");

		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		searchResults = SearchResultUtil.filterSearchResults(
			searchQuery, searchResults);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithNullNewSearchResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(_SEARCH_QUERY_ID);
		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");

		List<SearchResult> searchResults = SearchResultUtil.filterSearchResults(
			searchQuery, new ArrayList<>());

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithPartialPreviousResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(_SEARCH_QUERY_ID);
		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");

		_addSearchResult("1234");
		_addSearchResult("2345");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		List<SearchResult> nonCacheSearchResults =
			new ArrayList<>(searchResults);

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setItemId("3456");

		nonCacheSearchResults.add(searchResult);

		nonCacheSearchResults = SearchResultUtil.filterSearchResults(
			searchQuery, nonCacheSearchResults);

		Assert.assertEquals(1, nonCacheSearchResults.size());

		int searchResultId = nonCacheSearchResults.get(0).getSearchResultId();

		Assert.assertTrue(searchResultId > 0);
	}

	@Test
	public void testGetUndeliveredSearchResults() throws Exception {
		_addSearchResult("1234");
		_addSearchResult("2345");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		List<SearchResult> undeliveredSearchResults =
			SearchResultUtil.getUndeliveredSearchResults(_USER_ID);

		Assert.assertEquals(2, searchResults.size());
		Assert.assertEquals(0, undeliveredSearchResults.size());

		List<Integer> searchResultIds = new ArrayList<>();

		searchResultIds.add(searchResults.get(0).getSearchResultId());

		SearchResultUtil.updateSearchResultsDeliveredStatus(
			searchResultIds, false);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		undeliveredSearchResults = SearchResultUtil.getUndeliveredSearchResults(
			_USER_ID);

		Assert.assertEquals(2, searchResults.size());
		Assert.assertEquals(1, undeliveredSearchResults.size());

		SearchResult searchResult = undeliveredSearchResults.get(0);

		Assert.assertEquals(_SEARCH_QUERY_ID, searchResult.getSearchQueryId());
		Assert.assertEquals(_USER_ID, searchResult.getUserId());
		Assert.assertEquals("2345", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals("$10.00", searchResult.getAuctionPrice());
		Assert.assertEquals("$14.99", searchResult.getFixedPrice());
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertFalse(searchResult.isDelivered());
	}

	@Test
	public void testRemovePreviouslyNotifiedResults() throws Exception {
		List<SearchResult> existingSearchResults = new ArrayList<>();

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(1);
		searchResult.setItemId("1234");

		List<SearchResult> newSearchResults = new ArrayList<>();

		newSearchResults.add(searchResult);

		Method method = _clazz.getDeclaredMethod(
			"_removePreviouslyNotifiedResults", List.class, List.class);

		method.setAccessible(true);

		List<SearchResult> searchResults =
			(List<SearchResult>)method.invoke(
				_classInstance, existingSearchResults, newSearchResults);

		Assert.assertEquals(1, searchResults.size());

		searchResult = new SearchResult();

		searchResult.setSearchQueryId(1);
		searchResult.setItemId("2345");

		existingSearchResults.add(searchResult);

		searchResults = (List<SearchResult>)method.invoke(
			_classInstance, existingSearchResults, newSearchResults);

		Assert.assertEquals(1, searchResults.size());

		searchResult = new SearchResult();

		searchResult.setSearchQueryId(1);
		searchResult.setItemId("1234");

		existingSearchResults.add(searchResult);

		searchResults = (List<SearchResult>)method.invoke(
			_classInstance, existingSearchResults, newSearchResults);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testUpdateSearchResultsDeliveredStatus() throws Exception {
		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		List<SearchResult> undeliveredSearchResults =
			SearchResultUtil.getUndeliveredSearchResults(_USER_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(0, undeliveredSearchResults.size());

		List<Integer> searchResultIds = new ArrayList<>();

		searchResultIds.add(searchResults.get(0).getSearchResultId());

		SearchResultUtil.updateSearchResultsDeliveredStatus(
			searchResultIds, false);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		undeliveredSearchResults = SearchResultUtil.getUndeliveredSearchResults(
			_USER_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(1, undeliveredSearchResults.size());

		SearchResultUtil.updateSearchResultsDeliveredStatus(
			searchResultIds, true);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		undeliveredSearchResults = SearchResultUtil.getUndeliveredSearchResults(
			_USER_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(0, undeliveredSearchResults.size());
	}

	private static void _addSearchResult(String itemId) throws Exception {
		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId(itemId);
		searchResult.setItemTitle("First Item");
		searchResult.setAuctionPrice("$10.00");
		searchResult.setFixedPrice("$14.99");
		searchResult.setItemURL("http://www.ebay.com/itm/1234");
		searchResult.setGalleryURL("http://www.ebay.com/123.jpg");

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		SearchResultUtil.addSearchResults(_SEARCH_QUERY_ID, searchResults);
	}

	private static final int _SEARCH_QUERY_ID = 1;

	private static Object _classInstance;
	private static Class _clazz;

}