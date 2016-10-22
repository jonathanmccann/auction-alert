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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

/**
 * @author Jonathan McCann
 */
public class SearchQueryDAO {

	@Caching(
		evict = {
			@CacheEvict(value = "searchQueries", key = "#userId + 'true'"),
			@CacheEvict(value = "searchQueries", key = "#userId + 'false'")
		}
	)
	public void activateSearchQuery(int userId, int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Activating search query ID: {}", searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ACTIVATION_SEARCH_QUERY_SQL)) {

			preparedStatement.setBoolean(1, true);
			preparedStatement.setInt(2, searchQueryId);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	@Caching(
		evict = {
			@CacheEvict(value = "searchQueries", key = "#searchQuery.userId + 'true'"),
			@CacheEvict(value = "searchQueries", key = "#searchQuery.userId + 'false'")
		}
	)
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

			_populateAddSearchQueryPreparedStatement(
				preparedStatement, searchQuery);

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	@Caching(
		evict = {
			@CacheEvict(value = "searchQueries", key = "#userId + 'true'"),
			@CacheEvict(value = "searchQueries", key = "#userId + 'false'")
		}
	)
	public void deactivateSearchQuery(int userId, int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deactivating search query ID: {}", searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ACTIVATION_SEARCH_QUERY_SQL)) {

			preparedStatement.setBoolean(1, false);
			preparedStatement.setInt(2, searchQueryId);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	@Caching(
		evict = {
			@CacheEvict(value = "searchQueries", key = "#userId + 'true'"),
			@CacheEvict(value = "searchQueries", key = "#userId + 'false'")
		}
	)
	public void deleteSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting all search queries for userId: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERIES_SQL)) {

			preparedStatement.setInt(1, userId);

			preparedStatement.executeUpdate();
		}
	}

	@Caching(
		evict = {
			@CacheEvict(value = "searchQueries", key = "#userId + 'true'"),
			@CacheEvict(value = "searchQueries", key = "#userId + 'false'")
		}
	)
	public void deleteSearchQuery(int userId, int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting search query ID: {}", searchQueryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_SEARCH_QUERY_SQL)) {

			preparedStatement.setInt(1, searchQueryId);
			preparedStatement.setInt(2, userId);

			preparedStatement.executeUpdate();
		}
	}

	public List<SearchQuery> getSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting all search queries for userId: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERIES_BY_USER_ID_SQL)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			List<SearchQuery> searchQueries = new ArrayList<>();

			while (resultSet.next()) {
				searchQueries.add(_createSearchQueryFromResultSet(resultSet));
			}

			return searchQueries;
		}
	}

	@Cacheable(value = "searchQueries", key = "#userId + #active.toString()")
	public List<SearchQuery> getSearchQueries(int userId, boolean active)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting all search queries for userId: {} and active: {}", userId,
			active);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERIES_BY_USER_ID_AND_ACTIVE_SQL)) {

			preparedStatement.setInt(1, userId);
			preparedStatement.setBoolean(2, active);

			ResultSet resultSet = preparedStatement.executeQuery();

			List<SearchQuery> searchQueries = new ArrayList<>();

			while (resultSet.next()) {
				searchQueries.add(_createSearchQueryFromResultSet(resultSet));
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

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return _createSearchQueryFromResultSet(resultSet);
			}
			else {
				throw new SQLException(
					"There is no search query for ID: " + searchQueryId);
			}
		}
	}

	public int getSearchQueryCount(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting search query count for userId: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_SEARCH_QUERY_COUNT_SQL)) {

			preparedStatement.setInt(1, userId);

			int searchQueryCount = 0;

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				searchQueryCount = resultSet.getInt(1);
			}

			return searchQueryCount;
		}
	}

	@Caching(
		evict = {
			@CacheEvict(value = "searchQueries", key = "#userId + 'true'"),
			@CacheEvict(value = "searchQueries", key = "#userId + 'false'")
		}
	)
	public void updateSearchQuery(int userId, SearchQuery searchQuery)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating search query ID: {} ", searchQuery.getSearchQueryId());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_ADVANCED_SEARCH_QUERY_SQL)) {

			_populateUpdateSearchQueryPreparedStatement(
				preparedStatement, searchQuery, userId);

			preparedStatement.executeUpdate();
		}
	}

	private static SearchQuery _createSearchQueryFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(resultSet.getInt("searchQueryId"));
		searchQuery.setUserId(resultSet.getInt("userId"));
		searchQuery.setKeywords(resultSet.getString("keywords"));
		searchQuery.setCategoryId(resultSet.getString("categoryId"));
		searchQuery.setSubcategoryId(resultSet.getString("subcategoryId"));
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
		searchQuery.setGlobalId((resultSet.getString("globalId")));
		searchQuery.setActive(resultSet.getBoolean("active"));

		return searchQuery;
	}

	private static void _populateAddSearchQueryPreparedStatement(
			PreparedStatement preparedStatement, SearchQuery searchQuery)
		throws SQLException {

		preparedStatement.setInt(1, searchQuery.getUserId());
		preparedStatement.setString(2, searchQuery.getKeywords());
		preparedStatement.setString(3, searchQuery.getCategoryId());
		preparedStatement.setString(4, searchQuery.getSubcategoryId());
		preparedStatement.setBoolean(5, searchQuery.isSearchDescription());
		preparedStatement.setBoolean(6, searchQuery.isFreeShippingOnly());
		preparedStatement.setBoolean(7, searchQuery.isNewCondition());
		preparedStatement.setBoolean(8, searchQuery.isUsedCondition());
		preparedStatement.setBoolean(9, searchQuery.isUnspecifiedCondition());
		preparedStatement.setBoolean(10, searchQuery.isAuctionListing());
		preparedStatement.setBoolean(11, searchQuery.isFixedPriceListing());
		preparedStatement.setDouble(12, searchQuery.getMaxPrice());
		preparedStatement.setDouble(13, searchQuery.getMinPrice());
		preparedStatement.setString(14, searchQuery.getGlobalId());
		preparedStatement.setBoolean(15, searchQuery.isActive());
	}

	private static void _populateUpdateSearchQueryPreparedStatement(
			PreparedStatement preparedStatement, SearchQuery searchQuery,
			int userId)
		throws SQLException {

		preparedStatement.setString(1, searchQuery.getKeywords());
		preparedStatement.setString(2, searchQuery.getCategoryId());
		preparedStatement.setString(3, searchQuery.getSubcategoryId());
		preparedStatement.setBoolean(4, searchQuery.isSearchDescription());
		preparedStatement.setBoolean(5, searchQuery.isFreeShippingOnly());
		preparedStatement.setBoolean(6, searchQuery.isNewCondition());
		preparedStatement.setBoolean(7, searchQuery.isUsedCondition());
		preparedStatement.setBoolean(8, searchQuery.isUnspecifiedCondition());
		preparedStatement.setBoolean(9, searchQuery.isAuctionListing());
		preparedStatement.setBoolean(10, searchQuery.isFixedPriceListing());
		preparedStatement.setDouble(11, searchQuery.getMaxPrice());
		preparedStatement.setDouble(12, searchQuery.getMinPrice());
		preparedStatement.setString(13, searchQuery.getGlobalId());
		preparedStatement.setBoolean(14, searchQuery.isActive());
		preparedStatement.setInt(15, searchQuery.getSearchQueryId());
		preparedStatement.setInt(16, userId);
	}

	private static final String _ACTIVATION_SEARCH_QUERY_SQL =
		"UPDATE SearchQuery SET active = ? WHERE searchQueryId = ? and userId = ?";

	private static final String _ADD_ADVANCED_SEARCH_QUERY_SQL =
		"INSERT INTO SearchQuery(userId, keywords, categoryId, subcategoryId, " +
			"searchDescription, freeShippingOnly, newCondition, " +
				"usedCondition, unspecifiedCondition, auctionListing, " +
					"fixedPriceListing, maxPrice, minPrice, globalId, active) " +
						"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String _DELETE_SEARCH_QUERIES_SQL =
		"DELETE FROM SearchQuery WHERE userId = ?";

	private static final String _DELETE_SEARCH_QUERY_SQL =
		"DELETE FROM SearchQuery WHERE searchQueryId = ? AND userId = ?";

	private static final String _GET_SEARCH_QUERIES_BY_USER_ID_SQL =
		"SELECT * FROM SearchQuery WHERE userId = ?";

	private static final String _GET_SEARCH_QUERIES_BY_USER_ID_AND_ACTIVE_SQL =
		"SELECT * FROM SearchQuery WHERE userId = ? and active = ?";

	private static final String _GET_SEARCH_QUERY_COUNT_SQL =
		"SELECT COUNT(*) FROM SearchQuery WHERE userId = ?";

	private static final String _GET_SEARCH_QUERY_SQL =
		"SELECT * FROM SearchQuery WHERE searchQueryId = ?";

	private static final String _UPDATE_ADVANCED_SEARCH_QUERY_SQL =
		"UPDATE SearchQuery SET keywords = ?, categoryId = ?, subcategoryId = ?, " +
			"searchDescription = ?, freeShippingOnly = ?, newCondition = ?, " +
				"usedCondition = ?, unspecifiedCondition = ?, " +
					"auctionListing = ?, fixedPriceListing = ?, maxPrice = ?, " +
						"minPrice = ?, globalId = ?, active = ? WHERE searchQueryId = ? " +
							"AND userId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryDAO.class);

}