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

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.dao.impl.SearchResultDAOImpl;
import com.app.model.SearchQueryModel;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import com.app.util.SearchQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
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
		throws SQLException {

		SearchQueryModel searchQueryModel = new SearchQueryModel();

		model.put("searchQueryModel", searchQueryModel);

		if (SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed()) {
			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			model.put("disabled", true);
		}

		return "add_search_query";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.POST)
	public String addSearchQuery(
			@ModelAttribute("searchQueryModel")SearchQueryModel searchQueryModel,
			Map<String, Object> model)
		throws SQLException {

		if (SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed()) {
			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			return "redirect:add_search_query";
		}
		else {
			_searchQueryDAOImpl.addSearchQuery(
				searchQueryModel.getSearchQuery());

			List<SearchQueryModel> searchQueryModels =
				_searchQueryDAOImpl.getSearchQueries();

			model.put("searchQueryModels", searchQueryModels);

			return "redirect:view_search_queries";
		}
	}

	@RequestMapping(value = "/delete_search_query", method = RequestMethod.POST)
	public String deleteSearchQuery(String[] searchQueryIds)
		throws SQLException {

		if ((searchQueryIds != null) && (searchQueryIds.length > 0)) {
			for (String searchQueryId : searchQueryIds) {
				int searchQueryIdInteger = Integer.valueOf(searchQueryId);

				_searchQueryDAOImpl.deleteSearchQuery(searchQueryIdInteger);

				_searchResultDAOImpl.deleteSearchQueryResults(
					searchQueryIdInteger);
			}
		}

		return "redirect:view_search_queries";
	}

	@RequestMapping(
		value = {
			"/query", "/search_query", "/view_search_query",
			"/view_search_queries"
		},
		method = RequestMethod.GET)
	public String viewSearchQueries(Map<String, Object> model)
		throws SQLException {

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		model.put("searchQueryModels", searchQueryModels);

		return "view_search_queries";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryController.class);

	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();
	private static final SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

}