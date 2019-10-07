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
import com.app.model.SearchResult;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author Jonathan McCann
 */
public class SearchResultDAO {

	@CacheEvict(value = "searchResults", key = "#searchQueryId")
	public void addSearchResults(
			int searchQueryId, List<SearchResult> searchResults)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Adding {} new search results", searchResults.size());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_SEARCH_RESULT_SQL, Statement.RETURN_GENERATED_KEYS)) {

			for (SearchResult searchResult : searchResults) {
				_populateAddSearchResultPreparedStatement(
					preparedStatement, searchResult);

				preparedStatement.executeUpdate();

				ResultSet resultSet = preparedStatement.getGeneratedKeys();

				resultSet.next();

				searchResult.setSearchResultId(resultSet.getInt(1));
			}
		}
	}

	@CacheEvict(value = "searchResults", key = "#searchQueryId")
	public void deleteSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Deleting search query results for search query ID: {}",
			searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERY_RESULTS_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "searchResults", key = "#searchQueryId")
	public void deleteSearchResults(
			int searchQueryId, int numberOfSearchResultsToRemove)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Deleting {} search results for search query ID: {}",
			numberOfSearchResultsToRemove, searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_RESULT_SQL)) {

			preparedStatement.setInt(1, searchQueryId);
			preparedStatement.setInt(2, numberOfSearchResultsToRemove);

			preparedStatement.executeUpdate();
		}
	}

	@Cacheable(value = "searchResults", key = "#searchQueryId")
	public List<SearchResult> getSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting search query results for search query ID: {}",
			searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_RESULTS_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			ResultSet resultSet = preparedStatement.executeQuery();

			List<SearchResult> searchResults = new ArrayList<>();

			while (resultSet.next()) {
				searchResults.add(_createSearchResultFromResultSet(resultSet));
			}

			return searchResults;
		}
	}

	public List<SearchResult> getUndeliveredSearchResults(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting undelivered search results for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_UNDELIVERED_SEARCH_RESULTS_SQL)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			List<SearchResult> searchResults = new ArrayList<>();

			while (resultSet.next()) {
				searchResults.add(_createSearchResultFromResultSet(resultSet));
			}

			return searchResults;
		}
	}

	@CacheEvict(value = "searchResults", allEntries = true)
	public void updateSearchResultsDeliveredStatus(
			List<Integer> searchResultIds, boolean delivered)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating search result IDs: {} to delivered status of: {}",
			searchResultIds, delivered);

		StringBuilder sql = new StringBuilder((searchResultIds.size() * 2) + 1);

		sql.append(_UPDATE_SEARCH_RESULTS_DELIVERED_STATUS_SQL_PREFIX);

		for (int i = 0; i < searchResultIds.size(); i++) {
			sql.append(searchResultIds.get(i));

			if (i < (searchResultIds.size() - 1)) {
				sql.append(", ");
			}
			else {
				sql.append(")");
			}
		}

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql.toString())) {

			preparedStatement.setBoolean(1, delivered);

			preparedStatement.executeUpdate();
		}
	}

	private static SearchResult _createSearchResultFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchResultId(resultSet.getInt("searchResultId"));
		searchResult.setSearchQueryId(resultSet.getInt("searchQueryId"));
		searchResult.setUserId(resultSet.getInt("userId"));
		searchResult.setItemId(resultSet.getString("itemId"));
		searchResult.setItemTitle(resultSet.getString("itemTitle"));
		searchResult.setItemURL(resultSet.getString("itemURL"));
		searchResult.setGalleryURL(resultSet.getString("galleryURL"));
		searchResult.setAuctionPrice(resultSet.getString("auctionPrice"));
		searchResult.setFixedPrice(resultSet.getString("fixedPrice"));
		searchResult.setDelivered(resultSet.getBoolean("delivered"));

		return searchResult;
	}

	private static void _populateAddSearchResultPreparedStatement(
			PreparedStatement preparedStatement, SearchResult searchResult)
		throws SQLException {

		preparedStatement.setInt(1, searchResult.getSearchQueryId());
		preparedStatement.setInt(2, searchResult.getUserId());
		preparedStatement.setString(3, searchResult.getItemId());
		preparedStatement.setString(4, searchResult.getItemTitle());
		preparedStatement.setString(5, searchResult.getItemURL());
		preparedStatement.setString(6, searchResult.getGalleryURL());
		preparedStatement.setString(7, searchResult.getAuctionPrice());
		preparedStatement.setString(8, searchResult.getFixedPrice());
	}

	private static final String _ADD_SEARCH_RESULT_SQL =
		"INSERT INTO SearchResult(searchQueryId, userId, itemId, itemTitle, " +
			"itemURL, galleryURL, auctionPrice, fixedPrice) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String _DELETE_SEARCH_QUERY_RESULTS_SQL =
		"DELETE FROM SearchResult WHERE searchQueryId = ?";

	private static final String _DELETE_SEARCH_RESULT_SQL =
		"DELETE FROM SearchResult WHERE searchQueryId = ? LIMIT ?";

	private static final String _GET_SEARCH_QUERY_RESULTS_SQL =
		"SELECT * FROM SearchResult WHERE searchQueryId = ? ORDER BY " +
			"searchResultId DESC";

	private static final String _GET_UNDELIVERED_SEARCH_RESULTS_SQL =
		"SELECT * FROM SearchResult WHERE userId = ? AND delivered = FALSE " +
			"ORDER BY searchResultId DESC";

	private static final String
		_UPDATE_SEARCH_RESULTS_DELIVERED_STATUS_SQL_PREFIX =
			"UPDATE SearchResult SET delivered = ? WHERE searchResultId IN (";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultDAO.class);

}