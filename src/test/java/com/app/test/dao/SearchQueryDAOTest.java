package com.app.test.dao;

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.model.SearchQueryModel;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;

import java.sql.Connection;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * @author Jonathan McCann
 */
public class SearchQueryDAOTest {

	@Before
	public void setUp() throws Exception {
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
	public void testSearchQueryDAO() throws Exception {

		// Test add

		SearchQueryDAOImpl searchQueryDAOImpl = new SearchQueryDAOImpl();

		searchQueryDAOImpl.addSearchQuery("First test search query");
		searchQueryDAOImpl.addSearchQuery("Second test search query");

		// Test get

		String searchQuery = searchQueryDAOImpl.getSearchQuery(1);

		Assert.assertEquals("First test search query", searchQuery);

		// Test get multiple

		List<SearchQueryModel> searchQueryModels =
			searchQueryDAOImpl.getSearchQueries();

		Assert.assertEquals(2, searchQueryModels.size());
		Assert.assertEquals(1, searchQueryModels.get(0).getSearchQueryId());
		Assert.assertEquals(2, searchQueryModels.get(1).getSearchQueryId());
		Assert.assertEquals(
			"First test search query",
			searchQueryModels.get(0).getSearchQuery());
		Assert.assertEquals(
			"Second test search query",
			searchQueryModels.get(1).getSearchQuery());

		// Test update

		searchQueryDAOImpl.updateSearchQuery(1, "Updated test search query");

		String updatedSearchQuery = searchQueryDAOImpl.getSearchQuery(1);

		Assert.assertEquals("Updated test search query", updatedSearchQuery);

		// Test delete multiple

		searchQueryDAOImpl.deleteSearchQuery(1);
		searchQueryDAOImpl.deleteSearchQuery(2);

		searchQueryModels = searchQueryDAOImpl.getSearchQueries();

		Assert.assertEquals(0, searchQueryModels.size());
	}

}