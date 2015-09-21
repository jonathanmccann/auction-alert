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

import com.app.dao.impl.CategoryDAOImpl;
import com.app.dao.impl.ReleaseDAOImpl;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

/**
 * @author Jonathan McCann
 */
public class CategoryUtil {

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

		String version = getCategoriesCall.getReturnedCategoryVersion();

		if (!version.equals(
			_releaseDAOImpl.getReleaseVersion(_CATEGORY_RELEASE_NAME))) {

			_categoryDAOImpl.deleteCategories();

			CategoryType[] ebayCategories = getCategoriesCall.getCategories();

			for (CategoryType categoryType : ebayCategories) {
				_categoryDAOImpl.addCategory(
					categoryType.getCategoryID(),
					categoryType.getCategoryName());
			}

			_releaseDAOImpl.addRelease(_CATEGORY_RELEASE_NAME, version);
		}
	}

	private static final CategoryDAOImpl _categoryDAOImpl =
		new CategoryDAOImpl();
	private static final ReleaseDAOImpl _releaseDAOImpl = new ReleaseDAOImpl();

	private static final int _ROOT_CATEGORY_LEVEL_LIMIT = 1;

	private static final String _CATEGORY_RELEASE_NAME = "category";

}