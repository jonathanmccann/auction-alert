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
import com.app.model.Category;
import com.app.model.SearchQuery;
import com.app.util.CategoryUtil;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import java.sql.SQLException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class SearchQueryController {

	@RequestMapping(value = "/add_search_query", method = RequestMethod.GET)
	public String addSearchQuery(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		SearchQuery searchQuery = new SearchQuery();

		model.put("searchQuery", searchQuery);

		if (SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed()) {
			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			model.put("disabled", true);
		}

		Map<String, String> categories = new LinkedHashMap<>();

		for (Category category : CategoryUtil.getCategories()) {
			categories.put(
				category.getCategoryId(), category.getCategoryName());
		}

		model.put("searchQueryCategories", categories);

		return "add_search_query";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.POST)
	public String addSearchQuery(
			@ModelAttribute("searchQuery")SearchQuery searchQuery,
			Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		if (SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed()) {
			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			return "redirect:add_search_query";
		}
		else {
			String categoryId = searchQuery.getCategoryId();

			if ((categoryId == null) || categoryId.equals("")) {
				SearchQueryUtil.addSearchQuery(
					searchQuery.getKeywords());
			}
			else {
				SearchQueryUtil.addSearchQuery(
					searchQuery.getKeywords(),
					searchQuery.getCategoryId());
			}

			List<SearchQuery> searchQueries =
				SearchQueryUtil.getSearchQueries();

			model.put("searchQueries", searchQueries);

			return "redirect:view_search_queries";
		}
	}

	@RequestMapping(value = "/delete_search_query", method = RequestMethod.POST)
	public String deleteSearchQuery(String[] searchQueryIds)
		throws DatabaseConnectionException, SQLException {

		if ((searchQueryIds != null) && (searchQueryIds.length > 0)) {
			for (String searchQueryId : searchQueryIds) {
				int searchQueryIdInteger = Integer.parseInt(searchQueryId);

				SearchQueryUtil.deleteSearchQuery(searchQueryIdInteger);

				SearchResultUtil.deleteSearchQueryResults(
					searchQueryIdInteger);

				SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(
					searchQueryIdInteger);
			}
		}

		return "redirect:view_search_queries";
	}

	@ExceptionHandler(Exception.class)
	public String handleError(HttpServletRequest request, Exception exception) {
		_log.error("Request: {}", request.getRequestURL(), exception);

		return "redirect:error.jsp";
	}

	@RequestMapping(
		value = {
			"/query", "/search_query", "/view_search_query",
			"/view_search_queries"
		},
		method = RequestMethod.GET
	)
	public String viewSearchQueries(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		List<SearchQuery> searchQueries =
			SearchQueryUtil.getSearchQueries();

		model.put("searchQueries", searchQueries);

		return "view_search_queries";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryController.class);

}