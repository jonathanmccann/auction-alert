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

import com.app.model.Category;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.CategoryUtil;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesKeys;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SearchQueryControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpDatabase();
		setUpProperties();
		setUpUserUtil();
	}

	@After
	public void tearDown() throws Exception {
		CategoryUtil.deleteCategories();

		SearchQueryUtil.deleteSearchQueries(_USER_ID);

		SearchResultUtil.deleteSearchResult(1);

		SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(1);
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

		String[] searchQueryIds = new String[] { String.valueOf(searchQueryId) };

		SearchResult searchResult = new SearchResult(
			searchQueryId, "1234", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			new Date(), "Buy It Now");

		SearchResultUtil.addSearchResult(searchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchQueryId, "100");

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryIds", searchQueryIds))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		try {
			SearchQueryUtil.getSearchQuery(searchQueryId);
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(searchQueryId);

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				searchQueryId);

		Assert.assertEquals(0, searchResults.size());
		Assert.assertEquals(0, searchQueryPreviousResults.size());
	}

	@Test
	public void testDeleteSearchQueryWithNullSearchQueryIds() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

		SearchResult searchResult = new SearchResult(
			searchQueryId, "1234", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			new Date(), "Buy It Now");

		SearchResultUtil.addSearchResult(searchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchQueryId, "100");

		this.mockMvc.perform(post("/delete_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(searchQueryId);

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				searchQueryId);

		Assert.assertEquals(1, searchQueries.size());
		Assert.assertEquals(1, searchResults.size());
		Assert.assertEquals(1, searchQueryPreviousResults.size());
	}

	@Test
	public void testGetAddSearchQuery() throws Exception {
		List<Category> categories = new ArrayList<>();

		Category category = new Category("100", "Category Name");

		categories.add(category);

		category = new Category("200", "Category Name2");

		categories.add(category);

		CategoryUtil.addCategories(categories);

		this.mockMvc.perform(get("/add_search_query"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(0))))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeExists("isAdd"));
	}

	@Test
	public void testGetAddSearchQueryExceedingTotalNumberOfQueriesAllows()
		throws Exception {

		SearchQueryUtil.addSearchQuery(_USER_ID, "First test keywords");
		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		this.mockMvc.perform(get("/add_search_query"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(0))))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attribute("disabled", true))
			.andExpect(model().attributeExists("isAdd"));
	}

	@Test
	public void testGetUpdateSearchQuery() throws Exception {
		List<Category> categories = new ArrayList<>();

		Category category = new Category("100", "Category Name");

		categories.add(category);

		category = new Category("200", "Category Name2");

		categories.add(category);

		CategoryUtil.addCategories(categories);

		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));

		this.mockMvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(searchQueryId))))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testHandleError() throws Exception {
		DatabaseUtil.setDatabaseProperties("test", "test", "test");

		this.mockMvc.perform(get("/add_search_query"))
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
	public void testMuteSearchQuery() throws Exception {
		int searchQueryId = SearchQueryUtil.addSearchQuery(
			_USER_ID, "First test keywords");

		String[] searchQueryIds = new String[] { String.valueOf(searchQueryId) };

		this.mockMvc.perform(post("/mute_search_query")
			.param("searchQueryIds", searchQueryIds))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertTrue(searchQuery.isMuted());
	}

	@Test
	public void testPostAddSearchQueryExceedingTotalNumberOfQueriesAllows()
		throws Exception {

		SearchQueryUtil.addSearchQuery(_USER_ID, "First test keywords");
		SearchQueryUtil.addSearchQuery(_USER_ID, "Second test keywords");

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"))
			.andExpect(model().attributeDoesNotExist("searchQueries"))
			.andExpect(model().attributeDoesNotExist("searchQueryCategories"))
			.andExpect(model().attribute("disabled", true))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testPostAddSearchQueryWithDefaultCategoryId() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("keywords", "First test keywords");
		request.param("categoryId", "All Categories");

		this.mockMvc.perform(request)
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(model().attributeExists("searchQueries"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("isAdd"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
	}

	@Test
	public void testPostAddSearchQueryWithNullSearchQuery() throws Exception {
		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:error.jsp"))
			.andExpect(model().attributeDoesNotExist("searchQueries"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testPostAddSearchQueryWithSearchQuery() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("keywords", "First test keywords");

		this.mockMvc.perform(request)
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(model().attributeExists("searchQueries"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("isAdd"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
	}

	@Test
	public void testPostAddSearchQueryWithParameters() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeExists("searchQueries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
	}

	@Test
	public void testPostAddSearchQueryWithSearchQueryAndCategory()
		throws Exception {

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeExists("searchQueries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
	}

	@Test
	public void testPostUpdateSearchQueryWithParameters() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", false, false, false, false,
			false, false, false, 0.00, 0.00, false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
		Assert.assertFalse(searchQuery.isMuted());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "101");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("muted", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeExists("searchQueries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("101", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertTrue(searchQuery.isMuted());
	}

	@Test
	public void testPostUpdateWithDefaultCategoryId() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", false, false, false, false,
			false, false, false, 0.00, 0.00, false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "All Categories");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("muted", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeExists("searchQueries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertTrue(searchQuery.isMuted());
	}

	@Test
	public void testPostUpdateWithNullCategoryId() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", false, false, false, false,
			false, false, false, 0.00, 0.00, false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("muted", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeExists("searchQueries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertTrue(searchQuery.isMuted());
	}

	@Test
	public void testPostUpdateWithNullKeywords() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("keywords", "");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:error.jsp"));
		resultActions.andExpect(model().attributeDoesNotExist("searchQueries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testViewSearchQueries() throws Exception {
		this.mockMvc.perform(get("/view_search_queries"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_queries"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_queries.jsp"))
			.andExpect(model().attributeExists("searchQueries"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}