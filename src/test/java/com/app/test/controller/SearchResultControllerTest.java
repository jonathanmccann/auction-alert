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

import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;

import com.app.util.ConstantsUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;
import com.app.util.UserUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchResultControllerTest extends BaseTestCase {

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

		SearchQueryUtil.deleteSearchQueries(_USER.getUserId());

		SearchResultUtil.deleteSearchQueryResults(_searchQueryId);
	}

	@Test
	public void testGetSearchQueryResults() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		addSearchQueryAndResults();

		MvcResult mvcResult = this.mockMvc.perform(get("/search_query_results")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andReturn();

		Gson gson = new Gson();

		Type listType = new TypeToken<ArrayList<JsonSearchResult>>(){}.getType();

		List<JsonSearchResult> jsonSearchResults = gson.fromJson(
			mvcResult.getResponse().getContentAsString(), listType);

		JsonSearchResult jsonSearchResult = jsonSearchResults.get(0);

		Assert.assertEquals("$20.00", jsonSearchResult.getAuctionPrice());
		Assert.assertEquals("$24.99", jsonSearchResult.getFixedPrice());
		Assert.assertEquals("http://www.ebay.com/567.jpg", jsonSearchResult.getGalleryURL());
		Assert.assertEquals("Item ID 2", jsonSearchResult.getItemId());
		Assert.assertEquals("Second Item", jsonSearchResult.getItemTitle());
		Assert.assertEquals("http://www.ebay.com/itm/5678", jsonSearchResult.getItemURL());
		Assert.assertEquals(1, jsonSearchResult.getSearchQueryId());
		Assert.assertEquals(2, jsonSearchResult.getSearchResultId());

		jsonSearchResult = jsonSearchResults.get(1);

		Assert.assertEquals("$10.00", jsonSearchResult.getAuctionPrice());
		Assert.assertEquals("$14.99", jsonSearchResult.getFixedPrice());
		Assert.assertEquals("http://www.ebay.com/123.jpg", jsonSearchResult.getGalleryURL());
		Assert.assertEquals("Item ID 1", jsonSearchResult.getItemId());
		Assert.assertEquals("First Item", jsonSearchResult.getItemTitle());
		Assert.assertEquals("http://www.ebay.com/itm/1234", jsonSearchResult.getItemURL());
		Assert.assertEquals(1, jsonSearchResult.getSearchQueryId());
		Assert.assertEquals(1, jsonSearchResult.getSearchResultId());
	}

	@Test
	public void testGetSearchQueryResultsWithInvalidUserId()
		throws Exception {

		setUpSecurityUtilsSession(true, _INVALID_USER_ID);

		addSearchQueryAndResults();

		MvcResult mvcResult = this.mockMvc.perform(get("/search_query_results")
			.param("searchQueryId", String.valueOf(_searchQueryId)))
			.andReturn();

		Assert.assertEquals("[]", mvcResult.getResponse().getContentAsString());
	}

	private static void addSearchQueryAndResults() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER.getUserId());
		searchQuery.setKeywords("Test Keywords");

		_searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResult firstSearchResult = new SearchResult(
			_SEARCH_QUERY_ID, "Item ID 1", "First Item", "$10.00", "$14.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		SearchResult secondSearchResult = new SearchResult(
			_SEARCH_QUERY_ID, "Item ID 2", "Second Item", "$20.00", "$24.99",
			"http://www.ebay.com/itm/5678", "http://www.ebay.com/567.jpg");

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(firstSearchResult);
		searchResults.add(secondSearchResult);

		SearchResultUtil.addSearchResults(_searchQueryId, searchResults);
	}

	private static class JsonSearchResult {
		public String getAuctionPrice() {
			return auctionPrice;
		}

		public String getFixedPrice() {
			return fixedPrice;
		}

		public String getGalleryURL() {
			return galleryURL;
		}

		public String getItemId() {
			return itemId;
		}

		public String getItemTitle() {
			return itemTitle;
		}

		public String getItemURL() {
			return itemURL;
		}

		public int getSearchQueryId() {
			return searchQueryId;
		}

		public int getSearchResultId() {
			return searchResultId;
		}

		private String auctionPrice;
		private String fixedPrice;
		private String galleryURL;
		private String itemId;
		private String itemTitle;
		private String itemURL;
		private int searchQueryId;
		private int searchResultId;
	}

	private MockMvc mockMvc;

	private static int _searchQueryId;

	private static User _USER;

	@Autowired
	private WebApplicationContext wac;

	private static final int _SEARCH_QUERY_ID = 1;

}