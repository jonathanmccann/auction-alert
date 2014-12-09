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
		
		_searchQueryDAOImpl = new SearchQueryDAOImpl();
	}

	@Test
	public void testSearchQueryDAO() throws Exception {

		// Test add

		_searchQueryDAOImpl.addSearchQuery("First test search query");
		_searchQueryDAOImpl.addSearchQuery("Second test search query");

		// Test get

		String searchQuery = _searchQueryDAOImpl.getSearchQuery(1);

		Assert.assertEquals("First test search query", searchQuery);

		// Test get multiple

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		SearchQueryModel firstSearchQuery = searchQueryModels.get(0);
		SearchQueryModel secondSearchQuery = searchQueryModels.get(1);

		Assert.assertEquals(2, searchQueryModels.size());
		Assert.assertEquals(1, firstSearchQuery.getSearchQueryId());
		Assert.assertEquals(2, secondSearchQuery.getSearchQueryId());
		Assert.assertEquals(
			"First test search query", firstSearchQuery.getSearchQuery());
		Assert.assertEquals(
			"Second test search query", secondSearchQuery.getSearchQuery());

		// Test update

		_searchQueryDAOImpl.updateSearchQuery(1, "Updated test search query");

		String updatedSearchQuery = _searchQueryDAOImpl.getSearchQuery(1);

		Assert.assertEquals("Updated test search query", updatedSearchQuery);

		// Test delete multiple

		_searchQueryDAOImpl.deleteSearchQuery(1);
		_searchQueryDAOImpl.deleteSearchQuery(2);

		searchQueryModels = _searchQueryDAOImpl.getSearchQueries();

		Assert.assertEquals(0, searchQueryModels.size());
	}

	private static SearchQueryDAOImpl _searchQueryDAOImpl;
}