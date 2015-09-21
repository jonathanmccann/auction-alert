/**
 * Copyright (c) 2015-present Jonathan McCann
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

import com.app.dao.CategoryDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.CategoryModel;
import com.app.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
public class CategoryDAOImpl implements CategoryDAO {

	@Override
	public void addCategory(String categoryId, String categoryName)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding new category with ID {} and name {}", categoryId,
			categoryName);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_CATEGORY_SQL)) {

			preparedStatement.setString(1, categoryId);
			preparedStatement.setString(2, categoryName);

			preparedStatement.executeUpdate();
		}
	}

	@Override
	public void deleteCategory(String categoryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting category ID: {}", categoryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_CATEGORY_SQL)) {

			preparedStatement.setString(1, categoryId);

			preparedStatement.executeUpdate();
		}
	}

	@Override
	public void deleteCategories()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting all categories");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_CATEGORIES_SQL)) {

			preparedStatement.executeUpdate();
		}
	}

	@Override
	public CategoryModel getCategory(String categoryId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting category ID: {}", categoryId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_CATEGORY_SQL)) {

			preparedStatement.setString(1, categoryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return createCategoryFromResultSet(resultSet);
				}
				else {
					return new CategoryModel();
				}
			}
		}
	}

	@Override
	public List<CategoryModel> getCategories()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all categories");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_CATEGORIES_SQL)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<CategoryModel> categoryModels = new ArrayList<>();

				while (resultSet.next()) {
					categoryModels.add(createCategoryFromResultSet(resultSet));
				}

				return categoryModels;
			}
		}
	}

	private static CategoryModel createCategoryFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		CategoryModel categoryModel = new CategoryModel();

		categoryModel.setCategoryId(resultSet.getString("categoryId"));
		categoryModel.setCategoryName(resultSet.getString("categoryName"));

		return categoryModel;
	}

	private static final String _ADD_CATEGORY_SQL =
		"INSERT INTO Category(categoryId, categoryName) VALUES(?, ?)";

	private static final String _DELETE_CATEGORY_SQL =
		"DELETE FROM Category WHERE categoryId = ?";

	private static final String _DELETE_CATEGORIES_SQL =
		"TRUNCATE TABLE Category";

	private static final String _GET_CATEGORY_SQL =
		"SELECT * FROM Category WHERE categoryId = ?";

	private static final String _GET_CATEGORIES_SQL =
		"SELECT * FROM Category";

	private static final Logger _log = LoggerFactory.getLogger(
		CategoryDAOImpl.class);
}