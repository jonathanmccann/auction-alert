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

package com.app.util;

import com.app.dao.SearchQueryDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchQueryUtil {

	public static int addSearchQuery(int userId, String keywords)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.addSearchQuery(userId, keywords);
	}

	public static int addSearchQuery(
			int userId, String keywords, String categoryId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.addSearchQuery(userId, keywords, categoryId);
	}

	public static int addSearchQuery(SearchQuery searchQuery)
		throws DatabaseConnectionException, SQLException {

		normalizeSearchQuery(searchQuery);

		return _searchQueryDAO.addSearchQuery(searchQuery);
	}

	public static void deleteSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.deleteSearchQueries(userId);
	}

	public static void deleteSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.deleteSearchQuery(searchQueryId);
	}

	public static List<SearchQuery> getSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQueries(userId);
	}

	public static SearchQuery getSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQuery(searchQueryId);
	}

	public static int getSearchQueryCount(int userId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQueryCount(userId);
	}

	public static boolean isExceedsTotalNumberOfSearchQueriesAllowed(int userId)
		throws DatabaseConnectionException, SQLException {

		int searchQueryCount = _searchQueryDAO.getSearchQueryCount(userId);

		if ((searchQueryCount + 1) >
				PropertiesValues.TOTAL_NUMBER_OF_SEARCH_QUERIES_ALLOWED) {

			return true;
		}

		return false;
	}

	public static void updateSearchQuery(SearchQuery searchQuery)
		throws DatabaseConnectionException, SQLException {

		normalizeSearchQuery(searchQuery);

		_searchQueryDAO.updateSearchQuery(searchQuery);
	}

	public static void updateSearchQuery(int searchQueryId, String keywords)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.updateSearchQuery(searchQueryId, keywords);
	}

	public static void updateSearchQuery(
			int searchQueryId, String keywords, String categoryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.updateSearchQuery(
			searchQueryId, keywords, categoryId);
	}

	@Autowired
	public void setSearchQueryDAO(SearchQueryDAO searchQueryDAO) {
		_searchQueryDAO = searchQueryDAO;
	}

	private static void normalizeSearchQuery(SearchQuery searchQuery) {
		if (!searchQuery.isNewCondition() && !searchQuery.isUsedCondition() &&
			!searchQuery.isUnspecifiedCondition()) {

			searchQuery.setNewCondition(true);
			searchQuery.setUsedCondition(true);
			searchQuery.setUnspecifiedCondition(true);
		}

		if (!searchQuery.isAuctionListing() &&
			!searchQuery.isFixedPriceListing()) {

			searchQuery.setAuctionListing(true);
			searchQuery.setFixedPriceListing(true);
		}
	}

	private static SearchQueryDAO _searchQueryDAO;

}