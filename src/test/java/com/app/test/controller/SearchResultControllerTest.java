package com.app.test.controller;

import com.app.exception.DatabaseConnectionException;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Connection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Jonathan McCann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/test-dispatcher-servlet.xml")
public class SearchResultControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() throws DatabaseConnectionException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		String databasePassword = System.getProperty(
			PropertiesUtil.DATABASE_PASSWORD);
		String databaseURL = System.getProperty(PropertiesUtil.DATABASE_URL);
		String databaseUsername = System.getProperty(
			PropertiesUtil.DATABASE_USERNAME);

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);

		DatabaseUtil.initializeDatabase();

		Connection connection = DatabaseUtil.getDatabaseConnection();

		Resource resource = new ClassPathResource("/sql/testdb.sql");

		ScriptUtils.executeSqlScript(connection, resource);
    }

	@Test
    public void testViewSearchResults() throws Exception {
		this.mockMvc.perform(get("/result"))
			.andExpect(status().isOk())
			.andExpect(view().name("view_search_results"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/view_search_results.jsp"));
	}

}