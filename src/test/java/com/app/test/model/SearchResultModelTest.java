package com.app.test.model;

import com.app.model.SearchResultModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchResultModelTest {

	@Before
	public void setUp() {
		_searchResultModel = new SearchResultModel();
	}

	@Test
	public void testConstructor() throws Exception {
		Date endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			"1234", "itemTitle", "itemDetails", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", endingTime,
			"Buy It Now");

		String itemId = searchResultModel.getItemId();

		Assert.assertEquals("1234", itemId);

		String itemTitle = searchResultModel.getItemTitle();

		Assert.assertEquals("itemTitle", itemTitle);

		String itemDetails = searchResultModel.getItemDetails();

		Assert.assertEquals("itemDetails", itemDetails);

		double itemAuctionPrice = searchResultModel.getItemAuctionPrice();

		Assert.assertEquals(14.99, itemAuctionPrice, 0);

		double itemFixedPrice = searchResultModel.getItemFixedPrice();

		Assert.assertEquals(14.99, itemFixedPrice, 0);

		String itemURL = searchResultModel.getItemURL();

		Assert.assertEquals("http://www.ebay.com/itm/1234", itemURL);

		Date retrievedEndingTime = searchResultModel.getItemEndingTime();

		Assert.assertEquals(endingTime, retrievedEndingTime);

		String typeOfAuction = searchResultModel.getTypeOfAuction();

		Assert.assertEquals("Buy It Now", typeOfAuction);
	}

	@Test
	public void testSetAndGetItemId() throws Exception {
		_searchResultModel.setItemId("1234");

		String itemId = _searchResultModel.getItemId();

		Assert.assertEquals("1234", itemId);
	}

	@Test
	public void testSetAndGetItemTitle() throws Exception {
		_searchResultModel.setItemTitle("itemTitle");

		String itemTitle = _searchResultModel.getItemTitle();

		Assert.assertEquals("itemTitle", itemTitle);
	}

	@Test
	public void testSetAndGetItemDetails() throws Exception {
		_searchResultModel.setItemDetails("itemDetails");

		String itemDetails = _searchResultModel.getItemDetails();

		Assert.assertEquals("itemDetails", itemDetails);
	}

	@Test
	public void testSetAndGetItemAuctionPrice() throws Exception {
		_searchResultModel.setItemAuctionPrice(14.99);

		double itemAuctionPrice = _searchResultModel.getItemAuctionPrice();

		Assert.assertEquals(14.99, itemAuctionPrice, 0);
	}

	@Test
	public void testSetAndGetItemFixedPrice() throws Exception {
		_searchResultModel.setItemFixedPrice(14.99);

		double itemFixedPrice = _searchResultModel.getItemFixedPrice();

		Assert.assertEquals(14.99, itemFixedPrice, 0);
	}

	@Test
	public void testSetAndGetItemURL() throws Exception {
		_searchResultModel.setItemURL("http://www.ebay.com/itm/1234");

		String itemURL = _searchResultModel.getItemURL();

		Assert.assertEquals("http://www.ebay.com/itm/1234", itemURL);
	}

	@Test
	public void testSetAndGetItemEndingTime() throws Exception {
		Date endingTime = new Date();

		_searchResultModel.setItemEndingTime(endingTime);

		Date retrievedEndingTime = _searchResultModel.getItemEndingTime();

		Assert.assertEquals(endingTime, retrievedEndingTime);
	}

	@Test
	public void testSetAndGetTypeOfAuction() throws Exception {
		_searchResultModel.setTypeOfAuction("Buy It Now");

		String typeOfAuction = _searchResultModel.getTypeOfAuction();

		Assert.assertEquals("Buy It Now", typeOfAuction);
	}

	private SearchResultModel _searchResultModel;
}