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

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
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
		SearchResultUtil.deleteSearchQueryResults(1);
		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(1);
	}

	@Test
	public void testAddSearchResult() throws Exception {
		Date endingTime = new Date();

		int searchResultId = addSearchResult("1234", endingTime);

		SearchResult searchResult = SearchResultUtil.getSearchResult(
			searchResultId);

		Assert.assertEquals(1, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals(10.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertEquals(endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Auction", searchResult.getTypeOfAuction());
	}

	@Test
	public void testDeleteSearchQueryResults() throws Exception {
		Date endingTime = new Date();

		addSearchResult("1234", endingTime);
		addSearchResult("2345", endingTime);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(2, searchResults.size());

		SearchResultUtil.deleteSearchQueryResults(1);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteSearchResult() throws Exception {
		Date endingTime = new Date();

		int firstSearchResultId = addSearchResult("1234", endingTime);
		addSearchResult("2345", endingTime);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(2, searchResults.size());

		SearchResultUtil.deleteSearchResult(firstSearchResultId);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(1, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithCompletePreviousResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "Test keywords");

		Date endingTime = new Date();

		List<SearchResult> searchResults = new ArrayList<>();

		SearchResult searchResult = SearchResultUtil.getSearchResult(
			addSearchResult("1234", endingTime));

		searchResults.add(searchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchResult.getSearchQueryId(), "1234");

		Method method = _clazz.getDeclaredMethod(
			"_filterSearchResults", SearchQuery.class, List.class);

		method.setAccessible(true);

		searchResults = (List<SearchResult>)method.invoke(
			_classInstance, searchQuery, searchResults);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithNullNewSearchResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "Test keywords");

		Method method = _clazz.getDeclaredMethod(
			"_filterSearchResults", SearchQuery.class, List.class);

		method.setAccessible(true);

		List<SearchResult> searchResults = (List<SearchResult>)method.invoke(
			_classInstance, searchQuery, new ArrayList<>());

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testFilterSearchResultsWithPartialPreviousResults()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "Test keywords");

		Date endingTime = new Date();

		List<SearchResult> searchResults = new ArrayList<>();

		SearchResult firstSearchResult = SearchResultUtil.getSearchResult(
			addSearchResult("1234", endingTime));

		SearchResult secondSearchResult = SearchResultUtil.getSearchResult(
			addSearchResult("2345", endingTime));

		searchResults.add(firstSearchResult);
		searchResults.add(secondSearchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			firstSearchResult.getSearchQueryId(), "1234");

		Method method = _clazz.getDeclaredMethod(
			"_filterSearchResults", SearchQuery.class, List.class);

		method.setAccessible(true);

		searchResults = (List<SearchResult>)method.invoke(
			_classInstance, searchQuery, searchResults);

		Assert.assertEquals(1, searchResults.size());
	}

	@Test
	public void testAddNewResults() throws Exception {
		Date endingTime = new Date();

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(SearchResultUtil.getSearchResult(
			addSearchResult("1234", endingTime)));

		Method method = _clazz.getDeclaredMethod("_addNewResults", List.class);

		method.setAccessible(true);

		method.invoke(_classInstance, searchResults);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		SearchResult searchResult = searchResults.get(0);

		Assert.assertEquals(1, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals(10.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertEquals(endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Auction", searchResult.getTypeOfAuction());

		Assert.assertEquals(
			1,
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(1));
	}

	@Test
	public void testAddNewResultsWithPreviousSearchQueryResults()
		throws Exception {

		Date endingTime = new Date();

		SearchResult searchResult = SearchResultUtil.getSearchResult(
			addSearchResult("1234", endingTime));

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		for (int i = 0; i < PropertiesValues.TOTAL_NUMBER_OF_PREVIOUS_SEARCH_RESULT_IDS; i++) {
			SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
				searchResult.getSearchQueryId(), String.valueOf(i));
		}

		Assert.assertEquals(
			10,
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(1));

		Method method = _clazz.getDeclaredMethod("_addNewResults", List.class);

		method.setAccessible(true);

		method.invoke(_classInstance, searchResults);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		searchResult = searchResults.get(0);

		Assert.assertEquals(1, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals(10.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertEquals(endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Auction", searchResult.getTypeOfAuction());

		Assert.assertEquals(
			10,
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(1));

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(1);

		String latestSearchQueryPreviousResult =
			searchQueryPreviousResults.get(9);

		Assert.assertEquals("1234", latestSearchQueryPreviousResult);
	}

	@Test
	public void testDeleteOldResultsWithNoNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", List.class, int.class);

		method.setAccessible(true);

		Date endingTime = new Date();

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("1234", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("2345", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("3456", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("4567", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("7890", endingTime)));

		method.invoke(_classInstance, searchResults, 0);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(5, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithPartialNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", List.class, int.class);

		method.setAccessible(true);

		Date endingTime = new Date();

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("1234", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("2345", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("3456", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("4567", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("7890", endingTime)));

		method.invoke(_classInstance, searchResults, 3);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(2, searchResults.size());
	}

	@Test
	public void testDeleteOldResultsWithAllNewResults() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_deleteOldResults", List.class, int.class);

		method.setAccessible(true);

		Date endingTime = new Date();

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("1234", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("2345", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("3456", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("4567", endingTime)));
		searchResults.add(
			SearchResultUtil.getSearchResult(
				addSearchResult("7890", endingTime)));

		method.invoke(_classInstance, searchResults, 5);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testRemovePreviouslyNotifiedResults() throws Exception {
		SearchResult searchResult = new SearchResult(
			1, "1234", "First Item", 10.00, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			new Date(), "Auction");

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

	@Test(expected = SQLException.class)
	public void testGetNonExistentSearchResult() throws Exception {
		SearchResultUtil.getSearchResult(1);
	}

	private static int addSearchResult(String itemId, Date endingTime)
		throws Exception {

		SearchResult searchResult = new SearchResult(
			1, itemId, "First Item", 10.00, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			endingTime, "Auction");

		return SearchResultUtil.addSearchResult(searchResult);
	}

	private static Object _classInstance;
	private static Class _clazz;
	private static final int _USER_ID = 1;

}