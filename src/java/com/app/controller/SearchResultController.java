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
import com.app.util.UserUtil;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class SearchResultController {

	@ExceptionHandler(Exception.class)
	public String handleError(HttpServletRequest request, Exception exception) {
		_log.error("Request: {}", request.getRequestURL(), exception);

		return "redirect:error.jsp";
	}

	@RequestMapping(
		value = "view_search_query_results", method = RequestMethod.GET
	)
	public String viewSearchResults(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		Map<String, List<SearchResult>> searchResultMap = new HashMap<>();

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			UserUtil.getCurrentUserId(), true);

		_log.debug("Found {} search query results", searchQueries.size());

		for (SearchQuery searchQuery : searchQueries) {
			int searchQueryId = searchQuery.getSearchQueryId();

			String keywords = searchQuery.getKeywords();

			List<SearchResult> searchResults =
				SearchResultUtil.getSearchQueryResults(searchQueryId);

			if (!searchResults.isEmpty()) {
				searchResultMap.put(keywords, searchResults);
			}
		}

		model.put("searchResultMap", searchResultMap);

		return "view_search_query_results";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultController.class);

}