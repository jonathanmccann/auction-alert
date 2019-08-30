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

import com.app.controller.SearchQueryController;
import com.app.model.Category;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.CategoryUtil;
import com.app.util.ConstantsUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.util.UserUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchQueryControllerTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();

		setUpDatabase();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		_USER = UserUtil.addUser("test@liferay.com", "password");
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByUserId(_USER.getUserId());

		CategoryUtil.deleteCategories();

		SearchQueryUtil.deleteSearchQueries(_USER.getUserId());

		SearchResultUtil.deleteSearchQueryResults(_searchQueryId);
	}

	@Test
	public void testActivateSearchQuery() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		this.mockMvc.perform(post("/activate_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(
				flash().attribute("currentSearchQueryId", _searchQueryId))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", true));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testActivateSearchQueryWithInvalidUserId() throws Exception {
		setUpSecurityUtilsSession(_INVALID_USER_ID);

		addSearchQuery(false);

		this.mockMvc.perform(post("/activate_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testDeactivateSearchQuery() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(true);

		this.mockMvc.perform(post("/deactivate_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"))
			.andExpect(
				flash().attribute("currentSearchQueryId", _searchQueryId))
			.andExpect(flash().attribute("isCurrentSearchQueryActive", false));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testDeactivateSearchQueryWithInvalidUserId() throws Exception {
		setUpSecurityUtilsSession(_INVALID_USER_ID);

		addSearchQuery(true);

		this.mockMvc.perform(post("/deactivate_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testDeleteActiveSearchQueryWithSearchResults()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(true);

		addSearchResults();

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		try {
			SearchQueryUtil.getSearchQuery(_searchQueryId);
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_searchQueryId);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteInactiveSearchQueryWithSearchResults()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		addSearchResults();

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		try {
			SearchQueryUtil.getSearchQuery(_searchQueryId);
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_searchQueryId);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteSearchQueryWithInvalidUserId() throws Exception {
		setUpSecurityUtilsSession(_INVALID_USER_ID);

		addSearchQuery(true);

		addSearchResults();

		this.mockMvc.perform(post("/delete_search_query")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER.getUserId(), true);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_searchQueryId);

		Assert.assertEquals(1, searchQueries.size());
		Assert.assertEquals(1, searchResults.size());
	}

	@Test
	public void testGetAddSearchQuery() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

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
	public void testGetAddSearchQueryExceedingTotalNumberOfQueriesAllowed()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER.getUserId());
		firstSearchQuery.setKeywords("First test keywords");

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/monitor"))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor.jsp"))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeExists("searchQuery"))
			.andExpect(model().attributeExists("preferredCurrency"))
			.andExpect(model().attributeExists("preferredDomain"))
			.andExpect(model().attributeExists("rssGlobalIds"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(0))));
	}

	@Test
	public void testGetMonitorWithSearchQueryId() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(true);

		this.mockMvc.perform(get("/monitor")
			.param(
				"searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor.jsp"))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeExists("searchQuery"))
			.andExpect(model().attributeExists("preferredCurrency"))
			.andExpect(model().attributeExists("preferredDomain"))
			.andExpect(model().attributeExists("rssGlobalIds"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty(
					"searchQueryId", is(_searchQueryId))));
	}

	@Test
	public void testGetMonitorWithInvalidSearchQueryId() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER.getUserId() + 1);
		searchQuery.setKeywords("First test keywords");

		_searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(get("/monitor")
			.param(
				"searchQueryId", String.valueOf(_searchQueryId)))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor.jsp"))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeExists("searchQuery"))
			.andExpect(model().attributeExists("preferredCurrency"))
			.andExpect(model().attributeExists("preferredDomain"))
			.andExpect(model().attributeExists("rssGlobalIds"))
			.andExpect(model().attribute(
				"searchQuery", hasProperty("searchQueryId", is(0))));
	}

	@Test
	public void testGetSubcategories() throws Exception {
		addCategories();

		MvcResult mvcResult = this.mockMvc.perform(get("/subcategories")
			.param("categoryParentId", "100")
			.accept("application/json"))
			.andReturn();

		Assert.assertEquals(
			"{\"Category Name 2\":\"200\"}",
			mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testGetSubcategoriesWithInvalidCategoryParentId()
		throws Exception {

		addCategories();

		MvcResult mvcResult = this.mockMvc.perform(get("/subcategories")
			.param("categoryParentId", "1")
			.accept("application/json"))
			.andReturn();

		Assert.assertEquals("{}", mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testGetUpdateSearchQuery() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(true);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(_searchQueryId));

		this.mockMvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"))
			.andExpect(model().attribute(
				"searchQuery",
				hasProperty("searchQueryId", is(_searchQueryId))))
			.andExpect(model().attributeExists("searchQueryCategories"))
			.andExpect(model().attributeExists("globalIds"))
			.andExpect(model().attributeDoesNotExist("disabled"))
			.andExpect(model().attributeDoesNotExist("info"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testGetUpdateSearchQueryWithInvalidUserId() throws Exception {
		setUpSecurityUtilsSession(_INVALID_USER_ID);

		addSearchQuery(true);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(_searchQueryId));

		this.mockMvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"));
	}

	@Test
	public void testPopulateCategories() throws Exception {
		Class clazz = Class.forName(SearchQueryController.class.getName());

		Object classInstance = clazz.newInstance();

		Method populateCategoriesMethod = clazz.getDeclaredMethod(
			"_populateCategories",  Map.class);

		populateCategoriesMethod.setAccessible(true);

		Field field = clazz.getDeclaredField("_categories");

		field.setAccessible(true);

		Map<String, String> categories = (Map<String, String>)field.get(clazz);

		Assert.assertEquals(0, categories.size());

		List<Category> categoriesToAdd = new ArrayList<>();

		Category category = new Category(
			_CATEGORY_ID, _CATEGORY_NAME, _CATEGORY_PARENT_ID, _CATEGORY_LEVEL);

		categoriesToAdd.add(category);

		CategoryUtil.addCategories(categoriesToAdd);

		Map<String, Object> model = new HashMap<>();

		populateCategoriesMethod.invoke(classInstance, model);

		Map<String, String> searchQueryCategories =
			(HashMap)model.get("searchQueryCategories");

		Assert.assertEquals(
			_CATEGORY_NAME, searchQueryCategories.get(_CATEGORY_ID));

		categories = (Map<String, String>)field.get(clazz);

		Assert.assertEquals(1, categories.size());

		String categoryName = categories.get(_CATEGORY_ID);

		Assert.assertEquals(_CATEGORY_NAME, categoryName);
	}

	@Test
	public void testPostAddSearchQueryExceedingTotalNumberOfQueriesAllowed()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER.getUserId());
		searchQuery.setKeywords("Keywords");

		SearchQueryUtil.addSearchQuery(searchQuery);

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"))
			.andExpect(model().attributeDoesNotExist("searchQueryCategories"))
			.andExpect(model().attributeDoesNotExist("isAdd"));
	}

	@Test
	public void testPostAddSearchQueryWithDefaultCategoryId() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER.getUserId()));
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
			_USER.getUserId(), true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostAddSearchQueryWithDefaultSubcategoryId()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER.getUserId()));
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
			_USER.getUserId(), true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostAddSearchQueryWithNullSearchQuery() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostAddSearchQueryWithParameters() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER.getUserId()));
		request.param("keywords", "First test keywords");
		request.param("categoryId", "100");
		request.param("subcategoryId", "200");
		request.param("searchDescription", "true");
		request.param("freeShippingOnly", "true");
		request.param("newCondition", "true");
		request.param("auctionListing", "true");
		request.param("minPrice", "5.00");
		request.param("maxPrice", "10.00");
		request.param("active", "false");

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
			_USER.getUserId(), false);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testPostAddSearchQueryWithSearchQuery() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER.getUserId()));
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
			_USER.getUserId(), true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostAddSearchQueryWithSearchQueryAndCategory()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/add_search_query");

		request.param("userId", String.valueOf(_USER.getUserId()));
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
			_USER.getUserId(), true);

		Assert.assertEquals(1, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testPostUpdateSearchQueryWithInvalidUserId() throws Exception {
		setUpSecurityUtilsSession(_INVALID_USER_ID);

		addSearchQuery(false);

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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

		request.param(
			"searchQueryId", String.valueOf(_searchQueryId));
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

		searchQuery = SearchQueryUtil.getSearchQuery(_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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

		request.param("searchQueryId", String.valueOf(_searchQueryId));
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
			flash().attribute("currentSearchQueryId", _searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		searchQuery = SearchQueryUtil.getSearchQuery(_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(_searchQueryId));
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
			flash().attribute("currentSearchQueryId", _searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(_searchQueryId));
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
			flash().attribute("currentSearchQueryId", _searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(_searchQueryId));
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
			flash().attribute("currentSearchQueryId", _searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQuery(false);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/update_search_query");

		request.param("searchQueryId", String.valueOf(_searchQueryId));
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
			flash().attribute("currentSearchQueryId", _searchQueryId));
		resultActions.andExpect(
			flash().attribute("isCurrentSearchQueryActive", true));

		SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
			_searchQueryId);

		Assert.assertEquals(_USER.getUserId(), searchQuery.getUserId());
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
		setUpSecurityUtilsSession(true, _USER.getUserId());

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
		setUpSecurityUtilsSession(true, _USER.getUserId());

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

	private static void addCategories() throws Exception {
		List<Category> categories = new ArrayList<>();

		Category category = new Category("100", "Category Name", "100", 1);

		categories.add(category);

		category = new Category("200", "Category Name 2", "100", 2);

		categories.add(category);

		CategoryUtil.addCategories(categories);
	}

	private static void addSearchQuery(boolean isActive) throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER.getUserId(), "Test keywords", "100", "200", false, false,
			false, false, false, false, false, 0.00, 0.00, "EBAY-US", isActive);

		_searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);
	}

	private static void addSearchResults() throws Exception {
		SearchResult firstSearchResult = new SearchResult(
			_searchQueryId, "1234", "itemTitle", "$14.99", "$14.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(firstSearchResult);

		SearchResultUtil.addSearchResults(_searchQueryId, searchResults);
	}

	private MockMvc mockMvc;

	private static int _searchQueryId;
	private static User _USER;

	@Autowired
	private WebApplicationContext wac;

}