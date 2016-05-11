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
import com.app.model.Category;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

import java.sql.SQLException;
import java.util.ArrayList;
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

	public static void addCategories(List<Category> categories)
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.addCategories(categories);
	}

	public static void deleteCategories()
		throws DatabaseConnectionException, SQLException {

		_categoryDAO.deleteCategories();
	}

	public static List<Category> getCategories()
		throws DatabaseConnectionException, SQLException {

 		return _categoryDAO.getCategories();
	}

	public static void initializeCategories() throws Exception {
		populateCategories(createGetCategoriesCall());
	}

	private static GetCategoriesCall createGetCategoriesCall() {
		ApiContext apiContext = eBayAPIUtil.getApiContext();

		GetCategoriesCall getCategoriesCall = new GetCategoriesCall(apiContext);

		DetailLevelCodeType[] detailLevelCodeTypes = {
			DetailLevelCodeType.RETURN_ALL
		};

		getCategoriesCall.setCategorySiteID(SiteCodeType.US);
		getCategoriesCall.setDetailLevel(detailLevelCodeTypes);
		getCategoriesCall.setLevelLimit(_ROOT_CATEGORY_LEVEL_LIMIT);
		getCategoriesCall.setViewAllNodes(true);

		return getCategoriesCall;
	}

	private static boolean isNewerCategoryVersion(String version)
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

	private static void populateCategories(GetCategoriesCall getCategoriesCall)
		throws Exception {

		CategoryType[] ebayCategories = getCategoriesCall.getCategories();

		String version = getCategoriesCall.getReturnedCategoryVersion();

		if (isNewerCategoryVersion(version)) {
			_log.info(
				"Remove previous categories and inserting categories from " +
					"version: {}",
				version);

			deleteCategories();

			List<Category> categories = new ArrayList<>();

			for (CategoryType categoryType : ebayCategories) {
				Category category = new Category(
					categoryType.getCategoryID(),
					categoryType.getCategoryName());

				categories.add(category);
			}

			addCategories(categories);

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