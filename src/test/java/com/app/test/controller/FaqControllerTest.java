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
public class FaqControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetAccountFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/account_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("account_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/account_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetAccountFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/account_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("account_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/account_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetGeneralFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/general_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("general_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/general_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetGeneralFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/general_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("general_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/general_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetMonitorFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/monitor_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetMonitorFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/monitor_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("monitor_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/monitor_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetNewFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/new_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("new_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/new_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetNewFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/new_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("new_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/new_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetQueryFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/query_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("query_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/query_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetQueryFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/query_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("query_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/query_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetResultFaqAsActiveUser() throws Exception {
		setUpSecurityUtilsSubject(true);
		setUpUserUtil();

		this.mockMvc.perform(get("/result_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("result_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/result_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetResultFaqAsInactiveUser() throws Exception {
		setUpSecurityUtilsSubject(false);
		setUpUserUtil(false);

		this.mockMvc.perform(get("/result_faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("result_faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/result_faq.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

}