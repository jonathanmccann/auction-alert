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

import com.app.dao.SearchQueryPreviousResultDAO;
import com.app.exception.DatabaseConnectionException;

import java.sql.SQLException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchQueryPreviousResultUtil {

	public static void addSearchQueryPreviousResult(
			int searchQueryId, String searchResultItemId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryPreviousResultDAO.addSearchQueryPreviousResult(
			searchQueryId, searchResultItemId);
	}

	public static void deleteSearchQueryPreviousResult(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryPreviousResultDAO.deleteSearchQueryPreviousResult(
			searchQueryId);
	}

	public static void deleteSearchQueryPreviousResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryPreviousResultDAO.deleteSearchQueryPreviousResults(
			searchQueryId);
	}

	public static List<String> getSearchQueryPreviousResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryPreviousResultDAO.getSearchQueryPreviousResults(
			searchQueryId);
	}

	public static int getSearchQueryPreviousResultsCount(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryPreviousResultDAO.getSearchQueryPreviousResultsCount(
			searchQueryId);
	}

	@Autowired
	public void setSearchQueryPreviousResultDAO(
		SearchQueryPreviousResultDAO searchQueryPreviousResultDAO) {

		_searchQueryPreviousResultDAO = searchQueryPreviousResultDAO;
	}

	private static SearchQueryPreviousResultDAO _searchQueryPreviousResultDAO;

}