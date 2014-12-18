package com.app.test.controller;

import com.app.exception.DatabaseConnectionException;
import com.app.test.BaseDatabaseTestCase;
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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Jonathan McCann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/test-dispatcher-servlet.xml")
public class SearchResultControllerTest extends BaseDatabaseTestCase {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void doSetUp() throws DatabaseConnectionException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

	@Test
    public void testViewSearchResults() throws Exception {
		this.mockMvc.perform(get("/result"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_query_results"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_query_results.jsp"));
	}

}