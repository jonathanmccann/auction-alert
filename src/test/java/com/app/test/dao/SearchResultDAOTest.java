package com.app.test.dao;

import com.app.dao.impl.SearchResultDAOImpl;
import com.app.model.SearchResultModel;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;

import java.sql.Connection;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * @author Jonathan McCann
 */
public class SearchResultDAOTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
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

		_searchResultDAOImpl = new SearchResultDAOImpl();
	}

	@Test
	public void testSearchResultDAO() throws Exception {

		// Test add with constructor

		_endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			1, "1234", "First Item", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", _endingTime, "Auction");

		_searchResultDAOImpl.addSearchResult(searchResultModel);

		searchResultModel = new SearchResultModel(
			1, "2345", "Second Item", 14.99, 14.99,
			"http://www.ebay.com/itm/2345", _endingTime, "Auction");

		_searchResultDAOImpl.addSearchResult(searchResultModel);

		// Test add

		_searchResultDAOImpl.addSearchResult(
			2, "5678", "Third Item", 29.99, 29.99,
			"http://www.ebay.com/itm/5678", _endingTime, "Buy It Now");

		_searchResultDAOImpl.addSearchResult(
			2, "6789", "Fourth Item", 29.99, 29.99,
			"http://www.ebay.com/itm/6789", _endingTime, "Buy It Now");

		// Test get

		SearchResultModel searchResult =
			_searchResultDAOImpl.getSearchResult(1);

		Assert.assertEquals(1, searchResult.getSearchResultId());
		Assert.assertEquals(1, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals(14.99, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(_endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Auction", searchResult.getTypeOfAuction());

		// Test get multiple

		List<SearchResultModel> searchResultModels =
			_searchResultDAOImpl.getSearchResults();

		Assert.assertEquals(4, searchResultModels.size());

		SearchResultModel secondSearchResult = searchResultModels.get(2);

		Assert.assertEquals(3, secondSearchResult.getSearchResultId());
		Assert.assertEquals(2, secondSearchResult.getSearchQueryId());
		Assert.assertEquals("5678", secondSearchResult.getItemId());
		Assert.assertEquals("Third Item", secondSearchResult.getItemTitle());
		Assert.assertEquals(29.99, secondSearchResult.getAuctionPrice(), 0);
		Assert.assertEquals(29.99, secondSearchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/5678", secondSearchResult.getItemURL());
		Assert.assertEquals(_endingTime, secondSearchResult.getEndingTime());
		Assert.assertEquals(
			"Buy It Now", secondSearchResult.getTypeOfAuction());

		// Test delete multiple

		_searchResultDAOImpl.deleteSearchResult(1);
		_searchResultDAOImpl.deleteSearchResult(2);

		searchResultModels = _searchResultDAOImpl.getSearchResults();

		Assert.assertEquals(2, searchResultModels.size());

		// Test find by search query

		searchResultModels = _searchResultDAOImpl.getSearchQueryResults(2);

		Assert.assertEquals(2, searchResultModels.size());

		_searchResultDAOImpl.deleteSearchQueryResults(2);

		searchResultModels = _searchResultDAOImpl.getSearchQueryResults(2);

		Assert.assertEquals(0, searchResultModels.size());
	}

	private static SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

	private static Date _endingTime;

}