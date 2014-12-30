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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.exception.DatabaseConnectionException;
import com.app.test.BaseDatabaseTestCase;
import com.app.util.PropertiesUtil;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
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
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SearchQueryControllerTest extends BaseDatabaseTestCase {

	@Override
	public void doSetUp() throws DatabaseConnectionException, IOException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		String[] searchQueryIds = new String[] { "1" };

		this.mockMvc.perform(post("/delete_search_query")
			.sessionAttr("searchQueryIds", searchQueryIds))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));
	}

	@Test
	public void testGetAddSearchQuery() throws Exception {
		this.mockMvc.perform(get("/add_search_query"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_search_query"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/add_search_query.jsp"));
	}

	@Test
	public void testPostAddSearchQuery() throws Exception {
		this.mockMvc.perform(post("/add_search_query"))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:view_search_queries"));
	}

	@Test
	public void testViewSearchQueries() throws Exception {
		this.mockMvc.perform(get("/query"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_queries"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_queries.jsp"));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}