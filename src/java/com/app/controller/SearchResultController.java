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
import com.app.model.SearchResultModel;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class SearchResultController {

	@RequestMapping(
		value = {
			"/result", "/search_result", "/view_search_result",
			"/view_search_results", "view_search_query_results"
		},
		method = RequestMethod.GET)
	public String viewSearchResults(Map<String, Object> model)
		throws SQLException {

		Map<String, List<SearchResultModel>> searchResultModelMap =
			new HashMap<>();

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		for (SearchQueryModel searchQueryModel : searchQueryModels) {
			int searchQueryId = searchQueryModel.getSearchQueryId();

			String searchQuery = searchQueryModel.getSearchQuery();

			List<SearchResultModel> searchResultModels =
				_searchResultDAOImpl.getSearchQueryResults(searchQueryId);

			if (!searchResultModels.isEmpty()) {
				searchResultModelMap.put(searchQuery, searchResultModels);
			}
		}

		model.put("searchResultModelMap", searchResultModelMap);

		return "view_search_query_results";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryController.class);

	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();
	private static final SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

}