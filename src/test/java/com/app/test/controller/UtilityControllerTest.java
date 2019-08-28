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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.junit.Before;
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
public class UtilityControllerTest {

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetFavicon() throws Exception {
		this.mockMvc.perform(get("/favicon.ico"))
			.andExpect(forwardedUrl("resources/images/favicon.ico"))
			.andExpect(view().name("forward:resources/images/favicon.ico"));
	}

	@Test
	public void testGetRobots() throws Exception {
		this.mockMvc.perform(get("/robots.txt"))
			.andExpect(forwardedUrl("resources/robots.txt"))
			.andExpect(view().name("forward:resources/robots.txt"));
	}

	@Test
	public void testGetSitemap() throws Exception {
		this.mockMvc.perform(get("/sitemap.xml"))
			.andExpect(forwardedUrl("resources/sitemap.xml"))
			.andExpect(view().name("forward:resources/sitemap.xml"));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}