package com.app.test.model;

import com.app.model.SearchResultModel;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SearchResultModelTest {

	@Before
	public void setUp() {
		_searchResultModel = new SearchResultModel();
	}

	@Test
	public void testConstructor() throws Exception {
		Date endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			"1234", "itemTitle", 14.99, 14.99, "http://www.ebay.com/itm/1234",
			endingTime, "Buy It Now");

		Assert.assertEquals("1234", searchResultModel.getItemId());
		Assert.assertEquals("itemTitle", searchResultModel.getItemTitle());
		Assert.assertEquals(14.99, searchResultModel.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResultModel.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResultModel.getItemURL());
		Assert.assertEquals(endingTime, searchResultModel.getEndingTime());
		Assert.assertEquals("Buy It Now", searchResultModel.getTypeOfAuction());
	}

	@Test
	public void testSetAndGetAuctionPrice() throws Exception {
		_searchResultModel.setAuctionPrice(14.99);

		Assert.assertEquals(14.99, _searchResultModel.getAuctionPrice(), 0);
	}

	@Test
	public void testSetAndGetFixedPrice() throws Exception {
		_searchResultModel.setFixedPrice(14.99);

		Assert.assertEquals(14.99, _searchResultModel.getFixedPrice(), 0);
	}

	@Test
	public void testSetAndGetItemEndingTime() throws Exception {
		Date endingTime = new Date();

		_searchResultModel.setEndingTime(endingTime);

		Assert.assertEquals(endingTime, _searchResultModel.getEndingTime());
	}

	@Test
	public void testSetAndGetItemId() throws Exception {
		_searchResultModel.setItemId("1234");

		Assert.assertEquals("1234", _searchResultModel.getItemId());
	}

	@Test
	public void testSetAndGetItemTitle() throws Exception {
		_searchResultModel.setItemTitle("itemTitle");

		Assert.assertEquals("itemTitle", _searchResultModel.getItemTitle());
	}

	@Test
	public void testSetAndGetItemURL() throws Exception {
		_searchResultModel.setItemURL("http://www.ebay.com/itm/1234");

		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", _searchResultModel.getItemURL());
	}

	@Test
	public void testSetAndGetTypeOfAuction() throws Exception {
		_searchResultModel.setTypeOfAuction("Buy It Now");

		Assert.assertEquals(
			"Buy It Now", _searchResultModel.getTypeOfAuction());
	}

	private SearchResultModel _searchResultModel;

}