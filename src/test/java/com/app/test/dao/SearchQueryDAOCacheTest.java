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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchQueryDAOCacheTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpDatabase();

		SearchQuery activeSearchQuery = new SearchQuery(
			1, _USER_ID, "activeSearchQuery");

		activeSearchQuery.setActive(true);

		SearchQuery inactiveSearchQuery = new SearchQuery(
			2, _USER_ID, "inactiveSearchQuery");

		SearchQueryUtil.addSearchQuery(activeSearchQuery);
		SearchQueryUtil.addSearchQuery(inactiveSearchQuery);
	}

	@Test
	public void testActivateSearchQuery() throws Exception {
		_assertBeforeCacheEvict();

		_searchQueryDAO.activateSearchQuery(_USER_ID, 2);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(2, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(2, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(4, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());
	}

	@Test
	public void testAddSearchQuery() throws Exception {
		_assertBeforeCacheEvict();

		SearchQuery searchQuery = new SearchQuery(
			3, _USER_ID, "newSearchQuery");

		_searchQueryDAO.addSearchQuery(searchQuery);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(2, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(2, inactiveSearchQueries.size());
		Assert.assertEquals(4, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());
	}

	@Test
	public void testDeactivateSearchQuery() throws Exception {
		_assertBeforeCacheEvict();

		_searchQueryDAO.deactivateSearchQuery(_USER_ID, 1);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(2, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(2, inactiveSearchQueries.size());
		Assert.assertEquals(4, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());
	}

	@Test
	public void testDeleteSearchQueries() throws Exception {
		_assertBeforeCacheEvict();

		_searchQueryDAO.deleteSearchQueries(_USER_ID);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(0, inactiveSearchQueries.size());
		Assert.assertEquals(4, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		_assertBeforeCacheEvict();

		_searchQueryDAO.deleteSearchQuery(_USER_ID, 1);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(4, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());
	}

	@Test
	public void testUpdateSearchQuery() throws Exception {
		_assertBeforeCacheEvict();

		SearchQuery searchQuery = _searchQueryDAO.getSearchQuery(1);

		searchQuery.setActive(false);

		_searchQueryDAO.updateSearchQuery(_USER_ID, searchQuery);

		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(2, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(0, activeSearchQueries.size());
		Assert.assertEquals(2, inactiveSearchQueries.size());
		Assert.assertEquals(4, statistics.cacheHitCount());
		Assert.assertEquals(4, statistics.cacheMissCount());
	}

	private void _assertBeforeCacheEvict() throws Exception {
		Cache cache = _cacheManager.getCache("searchQueries");

		StatisticsGateway statistics = cache.getStatistics();

		List<SearchQuery> activeSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, true);

		List<SearchQuery> inactiveSearchQueries =
			_searchQueryDAO.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(0, statistics.cacheHitCount());
		Assert.assertEquals(2, statistics.cacheMissCount());

		activeSearchQueries = _searchQueryDAO.getSearchQueries(_USER_ID, true);

		inactiveSearchQueries = _searchQueryDAO.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(1, activeSearchQueries.size());
		Assert.assertEquals(1, inactiveSearchQueries.size());
		Assert.assertEquals(2, statistics.cacheHitCount());
		Assert.assertEquals(2, statistics.cacheMissCount());
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private SearchQueryDAO _searchQueryDAO;

}