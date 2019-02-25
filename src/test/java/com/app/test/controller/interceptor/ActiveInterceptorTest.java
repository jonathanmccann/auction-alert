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

package com.app.test.controller.interceptor;

import com.app.test.BaseTestCase;

import com.app.util.ConstantsUtil;
import com.app.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-active-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ActiveInterceptorTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpUserUtil();
		setUpDatabase();

		UserUtil.addUser("test@test.com", "password");
	}

	@Test
	public void testGetAddSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(get("/add_search_query"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"));
	}

	@Test
	public void testGetAddSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/add_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testGetMonitorWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(get("/monitor"))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor.jsp"));
	}

	@Test
	public void testGetMonitorWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/monitor"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testGetSearchQueryResultsWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(get("/search_query_results"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testGetSearchQueryResultsWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/search_query_results"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testGetUpdateSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(get("/update_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testGetUpdateSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/update_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testGetViewSearchQueriesWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(get("/view_search_queries"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_queries"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_queries.jsp"));
	}

	@Test
	public void testGetViewSearchQueriesWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/view_search_queries"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostActivateSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/activate_search_query")
			.param("searchQueryId", "1"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));
	}

	@Test
	public void testPostActivateSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = mockMvc.perform(post("/activate_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostAddSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"));
	}

	@Test
	public void testPostAddSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/add_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeactivateSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/deactivate_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testPostDeactivateSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/deactivate_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeleteSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/delete_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testPostDeleteSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/delete_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeleteSubscriptionWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/delete_subscription"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostDeleteSubscriptionWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/delete_subscription"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostUpdateSearchQueryWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/update_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testPostUpdateSearchQueryWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/update_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	@Test
	public void testPostUpdateSubscriptionWithActiveUser() throws Exception {
		UserUtil.updateUserSubscription(
			_USER_ID, "customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/update_subscription"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostUpdateSubscriptionWithInactiveUser() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/update_subscription"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("my_account", response.getRedirectedUrl());
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}