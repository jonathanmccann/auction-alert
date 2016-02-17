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

package com.app.dao;

import com.app.exception.DatabaseConnectionException;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SearchQueryPreviousResultDAO {

	public void addSearchQueryPreviousResult(
			int searchQueryId, String searchResultItemId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding {} as a new previous search query result for search " +
				"query ID: {}",
			searchResultItemId, searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_SEARCH_QUERY_PREVIOUS_RESULT_SQL)) {

			preparedStatement.setInt(1, searchQueryId);
			preparedStatement.setString(2, searchResultItemId);

			preparedStatement.executeUpdate();
		}
	}

	public void deleteSearchQueryPreviousResult(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Deleting the oldest previous search query results for search " +
				"query ID: {}",
			searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERY_PREVIOUS_RESULT_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	public void deleteSearchQueryPreviousResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Deleting previous search query results for search query ID: {}",
			searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERY_PREVIOUS_RESULTS_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	public List<String> getSearchQueryPreviousResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting previous search query results for search query ID: {}",
			searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_PREVIOUS_RESULTS_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			ResultSet resultSet = preparedStatement.executeQuery();

			List<String> searchQueryPreviousResults = new ArrayList<>();

			while (resultSet.next()) {
				searchQueryPreviousResults.add(
					resultSet.getString("searchResultItemId"));
			}

			return searchQueryPreviousResults;
		}
	}

	public int getSearchQueryPreviousResultsCount(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search query previous results count count");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_PREVIOUS_RESULTS_COUNT_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			ResultSet resultSet = preparedStatement.executeQuery();

			int searchQueryCount = 0;

			while (resultSet.next()) {
				searchQueryCount = resultSet.getInt(1);
			}

			return searchQueryCount;
		}
	}

	private static final String _ADD_SEARCH_QUERY_PREVIOUS_RESULT_SQL =
		"INSERT INTO SearchQueryPreviousResult(searchQueryId, " +
			"searchResultItemId) VALUES(?, ?)";

	private static final String _DELETE_SEARCH_QUERY_PREVIOUS_RESULT_SQL =
		"DELETE FROM SearchQueryPreviousResult WHERE searchQueryId = ? LIMIT 1";

	private static final String _DELETE_SEARCH_QUERY_PREVIOUS_RESULTS_SQL =
		"DELETE FROM SearchQueryPreviousResult WHERE searchQueryId = ?";

	private static final String _GET_SEARCH_QUERY_PREVIOUS_RESULTS_COUNT_SQL =
		"SELECT COUNT(*) FROM SearchQueryPreviousResult WHERE searchQueryId = ?";

	private static final String _GET_SEARCH_QUERY_PREVIOUS_RESULTS_SQL =
		"SELECT * FROM SearchQueryPreviousResult WHERE searchQueryId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryPreviousResultDAO.class);

}