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

import com.app.dao.SearchQueryDAO;
import com.app.model.SearchQuery;
import com.app.test.BaseTestCase;
import com.app.util.SearchQueryUtil;

import java.util.List;

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
public class SearchQueryDAOCacheTest extends BaseTestCase {

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
		int searchQueryId = _addSearchQuery(false);

		_searchQueryDAO.activateSearchQuery(_USER_ID, searchQueryId);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testAddSearchQuery() throws Exception {
		_addSearchQuery(true);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testDeactivateSearchQuery() throws Exception {
		int searchQueryId = _addSearchQuery(true);

		_searchQueryDAO.deactivateSearchQuery(_USER_ID, searchQueryId);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testDeleteSearchQueries() throws Exception {
		_searchQueryDAO.deleteSearchQueries(_USER_ID);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		int searchQueryId = _addSearchQuery(true);

		_searchQueryDAO.deleteSearchQuery(_USER_ID, searchQueryId);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	@Test
	public void testUpdateSearchQuery() throws Exception {
		int searchQueryId = _addSearchQuery(true);

		SearchQuery searchQuery = _searchQueryDAO.getSearchQuery(searchQueryId);

		searchQuery.setActive(false);

		_searchQueryDAO.updateSearchQuery(_USER_ID, searchQuery);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		long hitCount = statistics.cacheHitCount();
		long missCount = statistics.cacheMissCount();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(hitCount + 2, statistics.cacheHitCount());
		Assert.assertEquals(missCount + 2, statistics.cacheMissCount());
	}

	private static int _addSearchQuery(boolean isActive) throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "searchQuery");

		searchQuery.setActive(isActive);

		return SearchQueryUtil.addSearchQuery(searchQuery);
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private SearchQueryDAO _searchQueryDAO;

	private static final int _USER_ID = 1;

}