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
			"1234", "itemTitle", "itemDetails", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", endingTime, "Buy It Now");

		Assert.assertEquals("1234", searchResultModel.getItemId());
		Assert.assertEquals("itemTitle", searchResultModel.getItemTitle());
		Assert.assertEquals("itemDetails", searchResultModel.getItemDetails());
		Assert.assertEquals(14.99, searchResultModel.getItemAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResultModel.getItemFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResultModel.getItemURL());
		Assert.assertEquals(endingTime, searchResultModel.getItemEndingTime());
		Assert.assertEquals("Buy It Now", searchResultModel.getTypeOfAuction());
	}

	@Test
	public void testSetAndGetItemAuctionPrice() throws Exception {
		_searchResultModel.setItemAuctionPrice(14.99);

		Assert.assertEquals(14.99, _searchResultModel.getItemAuctionPrice(), 0);
	}

	@Test
	public void testSetAndGetItemDetails() throws Exception {
		_searchResultModel.setItemDetails("itemDetails");

		Assert.assertEquals("itemDetails", _searchResultModel.getItemDetails());
	}

	@Test
	public void testSetAndGetItemEndingTime() throws Exception {
		Date endingTime = new Date();

		_searchResultModel.setItemEndingTime(endingTime);

		Assert.assertEquals(endingTime, _searchResultModel.getItemEndingTime());
	}

	@Test
	public void testSetAndGetItemFixedPrice() throws Exception {
		_searchResultModel.setItemFixedPrice(14.99);

		Assert.assertEquals(14.99, _searchResultModel.getItemFixedPrice(), 0);
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