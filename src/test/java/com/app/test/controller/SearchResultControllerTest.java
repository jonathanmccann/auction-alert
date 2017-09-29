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

import com.app.model.SearchResult;
import com.app.test.BaseTestCase;

import com.app.util.SearchResultUtil;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SearchResultControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		setUpDatabase();

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetSearchQueryResults() throws Exception {
		SearchResult searchResult = new SearchResult(
			_SEARCH_QUERY_ID, "Item ID 1", "First Item", "$10.00", "$14.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		SearchResultUtil.addSearchResult(searchResult);

		searchResult = new SearchResult(
			_SEARCH_QUERY_ID, "Item ID 2", "Second Item", "$20.00", "$24.99",
			"http://www.ebay.com/itm/5678", "http://www.ebay.com/567.jpg");

		SearchResultUtil.addSearchResult(searchResult);

		MvcResult mvcResult = this.mockMvc.perform(get("/search_query_results")
			.param("searchQueryId", String.valueOf(_SEARCH_QUERY_ID)))
			.andReturn();

		Assert.assertEquals(
			_SEARCH_RESULTS_JSON, mvcResult.getResponse().getContentAsString());
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private static final int _SEARCH_QUERY_ID = 1;

	private static final String _SEARCH_RESULTS_JSON =
		"[{\"searchQueryId\":1,\"itemId\":\"Item ID 2\",\"searchResultId\":2,\"itemTitle\":\"Second Item\",\"itemURL\":\"http://www.ebay.com/itm/5678\",\"galleryURL\":\"http://www.ebay.com/567.jpg\",\"auctionPrice\":\"$20.00\",\"fixedPrice\":\"$24.99\"},{\"searchQueryId\":1,\"itemId\":\"Item ID 1\",\"searchResultId\":1,\"itemTitle\":\"First Item\",\"itemURL\":\"http://www.ebay.com/itm/1234\",\"galleryURL\":\"http://www.ebay.com/123.jpg\",\"auctionPrice\":\"$10.00\",\"fixedPrice\":\"$14.99\"}]";

}