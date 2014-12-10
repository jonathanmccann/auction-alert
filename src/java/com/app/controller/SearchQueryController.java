package com.app.controller;

import java.util.Map;

import com.app.model.SearchQueryModel;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchQueryController {

	@RequestMapping(
		value = {"/query", "/search_query", "/view_search_query"},
		method = RequestMethod.GET)
	public String viewSearchQueries() {
		return "view_search_queries";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.GET)
	public String viewAddSearchQuery(Map<String, Object> model) {
		SearchQueryModel searchQueryModel = new SearchQueryModel();

		model.put("searchQueryModel", searchQueryModel);

		return "add_search_query";
	}

	@RequestMapping(value = "/add_search_query", method = RequestMethod.POST)
	public String processRegistration(
		@ModelAttribute("searchQueryModel") SearchQueryModel searchQueryModel,
		Map<String, Object> model) {

		System.out.println("Search Query: " + searchQueryModel.getSearchQuery());

		return "view_search_queries";
	}
}