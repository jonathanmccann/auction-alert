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

import com.app.dao.impl.CategoryDAOImpl;
import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.dao.impl.SearchQueryPreviousResultDAOImpl;
import com.app.dao.impl.SearchResultDAOImpl;
import com.app.exception.DatabaseConnectionException;
import com.app.model.CategoryModel;
import com.app.model.SearchQueryModel;
import com.app.util.SearchQueryUtil;

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

		SearchQueryModel searchQueryModel = new SearchQueryModel();

		model.put("searchQueryModel", searchQueryModel);

		if (SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed()) {
			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			model.put("disabled", true);
		}

		Map<String, String> categories = new LinkedHashMap<>();

		for (CategoryModel category : _categoryDAOImpl.getCategories()) {
			categories.put(
				category.getCategoryId(), category.getCategoryName());
		}

		model.put("searchQueryCategories", categories);

		return "add_search_query";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.POST)
	public String addSearchQuery(
			@ModelAttribute("searchQueryModel")SearchQueryModel searchQueryModel,
			Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		if (SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed()) {
			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			return "redirect:add_search_query";
		}
		else {
			_searchQueryDAOImpl.addSearchQuery(
				searchQueryModel.getSearchQuery(),
				searchQueryModel.getCategoryId());

			List<SearchQueryModel> searchQueryModels =
				_searchQueryDAOImpl.getSearchQueries();

			model.put("searchQueryModels", searchQueryModels);

			return "redirect:view_search_queries";
		}
	}

	@RequestMapping(value = "/delete_search_query", method = RequestMethod.POST)
	public String deleteSearchQuery(String[] searchQueryIds)
		throws DatabaseConnectionException, SQLException {

		if ((searchQueryIds != null) && (searchQueryIds.length > 0)) {
			for (String searchQueryId : searchQueryIds) {
				int searchQueryIdInteger = Integer.parseInt(searchQueryId);

				_searchQueryDAOImpl.deleteSearchQuery(searchQueryIdInteger);

				_searchResultDAOImpl.deleteSearchQueryResults(
					searchQueryIdInteger);

				_searchQueryPreviousResultDAOImpl.
					deleteSearchQueryPreviousResults(searchQueryIdInteger);
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

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		model.put("searchQueryModels", searchQueryModels);

		return "view_search_queries";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryController.class);

	private static final CategoryDAOImpl _categoryDAOImpl =
		new CategoryDAOImpl();
	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();
	private static final SearchQueryPreviousResultDAOImpl
		_searchQueryPreviousResultDAOImpl =
			new SearchQueryPreviousResultDAOImpl();
	private static final SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

}