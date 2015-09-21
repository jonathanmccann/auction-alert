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

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQueryModel;
import com.app.test.BaseDatabaseTestCase;

import java.sql.SQLException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SearchQueryDAOTest extends BaseDatabaseTestCase {

	@Override
	public void doSetUp() throws DatabaseConnectionException {
		_searchQueryDAOImpl = new SearchQueryDAOImpl();
	}

	@Test
	public void testSearchQueryDAO()
		throws DatabaseConnectionException, SQLException {

		// Test add

		_searchQueryDAOImpl.addSearchQuery("First test search query");
		_searchQueryDAOImpl.addSearchQuery("Second test search query");
		_searchQueryDAOImpl.addSearchQuery(
			"Third test search query with category ID", "100");
		_searchQueryDAOImpl.addSearchQuery(
			"Fourth test search query with category ID", "200");

		// Test get

		String searchQuery = _searchQueryDAOImpl.getSearchQuery(1);

		Assert.assertEquals("First test search query", searchQuery);

		// Test get multiple

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

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

		int searchQueryCount = _searchQueryDAOImpl.getSearchQueryCount();

		Assert.assertEquals(4, searchQueryCount);

		// Test update

		_searchQueryDAOImpl.updateSearchQuery(1, "Updated test search query");
		_searchQueryDAOImpl.updateSearchQuery(
			3, "Updated test search query with category ID", "300");

		searchQueryModels =	_searchQueryDAOImpl.getSearchQueries();

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

		_searchQueryDAOImpl.deleteSearchQuery(1);
		_searchQueryDAOImpl.deleteSearchQuery(2);
		_searchQueryDAOImpl.deleteSearchQuery(3);
		_searchQueryDAOImpl.deleteSearchQuery(4);

		searchQueryModels = _searchQueryDAOImpl.getSearchQueries();

		Assert.assertEquals(0, searchQueryModels.size());

		searchQueryCount = _searchQueryDAOImpl.getSearchQueryCount();

		Assert.assertEquals(0, searchQueryCount);
	}

	private static SearchQueryDAOImpl _searchQueryDAOImpl;

}