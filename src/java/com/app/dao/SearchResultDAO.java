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
				_ADD_SEARCH_RESULT_SQL)) {

			connection.setAutoCommit(false);

			for (SearchResult searchResult : searchResults) {
				_populateAddSearchResultPreparedStatement(
					preparedStatement, searchResult);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();

			connection.commit();
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

	private static SearchResult _createSearchResultFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchResultId(resultSet.getInt("searchResultId"));
		searchResult.setSearchQueryId(resultSet.getInt("searchQueryId"));
		searchResult.setItemId(resultSet.getString("itemId"));
		searchResult.setItemTitle(resultSet.getString("itemTitle"));
		searchResult.setItemURL(resultSet.getString("itemURL"));
		searchResult.setGalleryURL(resultSet.getString("galleryURL"));
		searchResult.setAuctionPrice(resultSet.getString("auctionPrice"));
		searchResult.setFixedPrice(resultSet.getString("fixedPrice"));

		return searchResult;
	}

	private static void _populateAddSearchResultPreparedStatement(
			PreparedStatement preparedStatement, SearchResult searchResult)
		throws SQLException {

		preparedStatement.setInt(1, searchResult.getSearchQueryId());
		preparedStatement.setString(2, searchResult.getItemId());
		preparedStatement.setString(3, searchResult.getItemTitle());
		preparedStatement.setString(4, searchResult.getItemURL());
		preparedStatement.setString(5, searchResult.getGalleryURL());
		preparedStatement.setString(6, searchResult.getAuctionPrice());
		preparedStatement.setString(7, searchResult.getFixedPrice());
	}

	private static final String _ADD_SEARCH_RESULT_SQL =
		"INSERT INTO SearchResult(searchQueryId, itemId, itemTitle, itemURL, " +
			"galleryURL, auctionPrice, fixedPrice) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?)";

	private static final String _DELETE_SEARCH_QUERY_RESULTS_SQL =
		"DELETE FROM SearchResult WHERE searchQueryId = ?";

	private static final String _DELETE_SEARCH_RESULT_SQL =
		"DELETE FROM SearchResult WHERE searchQueryId = ? LIMIT ?";

	private static final String _GET_SEARCH_QUERY_RESULTS_SQL =
		"SELECT * FROM SearchResult WHERE searchQueryId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultDAO.class);

}