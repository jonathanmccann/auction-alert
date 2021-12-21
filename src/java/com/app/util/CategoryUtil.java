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

package com.app.util;

import com.app.dao.CategoryDAO;
import com.app.exception.CategoryException;
import com.app.exception.DatabaseConnectionException;
import com.app.json.ebay.category.CategoryJsonResponse;
import com.app.json.ebay.category.ChildCategoryTreeNode;
import com.app.json.ebay.category.EbayCategory;
import com.app.json.ebay.category.RootCategoryNode;
import com.app.json.ebay.Error;
import com.app.model.Category;

import com.google.gson.Gson;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class CategoryUtil {

	public static void addCategories(List<Category> categories)
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.addCategories(categories);
	}

	public static void deleteCategories()
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.deleteCategories();
	}

	public static List<Category> getParentCategories()
		throws DatabaseConnectionException, SQLException {

		return _categoryDAO.getParentCategories();
	}

	public static List<Category> getSubcategories(String categoryParentId)
		throws DatabaseConnectionException, SQLException {

		return _categoryDAO.getSubcategories(categoryParentId);
	}

	public static void initializeCategories() throws Exception {
		Gson gson = new Gson();

		Map<String, String> categoryVersionResponse = gson.fromJson(
			OAuthTokenUtil.executeRequest(_GET_CATEGORY_VERSION_URL),
			Map.class);

		String categoryTreeVersion = categoryVersionResponse.get(
			_CATEGORY_TREE_VERSION_KEY);

		if (!_isNewerCategoryVersion(categoryTreeVersion)) {
			return;
		}

		_log.info(
			"Remove previous categories and inserting categories from " +
				"version: {}",
			categoryTreeVersion);

		deleteCategories();

		_populateCategories();

		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, categoryTreeVersion);
	}

	@Autowired
	public void setCategoryDAO(CategoryDAO categoryDAO) {
		_categoryDAO = categoryDAO;
	}

	private static boolean _isNewerCategoryVersion(String version)
		throws DatabaseConnectionException, SQLException {

		String releaseVersion = ReleaseUtil.getReleaseVersion(
			_CATEGORY_RELEASE_NAME);

		if (ValidatorUtil.isNull(releaseVersion)) {
			return true;
		}
		else if (ValidatorUtil.isNull(version)) {
			return false;
		}

		int latestVersion = Integer.valueOf(version);

		int currentVersion = Integer.valueOf(releaseVersion);

		if (latestVersion > currentVersion) {
			return true;
		}

		return false;
	}

	private static void _populateCategories() throws Exception {
		Gson gson = new Gson();

		CategoryJsonResponse categoryJsonResponse = gson.fromJson(
			OAuthTokenUtil.executeRequest(_GET_CATEGORIES_URL),
			CategoryJsonResponse.class);

		List<Error> errors = categoryJsonResponse.getErrors();

		if (!errors.isEmpty()) {
			for (Error error : errors) {
				_log.error(
					"Unable to populate categories." +
						"Received error ID: {} and error message: {}",
					error.getErrorId(), error.getLongMessage());
			}

			throw new CategoryException();
		}

		List<Category> categories = new ArrayList<>();

		RootCategoryNode rootCategoryNode =
			categoryJsonResponse.getRootCategoryNode();

		for (ChildCategoryTreeNode parentCategoryTreeNode :
				rootCategoryNode.getChildCategoryTreeNodes()) {

			EbayCategory parentCategory = parentCategoryTreeNode.getCategory();

			Category category = new Category(
				parentCategory.getCategoryId(),
				parentCategory.getCategoryName(),
				parentCategory.getCategoryId(), _PARENT_CATEGORY_LEVEL);

			categories.add(category);

			for (ChildCategoryTreeNode childCategoryTreeNode :
					parentCategoryTreeNode.getChildCategoryTreeNodes()) {

				EbayCategory childCategory =
					childCategoryTreeNode.getCategory();

				category = new Category(
					childCategory.getCategoryId(),
					childCategory.getCategoryName(),
					childCategory.getCategoryId(), _CHILD_CATEGORY_LEVEL);

				categories.add(category);
			}
		}

		categories = categories.stream()
			.sorted(Comparator.comparing(Category::getCategoryName))
			.collect(Collectors.toList());

		addCategories(categories);
	}

	private static final String _CATEGORY_RELEASE_NAME = "category";

	private static final String _CATEGORY_TREE_VERSION_KEY =
		"categoryTreeVersion";

	private static final int _CHILD_CATEGORY_LEVEL = 2;

	private static final String _GET_CATEGORIES_URL =
		"https://api.ebay.com/commerce/taxonomy/v1/category_tree/0";

	private static final String _GET_CATEGORY_VERSION_URL =
		"https://api.ebay.com/commerce/taxonomy/v1/" +
			"get_default_category_tree_id?marketplace_id=EBAY_US";

	private static final int _PARENT_CATEGORY_LEVEL = 1;

	private static final Logger _log = LoggerFactory.getLogger(
		CategoryUtil.class);

	private static CategoryDAO _categoryDAO;

}