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

package com.app.dao.impl;

import com.app.dao.SearchQueryDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQueryModel;
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
public class SearchQueryDAOImpl implements SearchQueryDAO {

	@Override
	public void addSearchQuery(String searchQuery)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Adding new searchQuery: {}", searchQuery);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_SEARCH_QUERY_SQL)) {

			preparedStatement.setString(1, searchQuery);

			preparedStatement.executeUpdate();
		}
	}

	@Override
	public void deleteSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting search query ID: {}", searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERY_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	@Override
	public List<SearchQueryModel> getSearchQueries()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all search queries");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERIES_SQL);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<SearchQueryModel> searchQueryModels = new ArrayList<>();

			while (resultSet.next()) {
				int searchQueryId = resultSet.getInt("searchQueryId");

				String searchQuery = resultSet.getString("searchQuery");

				SearchQueryModel searchQueryModel = new SearchQueryModel(
					searchQueryId, searchQuery);

				searchQueryModels.add(searchQueryModel);
			}

			return searchQueryModels;
		}
	}

	@Override
	public String getSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search query ID: {}", searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY)) {

			preparedStatement.setInt(1, searchQueryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("searchQuery");
				}
				else {
					throw new SQLException();
				}
			}
		}
	}

	@Override
	public int getSearchQueryCount()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search query count");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_COUNT_SQL);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			int searchQueryCount = 0;

			while (resultSet.next()) {
				searchQueryCount = resultSet.getInt(1);
			}

			return searchQueryCount;
		}
	}

	@Override
	public void updateSearchQuery(int searchQueryId, String searchQuery)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating search query ID: {} to: {}", searchQueryId, searchQuery);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_SEARCH_QUERY)) {

			preparedStatement.setString(1, searchQuery);
			preparedStatement.setInt(2, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _ADD_SEARCH_QUERY_SQL =
		"INSERT INTO SearchQuery(searchQuery) VALUES(?)";

	private static final String _DELETE_SEARCH_QUERY_SQL =
		"DELETE FROM SearchQuery WHERE searchQueryId = ?";

	private static final String _GET_SEARCH_QUERIES_SQL =
		"SELECT * FROM SearchQuery";

	private static final String _GET_SEARCH_QUERY_COUNT_SQL =
		"SELECT COUNT(*) FROM SearchQuery";

	private static final String _GET_SEARCH_QUERY =
		"SELECT searchQuery FROM SearchQuery WHERE searchQueryId = ?";

	private static final String _UPDATE_SEARCH_QUERY =
		"UPDATE SearchQuery SET searchQuery = ? WHERE searchQueryId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryDAOImpl.class);

}