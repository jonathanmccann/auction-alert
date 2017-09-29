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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.test.BaseTestCase;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ErrorControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetAccountFaqAsActive() throws Exception {
		setUpSecurityUtils(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/error"))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/error.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetAccountFaqAsInactive() throws Exception {
		setUpSecurityUtils(true);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/error"))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/error.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testHeadAccountFaqAsActive() throws Exception {
		setUpSecurityUtils(true);
		setUpUserUtil();

		this.mockMvc.perform(head("/error"))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/error.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testHeadAccountFaqAsInactive() throws Exception {
		setUpSecurityUtils(true);
		setUpUserUtil(false);

		this.mockMvc.perform(head("/error"))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/error.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}