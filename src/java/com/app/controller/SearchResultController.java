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

package com.app.controller;

import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import java.sql.SQLException;

import java.util.Collections;
import java.util.List;

import com.app.util.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jonathan McCann
 */
@Controller
public class SearchResultController {

	@RequestMapping(
		value = "/search_query_results", method = RequestMethod.GET,
		produces = "application/json"
	)
	public @ResponseBody List<SearchResult> getSearchQueryResults(
			int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		int userId = UserUtil.getCurrentUserId();

		if (searchQuery.getUserId() == userId) {
			List<SearchResult> searchResults =
				SearchResultUtil.getSearchQueryResults(searchQueryId);

			Collections.reverse(searchResults);

			return searchResults;
		}

		return Collections.emptyList();
	}

}