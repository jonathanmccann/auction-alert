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

package com.app.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesKeys;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SearchResultControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpDatabase();
		setUpUserUtil();
	}

	@After
	public void tearDown() throws Exception {
		SearchQueryUtil.deleteSearchQueries(_USER_ID);

		SearchResultUtil.deleteSearchResult(1);
	}

	@Test
	public void testHandleError() throws Exception {
		DatabaseUtil.setDatabaseProperties("test", "test", "test");

		this.mockMvc.perform(get("/view_search_query_results"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:error.jsp"));

		String databasePassword = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_PASSWORD);
		String databaseURL = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_URL);
		String databaseUsername = System.getProperty(
			PropertiesKeys.JDBC_DEFAULT_USERNAME);

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);
	}

	@Test
	public void testViewSearchResults() throws Exception {
		this.mockMvc.perform(get("/view_search_query_results"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("searchResultMap"))
			.andExpect(view().name("view_search_query_results"))
			.andExpect(
				forwardedUrl("/WEB-INF/jsp/view_search_query_results.jsp"));
	}

	@Test
	public void testViewSearchResultsWithSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");
		searchQuery.setActive(true);

		SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(get("/view_search_query_results"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("searchResultMap"))
			.andExpect(view().name("view_search_query_results"))
			.andExpect(
				forwardedUrl("/WEB-INF/jsp/view_search_query_results.jsp"));
	}

	@Test
	public void testViewSearchResultsWithSearchQueryAndSearchResult()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResult searchResult = new SearchResult(
			searchQueryId, "1234", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		SearchResultUtil.addSearchResult(searchResult);

		this.mockMvc.perform(get("/view_search_query_results"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("searchResultMap"))
			.andExpect(view().name("view_search_query_results"))
			.andExpect(
				forwardedUrl("/WEB-INF/jsp/view_search_query_results.jsp"));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	protected static final int _USER_ID = 1;

}