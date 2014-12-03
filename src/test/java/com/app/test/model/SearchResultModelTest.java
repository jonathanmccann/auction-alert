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
		Map<String, String> sellerInformation = new HashMap<String, String>();

		sellerInformation.put("sellerUsername", "test");
		sellerInformation.put("sellerFeedbackPercentage", "98.7");

		Date endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			"itemTitle", "itemDetails", "14.99", "http://www.ebay.com/itm/1234",
			sellerInformation, endingTime, "Buy It Now");

		String itemTitle = searchResultModel.getItemTitle();

		Assert.assertEquals("itemTitle", itemTitle);

		String itemDetails = searchResultModel.getItemDetails();

		Assert.assertEquals("itemDetails", itemDetails);

		String itemPrice = searchResultModel.getItemPrice();

		Assert.assertEquals("14.99", itemPrice);

		String itemURL = searchResultModel.getItemURL();

		Assert.assertEquals("http://www.ebay.com/itm/1234", itemURL);

		Map<String, String> retrievedSellerInformation =
			searchResultModel.getSellerInformation();

		Assert.assertEquals(sellerInformation, retrievedSellerInformation);

		Date retrievedEndingTime = searchResultModel.getItemEndingTime();

		Assert.assertEquals(endingTime, retrievedEndingTime);

		String typeOfAuction = searchResultModel.getTypeOfAuction();

		Assert.assertEquals("Buy It Now", typeOfAuction);
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
	public void testSetAndGetItemPrice() throws Exception {
		_searchResultModel.setItemPrice("14.99");

		String itemPrice = _searchResultModel.getItemPrice();

		Assert.assertEquals("14.99", itemPrice);
	}

	@Test
	public void testSetAndGetItemURL() throws Exception {
		_searchResultModel.setItemURL("http://www.ebay.com/itm/1234");

		String itemURL = _searchResultModel.getItemURL();

		Assert.assertEquals("http://www.ebay.com/itm/1234", itemURL);
	}

	@Test
	public void testSetAndGetSellerInformation() throws Exception {
		Map<String, String> sellerInformation = new HashMap<String, String>();

		sellerInformation.put("sellerUsername", "test");
		sellerInformation.put("sellerFeedbackPercentage", "98.7");

		_searchResultModel.setSellerInformation(sellerInformation);

		Map<String, String> retrievedSellerInformation =
			_searchResultModel.getSellerInformation();

		Assert.assertEquals(sellerInformation, retrievedSellerInformation);
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