/**
 * Copyright (c) 2014-present Jonathan McCann
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General public static License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General public static License for more
 * details.
 */

package com.app.util;

import com.app.dao.SearchQueryDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQueryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchQueryUtil {

	public static void addSearchQuery(String searchQuery)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.addSearchQuery(searchQuery);
	}

	public static void addSearchQuery(String searchQuery, String categoryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.addSearchQuery(searchQuery, categoryId);
	}

	public static void deleteSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.deleteSearchQuery(searchQueryId);
	}

	public static List<SearchQueryModel> getSearchQueries()
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQueries();
	}

	public static String getSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQuery(searchQueryId);
	}

	public static int getSearchQueryCount()
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQueryCount();
	}

	public static boolean isExceedsTotalNumberOfSearchQueriesAllowed()
		throws DatabaseConnectionException, SQLException {

		int searchQueryCount = _searchQueryDAO.getSearchQueryCount();

		if ((searchQueryCount + 1) >
				PropertiesValues.TOTAL_NUMBER_OF_SEARCH_QUERIES_ALLOWED) {

			return true;
		}

		return false;
	}

	public static void updateSearchQuery(int searchQueryId, String searchQuery)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.updateSearchQuery(searchQueryId, searchQuery);
	}

	public static void updateSearchQuery(
			int searchQueryId, String searchQuery, String categoryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.updateSearchQuery(
			searchQueryId, searchQuery, categoryId);
	}

	@Autowired
	public void setSearchQueryDAO(SearchQueryDAO searchQueryDAO) {
		_searchQueryDAO = searchQueryDAO;
	}

	private static SearchQueryDAO _searchQueryDAO;

}