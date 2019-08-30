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

import com.app.model.User;
import com.app.test.BaseTestCase;

import com.app.util.ConstantsUtil;
import com.app.util.UserUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


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
@ContextConfiguration("/test-authentication-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AuthenticationInterceptorTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		setUpProperties();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		_USER = UserUtil.addUser("test@test.com", "password");
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByUserId(_USER.getUserId());
	}

	@Test
	public void testGetAddSearchQueryWithAuthenticatedUser() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/add_search_query"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"));
	}

	@Test
	public void testGetAddSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/add_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testGetDeleteAccountWithAuthenticatedUser() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/delete_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("delete_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/delete_account.jsp"));
	}

	@Test
	public void testGetDeleteAccountWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/delete_account"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testGetMonitorWithAuthenticatedUser() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/monitor"))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor.jsp"));
	}

	@Test
	public void testGetMonitorWithUnauthenticatedUser() throws Exception {
		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/monitor"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testGetMyAccountWithAuthenticatedUser() throws Exception {
		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/my_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
	}

	@Test
	public void testGetMyAccountWithUnauthenticatedUser() throws Exception {
		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/my_account"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testGetSearchQueryResultsWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/search_query_results"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testGetSearchQueryResultsWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/search_query_results"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testGetUpdateSearchQueryWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/update_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testGetUpdateSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/update_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testGetViewSearchQueriesWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(get("/view_search_queries"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_queries"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_queries.jsp"));
	}

	@Test
	public void testGetViewSearchQueriesWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(get("/view_search_queries"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostActivateSearchQueryWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/activate_search_query")
			.param("searchQueryId", "1"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));
	}

	@Test
	public void testPostActivateSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = mockMvc.perform(post("/activate_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostAddSearchQueryWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:add_search_query"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostAddSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/add_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeactivateSearchQueryWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/deactivate_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testPostDeactivateSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/deactivate_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeleteAccountWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/delete_account"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:delete_account"))
			.andExpect(redirectedUrl("delete_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostDeleteAccountWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/delete_account"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeleteSearchQueryWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/delete_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testPostDeleteSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/delete_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostDeleteSubscriptionWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/delete_subscription"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostDeleteSubscriptionWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/delete_subscription"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostMyAccountWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/my_account"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostMyAccountWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/my_account"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostResubscribeWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostResubscribeWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/resubscribe"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostUpdateSearchQueryWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/update_search_query"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:error"))
			.andExpect(redirectedUrl("error"));
	}

	@Test
	public void testPostUpdateSearchQueryWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/update_search_query"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	@Test
	public void testPostUpdateSubscriptionWithAuthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSession(true, _USER.getUserId());

		this.mockMvc.perform(post("/update_subscription"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testPostUpdateSubscriptionWithUnauthenticatedUser()
		throws Exception {

		setUpSecurityUtilsSubject(false);

		MvcResult result = this.mockMvc.perform(post("/update_subscription"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("log_in", response.getRedirectedUrl());
	}

	private MockMvc mockMvc;

	private static User _USER;

	@Autowired
	private WebApplicationContext wac;

}