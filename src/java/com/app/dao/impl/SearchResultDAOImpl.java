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

import com.app.dao.SearchResultDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchResultModel;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SearchResultDAOImpl implements SearchResultDAO {

	@Override
	public void addSearchResult(SearchResultModel searchResultModel)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding new search result: {}", searchResultModel.getItemId());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_SEARCH_RESULT_SQL)) {

			populateAddSearchResultPreparedStatement(
				preparedStatement, searchResultModel);

			preparedStatement.executeUpdate();
		}
	}

	@Override
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

	@Override
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

	@Override
	public List<SearchResultModel> getSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting search query results for search query ID: {}",
			searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_RESULTS_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<SearchResultModel> searchResults = new ArrayList<>();

				while (resultSet.next()) {
					searchResults.add(
						createSearchResultFromResultSet(resultSet));
				}

				return searchResults;
			}
		}
	}

	@Override
	public SearchResultModel getSearchResult(int searchResultId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search result for ID: {}", searchResultId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_RESULT_SQL)) {

			preparedStatement.setInt(1, searchResultId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return createSearchResultFromResultSet(resultSet);
				}
				else {
					return new SearchResultModel();
				}
			}
		}
	}

	@Override
	public List<SearchResultModel> getSearchResults()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all search results");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_RESULTS_SQL)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<SearchResultModel> searchResults = new ArrayList<>();

				while (resultSet.next()) {
					searchResults.add(
						createSearchResultFromResultSet(resultSet));
				}

				return searchResults;
			}
		}
	}

	private static SearchResultModel createSearchResultFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		SearchResultModel searchResult = new SearchResultModel();

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
			SearchResultModel searchResultModel)
		throws SQLException {

		preparedStatement.setInt(1, searchResultModel.getSearchQueryId());
		preparedStatement.setString(2, searchResultModel.getItemId());
		preparedStatement.setString(3, searchResultModel.getItemTitle());
		preparedStatement.setString(4, searchResultModel.getTypeOfAuction());
		preparedStatement.setString(5, searchResultModel.getItemURL());
		preparedStatement.setString(6, searchResultModel.getGalleryURL());

		Date endingTime = searchResultModel.getEndingTime();

		preparedStatement.setLong(7, endingTime.getTime());
		preparedStatement.setDouble(8, searchResultModel.getAuctionPrice());
		preparedStatement.setDouble(9, searchResultModel.getFixedPrice());
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

	private static final String _GET_SEARCH_RESULTS_SQL =
		"SELECT * FROM SearchResult";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultDAOImpl.class);

}