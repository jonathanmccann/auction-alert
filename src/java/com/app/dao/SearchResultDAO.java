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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SearchResultDAO {

	public int addSearchResult(SearchResult searchResult)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding new search result: {}", searchResult.getItemId());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_SEARCH_RESULT_SQL, Statement.RETURN_GENERATED_KEYS)) {

			populateAddSearchResultPreparedStatement(
				preparedStatement, searchResult);

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

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

	public void deleteSearchResult(int searchResultId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting search result ID: {}", searchResultId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_RESULT_SQL)) {

			preparedStatement.setInt(1, searchResultId);

			preparedStatement.executeUpdate();
		}
	}

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
				searchResults.add(
					createSearchResultFromResultSet(resultSet));
			}

			return searchResults;
		}
	}

	public SearchResult getSearchResult(int searchResultId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search result for ID: {}", searchResultId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_RESULT_SQL)) {

			preparedStatement.setInt(1, searchResultId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return createSearchResultFromResultSet(resultSet);
			}
			else {
				throw new SQLException(
					"No search result exists with ID: " + searchResultId);
			}
		}
	}

	private static SearchResult createSearchResultFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchResultId(resultSet.getInt("searchResultId"));
		searchResult.setSearchQueryId(resultSet.getInt("searchQueryId"));
		searchResult.setItemId(resultSet.getString("itemId"));
		searchResult.setItemTitle(resultSet.getString("itemTitle"));
		searchResult.setTypeOfAuction(resultSet.getString("typeOfAuction"));
		searchResult.setItemURL(resultSet.getString("itemURL"));
		searchResult.setGalleryURL(resultSet.getString("galleryURL"));
		searchResult.setEndingTime(new Date(resultSet.getLong("endingTime")));
		searchResult.setAuctionPrice(resultSet.getDouble("auctionPrice"));
		searchResult.setFixedPrice(resultSet.getDouble("fixedPrice"));

		return searchResult;
	}

	private static void populateAddSearchResultPreparedStatement(
			PreparedStatement preparedStatement,
			SearchResult searchResult)
		throws SQLException {

		preparedStatement.setInt(1, searchResult.getSearchQueryId());
		preparedStatement.setString(2, searchResult.getItemId());
		preparedStatement.setString(3, searchResult.getItemTitle());
		preparedStatement.setString(4, searchResult.getTypeOfAuction());
		preparedStatement.setString(5, searchResult.getItemURL());
		preparedStatement.setString(6, searchResult.getGalleryURL());

		Date endingTime = searchResult.getEndingTime();

		preparedStatement.setLong(7, endingTime.getTime());
		preparedStatement.setDouble(8, searchResult.getAuctionPrice());
		preparedStatement.setDouble(9, searchResult.getFixedPrice());
	}

	private static final String _ADD_SEARCH_RESULT_SQL =
		"INSERT INTO SearchResult(searchQueryId, itemId, itemTitle, " +
			"typeOfAuction, itemURL, galleryURL, endingTime, auctionPrice, " +
				" fixedPrice) VALUES(?, ?, ? , ?, ?, ?, ?, ?, ?)";

	private static final String _DELETE_SEARCH_QUERY_RESULTS_SQL =
		"DELETE FROM SearchResult WHERE searchQueryId = ?";

	private static final String _DELETE_SEARCH_RESULT_SQL =
		"DELETE FROM SearchResult WHERE searchResultId = ?";

	private static final String _GET_SEARCH_QUERY_RESULTS_SQL =
		"SELECT * FROM SearchResult WHERE searchQueryId = ?";

	private static final String _GET_SEARCH_RESULT_SQL =
		"SELECT * FROM SearchResult WHERE searchResultId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultDAO.class);

}