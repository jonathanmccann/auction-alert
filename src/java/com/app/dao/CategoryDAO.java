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
import com.app.model.Category;
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
public class CategoryDAO {

	@CacheEvict(value = "categories", allEntries = true)
	public void addCategories(List<Category> categories)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding {} categories", categories.size());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_CATEGORY_SQL)) {

			connection.setAutoCommit(false);

			for (Category category : categories) {
				preparedStatement.setString(1, category.getCategoryId());
				preparedStatement.setString(2, category.getCategoryName());

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();

			connection.commit();
		}
	}

	@CacheEvict(value = "categories", allEntries = true)
	public void deleteCategories()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting all categories");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_CATEGORIES_SQL)) {

			preparedStatement.executeUpdate();
		}
	}

	@Cacheable(value = "categories")
	public List<Category> getCategories()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all categories");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_CATEGORIES_SQL)) {

			ResultSet resultSet = preparedStatement.executeQuery();

			List<Category> categories = new ArrayList<>();

			while (resultSet.next()) {
				categories.add(createCategoryFromResultSet(resultSet));
			}

			return categories;
		}
	}

	private static Category createCategoryFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		Category category = new Category();

		category.setCategoryId(resultSet.getString("categoryId"));
		category.setCategoryName(resultSet.getString("categoryName"));

		return category;
	}

	private static final String _ADD_CATEGORY_SQL =
		"INSERT INTO Category(categoryId, categoryName) VALUES(?, ?)";

	private static final String _DELETE_CATEGORIES_SQL =
		"TRUNCATE TABLE Category";

	private static final String _GET_CATEGORIES_SQL = "SELECT * FROM Category";

	private static final Logger _log = LoggerFactory.getLogger(
		CategoryDAO.class);

}