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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.model.Category;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.CategoryUtil;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
	}

	@Test
	public void testActivateSearchQuery() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(post("/activate_search_query")
			.param("searchQueryId", String.valueOf(searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(flash().attribute("currentSearchQueryId", searchQueryId))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", true));

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testActivateSearchQueryWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(post("/activate_search_query")
			.param("searchQueryId", String.valueOf(searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testDeactivateSearchQuery() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(post("/deactivate_search_query")
			.param("searchQueryId", String.valueOf(searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(flash().attribute("currentSearchQueryId", searchQueryId))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", false));

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testDeactivateSearchQueryWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(post("/deactivate_search_query")
			.param("searchQueryId", String.valueOf(searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		setUpUserUtil();

		SearchQuery activeSearchQuery = new SearchQuery();

		activeSearchQuery.setUserId(_USER_ID);
		activeSearchQuery.setKeywords("First test keywords");
		activeSearchQuery.setActive(true);

		int activeSearchQueryId = SearchQueryUtil.addSearchQuery(
			activeSearchQuery);

		SearchResult firstSearchResult = new SearchResult(
			activeSearchQueryId, "1234", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		SearchResultUtil.addSearchResult(firstSearchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			activeSearchQuery.getSearchQueryId(), "100");

		SearchQuery inactiveSearchQuery = new SearchQuery();

		inactiveSearchQuery.setUserId(_USER_ID);
		inactiveSearchQuery.setKeywords("First test keywords");
		inactiveSearchQuery.setActive(true);

		int inactiveSearchQueryId = SearchQueryUtil.addSearchQuery(
			inactiveSearchQuery);

		SearchResult secondSearchResult = new SearchResult(
			inactiveSearchQueryId, "2345", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/2345", "http://www.ebay.com/234.jpg");

		SearchResultUtil.addSearchResult(secondSearchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			inactiveSearchQuery.getSearchQueryId(), "200");

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryId", String.valueOf(activeSearchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		try {
			SearchQueryUtil.getSearchQuery(
				activeSearchQuery.getSearchQueryId());
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryId", String.valueOf(inactiveSearchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		try {
			SearchQueryUtil.getSearchQuery(
				inactiveSearchQuery.getSearchQueryId());
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(activeSearchQueryId);

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				activeSearchQueryId);

		Assert.assertEquals(0, searchResults.size());
		Assert.assertEquals(0, searchQueryPreviousResults.size());

		searchResults = SearchResultUtil.getSearchQueryResults(
			inactiveSearchQueryId);

		searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				inactiveSearchQueryId);

		Assert.assertEquals(0, searchResults.size());
		Assert.assertEquals(0, searchQueryPreviousResults.size());
	}

	@Test
	public void testDeleteSearchQueryWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResult searchResult = new SearchResult(
			searchQueryId, "1234", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		SearchResultUtil.addSearchResult(searchResult);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchQueryId, "100");

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryId", String.valueOf(searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

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
		setUpUserUtil();

		List<Category> categories = new ArrayList<>();

		Category category = new Category("100", "Category Name", "100", 1);

		categories.add(category);

		category = new Category("200", "Category Name2", "100", 2);

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
			.andExpect(model().attributeDoesNotExist("info"))
			.andExpect(model().attributeExists("globalIds"))
			.andExpect(model().attributeExists("isAdd"));
	}

	@Test
	public void testGetAddSearchQueryExceedingTotalNumberOfQueriesAllows()
		throws Exception {

		setUpUserUtil();

		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");

		SearchQueryUtil.addSearchQuery(firstSearchQuery);
		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		this.mockMvc.perform(get("/add_search_query"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(0))))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attribute("disabled", true))
			.andExpect(model().attributeExists("info"))
			.andExpect(model().attributeExists("globalIds"))
			.andExpect(model().attributeExists("isAdd"));
	}

	@Test
	public void testGetMonitor() throws Exception {
		this.mockMvc.perform(get("/monitor"))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor.jsp"))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeExists("searchQuery"))
			.andExpect(model().attributeExists("preferredDomain"))
			.andExpect(model().attributeExists("rssGlobalIds"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(0))));
	}

	@Test
	public void testGetUpdateSearchQuery() throws Exception {
		setUpUserUtil();

		List<Category> categories = new ArrayList<>();

		Category category = new Category("100", "Category Name", "100", 1);

		categories.add(category);

		category = new Category("200", "Category Name2", "100", 2);

		categories.add(category);

		CategoryUtil.addCategories(categories);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

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
			.andExpect(model().attributeExists("globalIds"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("info"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testGetUpdateSearchQueryWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		List<Category> categories = new ArrayList<>();

		Category category = new Category("100", "Category Name", "100", 1);

		categories.add(category);

		category = new Category("200", "Category Name2", "100", 2);

		categories.add(category);

		CategoryUtil.addCategories(categories);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));

		this.mockMvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"));
	}

	@Test
	public void testPostAddSearchQueryExceedingTotalNumberOfQueriesAllows()
		throws Exception {

		setUpUserUtil();

		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");

		SearchQueryUtil.addSearchQuery(firstSearchQuery);
		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"))
			.andExpect(model().attributeDoesNotExist("searchQueryCategories"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testPostAddSearchQueryWithDefaultCategoryId() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER_ID));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "All Categories");
		request.param("active", "true");

		this.mockMvc.perform(request)
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("info"))
			.andExpect(model().attributeDoesNotExist("isAdd"))
			.andExpect(flash().attributeExists("currentSearchQueryId"))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
	}

	@Test
	public void testPostAddSearchQueryWithDefaultSubcategoryId()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER_ID));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("subcategoryId", "All Subcategories");
		request.param("active", "true");

		this.mockMvc.perform(request)
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("info"))
			.andExpect(model().attributeDoesNotExist("isAdd"))
			.andExpect(flash().attributeExists("currentSearchQueryId"))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
	}

	@Test
	public void testPostAddSearchQueryWithNullSearchQuery() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostAddSearchQueryWithParameters() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER_ID));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("subcategoryId", "200");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(flash().attributeExists("currentSearchQueryId"));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
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
	public void testPostAddSearchQueryWithSearchQuery() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER_ID));
		request.param("keywords", "First test keywords");
		request.param("active", "true");

		this.mockMvc.perform(request)
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("info"))
			.andExpect(model().attributeDoesNotExist("isAdd"))
			.andExpect(flash().attributeExists("currentSearchQueryId"))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
	}

	@Test
	public void testPostAddSearchQueryWithSearchQueryAndCategory()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER_ID));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("subcategoryId", "200");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(flash().attributeExists("currentSearchQueryId"));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
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
	public void testPostUpdateSearchQueryWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals("EBAY-US", searchQuery.getGlobalId());
		Assert.assertFalse(searchQuery.isActive());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "101");
		request.param("subcategoryId", "201");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("globalId", "EBAY-CA");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals("EBAY-US", searchQuery.getGlobalId());
		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateSearchQueryWithParameters() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, false);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals("EBAY-US", searchQuery.getGlobalId());
		Assert.assertFalse(searchQuery.isActive());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "101");
		request.param("subcategoryId", "201");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("globalId", "EBAY-CA");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(
			flash().attribute("currentSearchQueryId", searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("101", searchQuery.getCategoryId());
		Assert.assertEquals("201", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals("EBAY-CA", searchQuery.getGlobalId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateWithDefaultCategoryId() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "All Categories");
		request.param("subcategoryId", "200");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("globalId", "EBAY-CA");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(
			flash().attribute("currentSearchQueryId", searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals("EBAY-CA", searchQuery.getGlobalId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateWithDefaultSubcategoryId() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("subcategoryId", "All Subcategories");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("globalId", "EBAY-CA");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(
			flash().attribute("currentSearchQueryId", searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals("EBAY-CA", searchQuery.getGlobalId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateWithNullCategoryId() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "");
		request.param("subcategoryId", "200");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("globalId", "EBAY-CA");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(
			flash().attribute("currentSearchQueryId", searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals("EBAY-CA", searchQuery.getGlobalId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateWithNullSubcategoryId() throws Exception {
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, false,
			false, false, false, false, 0.00, 0.00, "EBAY-US", false);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(searchQueryId));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("subcategoryId", "");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("globalId", "EBAY-CA");
		request.param("active", "true");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:view_search_queries"));
		resultActions.andExpect(model().attributeDoesNotExist("disabled"));
		resultActions.andExpect(model().attributeDoesNotExist("info"));
		resultActions.andExpect(model().attributeDoesNotExist("isAdd"));
		resultActions.andExpect(
			flash().attribute("currentSearchQueryId", searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());

		searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertEquals("EBAY-CA", searchQuery.getGlobalId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateWithNullKeywords() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("keywords", "");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isFound());
		resultActions.andExpect(view().name("redirect:update_search_query"));
		resultActions.andExpect(model().attributeExists("searchQueryId"));
		resultActions.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testViewSearchQueries() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/view_search_queries")
				.param("currentSearchQueryId", "100")
				.param("isCurrentSearchQueryActive", "true"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_queries"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_queries.jsp"))
			.andExpect(model().attributeExists("activeSearchQueries"))
			.andExpect(model().attributeExists("inactiveSearchQueries"))
			.andExpect(model().attributeDoesNotExist("isAdd"))
			.andExpect(model().attribute("currentSearchQueryId", "100"))
			.andExpect(model().attribute("isCurrentSearchQueryActive", "true"));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}