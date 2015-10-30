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
import com.app.model.SearchQuery;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import com.app.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SearchQueryDAO {

	public int addSearchQuery(SearchQuery searchQuery)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding new searchQuery with keywords: {}",
			searchQuery.getKeywords());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement =
				connection.prepareStatement(
					_ADD_ADVANCED_SEARCH_QUERY_SQL,
					Statement.RETURN_GENERATED_KEYS)) {

			populateAddSearchQueryPreparedStatement(
				preparedStatement, searchQuery);

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	public int addSearchQuery(String searchKeywords)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Adding new searchQuery with keywords: {}", searchKeywords);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_SEARCH_QUERY_SQL, Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, searchKeywords);

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	public int addSearchQuery(String searchKeywords, String categoryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding new searchQuery with keywords: {} and category ID: {}",
			searchKeywords, categoryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement =
				connection.prepareStatement(
					_ADD_SEARCH_QUERY_WITH_CATEGORY_SQL,
					Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, searchKeywords);
			preparedStatement.setString(2, categoryId);

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	public void deleteSearchQueries()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting all search queries");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERIES_SQL)) {

			preparedStatement.executeUpdate();
		}
	}

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

	public List<SearchQuery> getSearchQueries()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all search queries");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERIES_SQL);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<SearchQuery> searchQueries = new ArrayList<>();

			while (resultSet.next()) {
				searchQueries.add(createSearchQueryFromResultSet(resultSet));
			}

			return searchQueries;
		}
	}

	public SearchQuery getSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search query ID: {}", searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_SQL)) {

			preparedStatement.setInt(1, searchQueryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return createSearchQueryFromResultSet(resultSet);
				}
				else {
					throw new SQLException();
				}
			}
		}
	}

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

	public void updateSearchQuery(SearchQuery searchQuery)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating search query ID: {} ", searchQuery.getSearchQueryId());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_ADVANCED_SEARCH_QUERY_SQL)) {

			populateAddSearchQueryPreparedStatement(
				preparedStatement, searchQuery);

			preparedStatement.setInt(12, searchQuery.getSearchQueryId());

			preparedStatement.executeUpdate();
		}
	}

	public void updateSearchQuery(int searchQueryId, String keywords)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating search query ID: {} to keywords: {}", searchQueryId,
			keywords);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_SEARCH_QUERY_SQL)) {

			preparedStatement.setString(1, keywords);
			preparedStatement.setInt(2, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	public void updateSearchQuery(
			int searchQueryId, String keywords, String categoryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating search query ID: {} to keywords: {} with category ID: {}",
			searchQueryId, keywords, categoryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_SEARCH_QUERY_WITH_CATEGORY_SQL)) {

			preparedStatement.setString(1, keywords);
			preparedStatement.setString(2, categoryId);
			preparedStatement.setInt(3, searchQueryId);

			preparedStatement.executeUpdate();
		}
	}

	private static SearchQuery createSearchQueryFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(resultSet.getInt("searchQueryId"));
		searchQuery.setKeywords(resultSet.getString("keywords"));
		searchQuery.setCategoryId(resultSet.getString("categoryId"));
		searchQuery.setSearchDescription(
			resultSet.getBoolean("searchDescription"));
		searchQuery.setFreeShippingOnly(
			resultSet.getBoolean("freeShippingOnly"));
		searchQuery.setNewCondition(resultSet.getBoolean("newCondition"));
		searchQuery.setUsedCondition(resultSet.getBoolean("usedCondition"));
		searchQuery.setUnspecifiedCondition(
			resultSet.getBoolean("unspecifiedCondition"));
		searchQuery.setAuctionListing(resultSet.getBoolean("auctionListing"));
		searchQuery.setFixedPriceListing(
			resultSet.getBoolean("fixedPriceListing"));
		searchQuery.setMaxPrice(resultSet.getDouble("maxPrice"));
		searchQuery.setMinPrice(resultSet.getDouble("minPrice"));

		return searchQuery;
	}

	private static void populateAddSearchQueryPreparedStatement(
			PreparedStatement preparedStatement,
			SearchQuery searchQuery)
		throws SQLException {

		preparedStatement.setString(1, searchQuery.getKeywords());
		preparedStatement.setString(2, searchQuery.getCategoryId());
		preparedStatement.setBoolean(3, searchQuery.isSearchDescription());
		preparedStatement.setBoolean(4, searchQuery.isFreeShippingOnly());
		preparedStatement.setBoolean(5, searchQuery.isNewCondition());
		preparedStatement.setBoolean(6, searchQuery.isUsedCondition());
		preparedStatement.setBoolean(7, searchQuery.isUnspecifiedCondition());
		preparedStatement.setBoolean(8, searchQuery.isAuctionListing());
		preparedStatement.setBoolean(9, searchQuery.isFixedPriceListing());
		preparedStatement.setDouble(10, searchQuery.getMaxPrice());
		preparedStatement.setDouble(11, searchQuery.getMinPrice());
	}

	private static final String _ADD_ADVANCED_SEARCH_QUERY_SQL =
		"INSERT INTO SearchQuery(keywords, categoryId, " +
			"searchDescription, freeShippingOnly, newCondition, " +
				"usedCondition, unspecifiedCondition, auctionListing, " +
					"fixedPriceListing, maxPrice, minPrice) VALUES(?, " +
						"?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String _ADD_SEARCH_QUERY_SQL =
		"INSERT INTO SearchQuery(keywords) VALUES(?)";

	private static final String _ADD_SEARCH_QUERY_WITH_CATEGORY_SQL =
		"INSERT INTO SearchQuery(keywords, categoryId) VALUES(?, ?)";

	private static final String _DELETE_SEARCH_QUERIES_SQL =
		"TRUNCATE TABLE SearchQuery";

	private static final String _DELETE_SEARCH_QUERY_SQL =
		"DELETE FROM SearchQuery WHERE searchQueryId = ?";

	private static final String _GET_SEARCH_QUERIES_SQL =
		"SELECT * FROM SearchQuery";

	private static final String _GET_SEARCH_QUERY_COUNT_SQL =
		"SELECT COUNT(*) FROM SearchQuery";

	private static final String _GET_SEARCH_QUERY_SQL =
		"SELECT * FROM SearchQuery WHERE searchQueryId = ?";

	private static final String _UPDATE_ADVANCED_SEARCH_QUERY_SQL =
		"UPDATE SearchQuery SET keywords = ?, categoryId = ?, " +
			"searchDescription = ?, freeShippingOnly = ?, newCondition = ?, " +
				"usedCondition = ?, unspecifiedCondition = ?, " +
					"auctionListing = ?, fixedPriceListing = ?, maxPrice = ?, " +
						"minPrice = ? WHERE searchQueryId = ?";

	private static final String _UPDATE_SEARCH_QUERY_SQL =
		"UPDATE SearchQuery SET keywords = ? WHERE searchQueryId = ?";

	private static final String _UPDATE_SEARCH_QUERY_WITH_CATEGORY_SQL =
		"UPDATE SearchQuery SET keywords = ?, categoryId = ? WHERE " +
			"searchQueryId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryDAO.class);

}