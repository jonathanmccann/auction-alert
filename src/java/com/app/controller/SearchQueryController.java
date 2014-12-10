package com.app.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.model.SearchQueryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchQueryController {

	@RequestMapping(
		value = {
			"/query", "/search_query", "/view_search_query",
			"/view_search_queries"
		},
		method = RequestMethod.GET)
	public String viewSearchQueries(Map<String, Object> model) throws SQLException {
		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		model.put("searchQueryModels", searchQueryModels);

 		return "view_search_queries";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.GET)
	public String addSearchQuery(Map<String, Object> model) {
		SearchQueryModel searchQueryModel = new SearchQueryModel();

		model.put("searchQueryModel", searchQueryModel);

		return "add_search_query";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.POST)
	public String addSearchQuery(
			@ModelAttribute("searchQueryModel") SearchQueryModel searchQueryModel,
			Map<String, Object> model)
		throws SQLException {

		_searchQueryDAOImpl.addSearchQuery(searchQueryModel.getSearchQuery());

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		model.put("searchQueryModels", searchQueryModels);

		return "redirect:view_search_queries";
	}

	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryController.class);
}