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

import com.app.dao.impl.SearchQueryDAOImpl;

import java.sql.SQLException;

/**
 * @author Jonathan McCann
 */
public class SearchQueryUtil {

	public static boolean isExceedsTotalNumberOfSearchQueriesAllowed()
		throws SQLException {

		int searchQueryCount = _searchQueryDAOImpl.getSearchQueryCount();

		if ((searchQueryCount + 1) >
			PropertiesValues.TOTAL_NUMBER_OF_SEARCH_QUERIES_ALLOWED) {

			return true;
		}

		return false;
	}

	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();

}