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
import com.app.exception.SearchQueryException;
import com.app.language.LanguageUtil;
import com.app.model.Category;
import com.app.model.SearchQuery;
import com.app.util.CategoryUtil;
import com.app.util.PropertiesValues;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;
import com.app.util.UserUtil;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Jonathan McCann
 */
@Controller
public class SearchQueryController {

	@RequestMapping(
		value = "/activate_search_query", method = RequestMethod.POST
	)
	public String activateSearchQuery(
			@RequestParam("searchQueryId")int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		SearchQueryUtil.activateSearchQuery(
			UserUtil.getCurrentUserId(), searchQueryId);

		return "redirect:view_search_queries";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.POST)
	public String addSearchQuery(
			@ModelAttribute("searchQuery")SearchQuery searchQuery,
			RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		try {
			SearchQueryUtil.addSearchQuery(searchQuery);
		}
		catch (SearchQueryException sqe) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-search-query"));

			return "redirect:add_search_query";
		}

		return "redirect:view_search_queries";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.GET)
	public String addSearchQuery(
			@ModelAttribute("error")String error, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(UserUtil.getCurrentUserId());

		model.put("searchQuery", searchQuery);

		model.put("error", error);

		if (SearchQueryUtil.exceedsMaximumNumberOfSearchQueries(
				UserUtil.getCurrentUserId())) {

			_log.debug(
				"Unable to add more search queries since it would exceed the " +
					"total number of search queries allowed");

			model.put("disabled", true);
			model.put(
				"info",
				LanguageUtil.getMessage("reached-maximum-search-queries"));
		}

		populateCategories(model);

		model.put("isAdd", true);

		return "add_search_query";
	}

	@RequestMapping(
		value = "/deactivate_search_query", method = RequestMethod.POST
	)
	public String deactivateSearchQuery(
			@RequestParam("searchQueryId")int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		SearchQueryUtil.deactivateSearchQuery(
			UserUtil.getCurrentUserId(), searchQueryId);

		return "redirect:view_search_queries";
	}

	@RequestMapping(value = "/delete_search_query", method = RequestMethod.POST)
	public String deleteSearchQuery(
			@RequestParam("searchQueryId")int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		int userId = UserUtil.getCurrentUserId();

		if (searchQuery.getUserId() == userId) {
			SearchQueryUtil.deleteSearchQuery(userId, searchQueryId);

			SearchResultUtil.deleteSearchQueryResults(searchQueryId);

			SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(
				searchQueryId);
		}

		return "redirect:view_search_queries";
	}

	@RequestMapping(value = "/update_search_query", method = RequestMethod.POST)
	public String updateSearchQuery(
			@ModelAttribute("searchQuery")SearchQuery searchQuery,
			RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		try {
			SearchQueryUtil.updateSearchQuery(
				UserUtil.getCurrentUserId(), searchQuery);
		}
		catch (SearchQueryException sqe) {
			redirectAttributes.addAttribute(
				"searchQueryId", searchQuery.getSearchQueryId());
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-search-query"));

			return "redirect:update_search_query";
		}

		return "redirect:view_search_queries";
	}

	@RequestMapping(value = "/update_search_query", method = RequestMethod.GET)
	public String updateSearchQuery(
			@ModelAttribute("error")String error,
			@RequestParam("searchQueryId")int searchQueryId,
			Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		if (searchQuery.getUserId() == UserUtil.getCurrentUserId()) {
			model.put("searchQuery", searchQuery);

			populateCategories(model);
		}

		model.put("error", error);

		return "add_search_query";
	}

	@RequestMapping(value = "/view_search_queries", method = RequestMethod.GET)
	public String viewSearchQueries(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		List<SearchQuery> activeSearchQueries =
			SearchQueryUtil.getSearchQueries(UserUtil.getCurrentUserId(), true);

		List<SearchQuery> inactiveSearchQueries =
			SearchQueryUtil.getSearchQueries(
				UserUtil.getCurrentUserId(), false);

		model.put("activeSearchQueries", activeSearchQueries);
		model.put("inactiveSearchQueries", inactiveSearchQueries);

		return "view_search_queries";
	}

	@RequestMapping(value = "/subcategories", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getParentSubcategories(
			String categoryParentId)
		throws DatabaseConnectionException, SQLException {

		Map<String, String> subcategories = new HashMap<>();

		for (Category subcategory :
				CategoryUtil.getSubcategories(categoryParentId)) {

			subcategories.put(
				subcategory.getCategoryName(), subcategory.getCategoryId());
		}

		return subcategories;
	}

	/*
	java.lang.IllegalStateException: Optional int parameter 'searchQueryId' is
	present but cannot be translated into a null value due to being declared as
	a primitive type. Consider declaring it as object wrapper for the corresponding
	primitive type.
	 */
	@RequestMapping(value = "/monitor", method = RequestMethod.GET)
	public String monitor(Integer searchQueryId, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		if (searchQueryId != null) {
			SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
				searchQueryId);

			if (searchQuery.getUserId() == UserUtil.getCurrentUserId()) {
				model.put("searchQuery", searchQuery);
			}
			else {
				model.put("searchQuery", new SearchQuery());
			}
		}
		else {
			model.put("searchQuery", new SearchQuery());
		}

		populateCategories(model);

		model.put("campaignId", PropertiesValues.EBAY_CAMPAIGN_ID);

		return "monitor";
	}

	private void populateCategories(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		if (_CATEGORIES.isEmpty()) {
			for (Category parentCategory : CategoryUtil.getParentCategories()) {
				_CATEGORIES.put(
					parentCategory.getCategoryId(),
					parentCategory.getCategoryName());
			}
		}

		model.put("searchQueryCategories", _CATEGORIES);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryController.class);

	private static final Map<String, String> _CATEGORIES = new LinkedHashMap<>();

}