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

package com.app.test.dao;

import com.app.dao.SearchResultDAO;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import com.app.util.SearchResultUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchResultDAOCacheTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
	}

	@After
	public void tearDown() throws Exception {
		SearchResultUtil.deleteSearchQueryResults(_SEARCH_QUERY_ID);
	}

	@Test
	public void testAddSearchResultsCacheEvict() throws Exception {
		Cache cache = _cacheManager.getCache("searchResults");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchResult> searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(0, searchResults.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 1, statistics.cacheMissCount());

		List<SearchResult> searchResultsToAdd = new ArrayList<>();

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setItemId("itemId");

		searchResultsToAdd.add(searchResult);

		_searchResultDAO.addSearchResults(_SEARCH_QUERY_ID, searchResultsToAdd);

		searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(hitCount + 1, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testDeleteSearchQueryResultsCacheEvict() throws Exception {
		List<SearchResult> searchResultsToAdd = new ArrayList<>();

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setItemId("itemId");

		searchResultsToAdd.add(searchResult);

		_searchResultDAO.addSearchResults(_SEARCH_QUERY_ID, searchResultsToAdd);

		Cache cache = _cacheManager.getCache("searchResults");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchResult> searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 1, statistics.cacheMissCount());

		_searchResultDAO.deleteSearchQueryResults(_SEARCH_QUERY_ID);

		searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(0, searchResults.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(0, searchResults.size());
		Assert.assertEquals(hitCount + 1, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testDeleteSearchResultsCacheEvict() throws Exception {
		List<SearchResult> searchResultsToAdd = new ArrayList<>();

		SearchResult firstSearchResult = new SearchResult();

		firstSearchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		firstSearchResult.setItemId("itemId");

		SearchResult secondSearchResult = new SearchResult();

		secondSearchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		secondSearchResult.setItemId("itemId");

		searchResultsToAdd.add(firstSearchResult);
		searchResultsToAdd.add(secondSearchResult);

		_searchResultDAO.addSearchResults(_SEARCH_QUERY_ID, searchResultsToAdd);

		Cache cache = _cacheManager.getCache("searchResults");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchResult> searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(2, searchResults.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 1, statistics.cacheMissCount());

		_searchResultDAO.deleteSearchResults(_SEARCH_QUERY_ID, 1);

		searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		searchResults =
			_searchResultDAO.getSearchQueryResults(_SEARCH_QUERY_ID);

		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(hitCount + 1, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private SearchResultDAO _searchResultDAO;

	private static final int _SEARCH_QUERY_ID = 1;

}