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
import com.app.exception.DatabaseConnectionException;
import com.app.model.CategoryModel;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class CategoryUtil {

	public static void addCategory(String categoryId, String categoryName)
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.addCategory(categoryId, categoryName);
	}

	public static void deleteCategories()
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.deleteCategories();
	}

	public static void deleteCategory(String categoryId)
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.deleteCategory(categoryId);
	}

	public static List<CategoryModel> getCategories()
		throws DatabaseConnectionException, SQLException {

 		return _categoryDAO.getCategories();
	}

	public static CategoryModel getCategory(String categoryId)
		throws DatabaseConnectionException, SQLException {

		return _categoryDAO.getCategory(categoryId);
	}

	public static void initializeCategories() throws Exception {
		ApiContext apiContext = eBayAPIUtil.getApiContext();
		GetCategoriesCall getCategoriesCall = new GetCategoriesCall(apiContext);

		DetailLevelCodeType[] detailLevelCodeTypes = {
			DetailLevelCodeType.RETURN_ALL
		};

		getCategoriesCall.setCategorySiteID(SiteCodeType.US);
		getCategoriesCall.setDetailLevel(detailLevelCodeTypes);
		getCategoriesCall.setLevelLimit(_ROOT_CATEGORY_LEVEL_LIMIT);
		getCategoriesCall.setViewAllNodes(true);

		CategoryType[] ebayCategories = getCategoriesCall.getCategories();

		String version = getCategoriesCall.getReturnedCategoryVersion();

		if (!version.equals(
				ReleaseUtil.getReleaseVersion(_CATEGORY_RELEASE_NAME))) {

			_log.info(
				"Remove previous categories and inserting categories from " +
					"version: {}",
				version);

			deleteCategories();

			for (CategoryType categoryType : ebayCategories) {
				addCategory(
					categoryType.getCategoryID(),
					categoryType.getCategoryName());
			}

			ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, version);
		}
	}

	@Autowired
	public void setCategoryDAO(CategoryDAO categoryDAO) {
		_categoryDAO = categoryDAO;
	}

	private static CategoryDAO _categoryDAO;

	private static final String _CATEGORY_RELEASE_NAME = "category";

	private static final int _ROOT_CATEGORY_LEVEL_LIMIT = 1;

	private static final Logger _log = LoggerFactory.getLogger(
		CategoryUtil.class);

}