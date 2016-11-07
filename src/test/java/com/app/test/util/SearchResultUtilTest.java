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
import com.app.util.PropertiesValues;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchResultUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
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
		SearchResultUtil.deleteSearchQueryResults(_SEARCH_QUERY_ID);
		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(
			_SEARCH_QUERY_ID);
	}

	@Test
	public void testAddNewResults() throws Exception {
		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		Method method = _clazz.getDeclaredMethod("_addNewResults", List.class);

		method.setAccessible(true);

		method.invoke(_classInstance, searchResults);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals(_SEARCH_QUERY_ID, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals("$10.00", searchResult.getAuctionPrice());
		Assert.assertEquals("$14.99", searchResult.getFixedPrice());
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());

		Assert.assertEquals(
			1,
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(
				_SEARCH_QUERY_ID));
	}

	@Test
	public void testAddNewResultsWithPreviousSearchQueryResults()
		throws Exception {

		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		SearchResult searchResult = searchResults.get(0);

		for (int i = 0; i < PropertiesValues.TOTAL_NUMBER_OF_PREVIOUS_SEARCH_RESULT_IDS; i++) {
			SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
				searchResult.getSearchQueryId(), String.valueOf(i));
		}

		Assert.assertEquals(
			10,
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(
				_SEARCH_QUERY_ID));

		Method method = _clazz.getDeclaredMethod("_addNewResults", List.class);

		method.setAccessible(true);

		method.invoke(_classInstance, searchResults);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		searchResult = searchResults.get(0);

		Assert.assertEquals(_SEARCH_QUERY_ID, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals("$10.00", searchResult.getAuctionPrice());
		Assert.assertEquals("$14.99", searchResult.getFixedPrice());
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());

		Assert.assertEquals(
			10,
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(
				_SEARCH_QUERY_ID));

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				_SEARCH_QUERY_ID);

		String latestSearchQueryPreviousResult = searchQueryPreviousResults.get(
			9);

		Assert.assertEquals("1234", latestSearchQueryPreviousResult);
	}

	@Test
	public void testAddSearchResult() throws Exception {
		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals(_SEARCH_QUERY_ID, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals("$10.00", searchResult.getAuctionPrice());
		Assert.assertEquals("$14.99", searchResult.getFixedPrice());
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
	}

	@Test
	public void testDeleteOldResultsWithAllNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", List.class, int.class);

		method.setAccessible(true);

		_addSearchResult("1234");
		_addSearchResult("2345");
		_addSearchResult("3456");
		_addSearchResult("4567");
		_addSearchResult("5678");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		method.invoke(_classInstance, searchResults, 5);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithNoNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", List.class, int.class);

		method.setAccessible(true);

		_addSearchResult("1234");
		_addSearchResult("2345");
		_addSearchResult("3456");
		_addSearchResult("4567");
		_addSearchResult("5678");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		method.invoke(_classInstance, searchResults, 0);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		Assert.assertEquals(5, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithPartialNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", List.class, int.class);

		method.setAccessible(true);

		_addSearchResult("1234");
		_addSearchResult("2345");
		_addSearchResult("3456");
		_addSearchResult("4567");
		_addSearchResult("5678");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		method.invoke(_classInstance, searchResults, 3);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		Assert.assertEquals(2, searchResults.size());
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

		SearchResult firstSearchResult = searchResults.get(0);

		SearchResultUtil.deleteSearchResult(
			firstSearchResult.getSearchResultId());

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithCompletePreviousResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			_SEARCH_QUERY_ID, _USER_ID, "Test keywords");

		_addSearchResult("1234");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		SearchResult searchResult = searchResults.get(0);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchResult.getSearchQueryId(), "1234");

		searchResults = SearchResultUtil.filterSearchResults(
			searchQuery, searchResults);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithNullNewSearchResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			_SEARCH_QUERY_ID, _USER_ID, "Test keywords");

		List<SearchResult> searchResults = SearchResultUtil.filterSearchResults(
			searchQuery, new ArrayList<SearchResult>());

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithPartialPreviousResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			_SEARCH_QUERY_ID, _USER_ID, "Test keywords");

		_addSearchResult("1234");
		_addSearchResult("2345");

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_SEARCH_QUERY_ID);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			_SEARCH_QUERY_ID, "1234");

		searchResults = SearchResultUtil.filterSearchResults(
			searchQuery, searchResults);

		Assert.assertEquals(1, searchResults.size());
	}

	@Test
	public void testRemovePreviouslyNotifiedResults() throws Exception {
		SearchResult searchResult = new SearchResult(
			1, "1234", "First Item", "$10.00", "$14.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		Method method = _clazz.getDeclaredMethod(
			"_removePreviouslyNotifiedResults", int.class, List.class);

		method.setAccessible(true);

		method.invoke(_classInstance, 1, searchResults);

		Assert.assertEquals(1, searchResults.size());

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchResult.getSearchQueryId(), "2345");

		method.invoke(_classInstance, 1, searchResults);

		Assert.assertEquals(1, searchResults.size());

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchResult.getSearchQueryId(), "1234");

		method.invoke(_classInstance, 1, searchResults);

		Assert.assertEquals(0, searchResults.size());
	}

	private static int _addSearchResult(String itemId) throws Exception {
		SearchResult searchResult = new SearchResult(
			_SEARCH_QUERY_ID, itemId, "First Item", "$10.00", "$14.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		return SearchResultUtil.addSearchResult(searchResult);
	}

	private static final int _SEARCH_QUERY_ID = 1;

	private static final int _USER_ID = 1;

	private static Object _classInstance;
	private static Class _clazz;

}