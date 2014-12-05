package com.app.test.dao;

import com.app.dao.impl.SearchResultDAOImpl;
import com.app.model.SearchResultModel;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * @author Jonathan McCann
 */
public class SearchResultDAOTest {

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

		Resource resource = new ClassPathResource(
			"/sql/testdb.sql");

		ScriptUtils.executeSqlScript(connection, resource);
	}

	@Test
	public void testSearchResultDAO() throws Exception {
		// Test add
		SearchResultDAOImpl searchResultDAOImpl = new SearchResultDAOImpl();

		Date endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			"1234", "First Item", 14.99, 14.99, "http://www.ebay.com/itm/1234",
			endingTime, "Auction");

		searchResultDAOImpl.addSearchResult(searchResultModel);
		searchResultDAOImpl.addSearchResult(
			"5678", "Second Item", 29.99, 29.99, "http://www.ebay.com/itm/5678",
			endingTime, "Buy It Now");

		// Test get
		SearchResultModel searchResult = searchResultDAOImpl.getSearchResult(1);

		Assert.assertEquals(1, searchResult.getSearchResultId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals(14.99, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Auction", searchResult.getTypeOfAuction());

		// Test get multiple
		List<SearchResultModel> searchResultModels =
			searchResultDAOImpl.getSearchResults();

		Assert.assertEquals(2, searchResultModels.size());

		SearchResultModel secondSearchResult = searchResultModels.get(1);

		Assert.assertEquals(2, secondSearchResult.getSearchResultId());
		Assert.assertEquals("5678", secondSearchResult.getItemId());
		Assert.assertEquals("Second Item", secondSearchResult.getItemTitle());
		Assert.assertEquals(29.99, secondSearchResult.getAuctionPrice(), 0);
		Assert.assertEquals(29.99, secondSearchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/5678", secondSearchResult.getItemURL());
		Assert.assertEquals(endingTime, secondSearchResult.getEndingTime());
		Assert.assertEquals("Buy It Now", secondSearchResult.getTypeOfAuction());

		// Test delete multiple
		searchResultDAOImpl.deleteSearchResult(1);
		searchResultDAOImpl.deleteSearchResult(2);

		searchResultModels = searchResultDAOImpl.getSearchResults();

		Assert.assertEquals(0, searchResultModels.size());
	}

}