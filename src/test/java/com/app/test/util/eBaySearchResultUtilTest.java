/**
 * Copyright (c) 2014-present Jonathan McCann
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package com.app.test.util;

import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.PropertiesValues;
import com.app.util.ValidatorUtil;
import com.app.util.eBaySearchResultUtil;

import com.ebay.services.finding.Amount;
import com.ebay.services.finding.FindItemsAdvancedRequest;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SellingStatus;
import com.ebay.services.finding.SortOrderType;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class eBaySearchResultUtilTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = Class.forName(eBaySearchResultUtil.class.getName());

		_classInstance = clazz.newInstance();

		_createSearchResultMethod = clazz.getDeclaredMethod(
			"createSearchResult", SearchItem.class);

		_createSearchResultMethod.setAccessible(true);

		_createSearchResultsMethod = clazz.getDeclaredMethod(
			"createSearchResults", List.class, int.class);

		_createSearchResultsMethod.setAccessible(true);

		_setPriceMethod = clazz.getDeclaredMethod(
			"setPrice", SearchResult.class, ListingInfo.class,
			SellingStatus.class, String.class);

		_setPriceMethod.setAccessible(true);

		_setUpAdvanceRequestMethod = clazz.getDeclaredMethod(
			"setUpAdvancedRequest", String.class, String.class);

		_setUpAdvanceRequestMethod.setAccessible(true);

		_setUpRequestMethod = clazz.getDeclaredMethod(
			"setUpRequest", String.class);

		_setUpRequestMethod.setAccessible(true);
	}

	@Test
	public void testCreateSearchResult() throws Exception {
		SearchItem searchItem = createSearchItem();

		SearchResult searchResult =
			(SearchResult)_createSearchResultMethod.invoke(
				_classInstance, searchItem);

		Assert.assertEquals(_ITEM_ID, searchResult.getItemId());
		Assert.assertEquals(_ITEM_TITLE, searchResult.getItemTitle());
		Assert.assertEquals(
			_EBAY_URL_PREFIX + searchResult.getItemId(),
			searchResult.getItemURL());
		Assert.assertEquals(_GALLERY_URL, searchResult.getGalleryURL());
		Assert.assertEquals(
			_CALENDAR.getTime(), searchResult.getEndingTime());
		Assert.assertEquals(_AUCTION, searchResult.getTypeOfAuction());
		Assert.assertEquals(5.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testCreateSearchResults() throws Exception {
		List<SearchItem> searchItems = new ArrayList<>();

		searchItems.add(createSearchItem("firstItem"));
		searchItems.add(createSearchItem("secondItem"));

		List<SearchResult> searchResults =
			(List<SearchResult>)_createSearchResultsMethod.invoke(
				_classInstance, searchItems, 1);

		Assert.assertEquals(2, searchResults.size());

		SearchResult firstSearchResult = searchResults.get(0);
		SearchResult secondSearchResult = searchResults.get(1);

		Assert.assertEquals("secondItem", firstSearchResult.getItemId());
		Assert.assertEquals("firstItem", secondSearchResult.getItemId());
	}

	@Test
	public void testGeteBaySearchResults() throws Exception {
		SearchQuery searchQuery = new SearchQuery(1, "eBay");

		List<SearchResult> eBaySearchResults =
			eBaySearchResultUtil.geteBaySearchResults(searchQuery);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

	@Test
	public void testGeteBaySearchResultsWithCategory() throws Exception {
		SearchQuery searchQuery = new SearchQuery(1, "eBay", "267");

		List<SearchResult> eBaySearchResults =
			eBaySearchResultUtil.geteBaySearchResults(searchQuery);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

	@Test
	public void testSetAuctionPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, createListingInfo(),
			createSellingStatus(), _AUCTION);

		Assert.assertEquals(5.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetAuctionWithBINPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, createListingInfo(),
			createSellingStatus(), _AUCTION_WITH_BIN);

		Assert.assertEquals(5.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(10.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetFixedPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, createListingInfo(),
			createSellingStatus(), _FIXED_PRICE);

		Assert.assertEquals(0.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(5.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetStoreInventoryPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, createListingInfo(),
			createSellingStatus(), _STORE_INVENTORY);

		Assert.assertEquals(0.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(5.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetUnknownTypeOfAuctionPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, createListingInfo(),
			createSellingStatus(), _UNKNOWN);

		Assert.assertEquals(0.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetUpAdvancedRequest() throws Exception {
		String keywords = "Test keywords";
		String categoryId = "1";

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, keywords, categoryId);

		Assert.assertEquals(
			keywords, findItemsAdvancedRequest.getKeywords());

		List<String> categoryIds = findItemsAdvancedRequest.getCategoryId();

		Assert.assertEquals(categoryId, categoryIds.get(0));
		Assert.assertEquals(1, categoryIds.size());

		PaginationInput paginationInput =
			findItemsAdvancedRequest.getPaginationInput();

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS,
			(int) paginationInput.getEntriesPerPage());

		Assert.assertEquals(
			SortOrderType.START_TIME_NEWEST,
			findItemsAdvancedRequest.getSortOrder());
	}

	@Test
	public void testSetUpRequest() throws Exception {
		String keywords = "Test keywords";

		FindItemsByKeywordsRequest findItemsAdvancedRequest =
			(FindItemsByKeywordsRequest)_setUpRequestMethod.invoke(
				_classInstance, keywords);

		Assert.assertEquals(
			keywords, findItemsAdvancedRequest.getKeywords());

		PaginationInput paginationInput =
			findItemsAdvancedRequest.getPaginationInput();

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS,
			(int)paginationInput.getEntriesPerPage());

		Assert.assertEquals(
			SortOrderType.START_TIME_NEWEST,
			findItemsAdvancedRequest.getSortOrder());
	}

	private static ListingInfo createListingInfo() {
		Amount buyItNowPrice = new Amount();
		buyItNowPrice.setValue(10.00);

		ListingInfo listingInfo = new ListingInfo();
		listingInfo.setBuyItNowPrice(buyItNowPrice);

		return listingInfo;
	}

	private static SearchItem createSearchItem(String itemId) {
		SearchItem searchItem = new SearchItem();

		if (ValidatorUtil.isNull(itemId)) {
			itemId = _ITEM_ID;
		}

		ListingInfo listingInfo = createListingInfo();

		listingInfo.setEndTime(_CALENDAR);
		listingInfo.setListingType(_AUCTION);

		searchItem.setGalleryURL(_GALLERY_URL);
		searchItem.setItemId(itemId);
		searchItem.setListingInfo(listingInfo);
		searchItem.setTitle(_ITEM_TITLE);
		searchItem.setSellingStatus(createSellingStatus());

		return searchItem;
	}

	private static SearchItem createSearchItem() {
		return createSearchItem(null);
	}

	private static SellingStatus createSellingStatus() {
		Amount currentPrice = new Amount();
		currentPrice.setValue(5.00);

		SellingStatus sellingStatus = new SellingStatus();
		sellingStatus.setCurrentPrice(currentPrice);

		return sellingStatus;
	}

	private static final Calendar _CALENDAR = Calendar.getInstance();

	private static Method _createSearchResultMethod;
	private static Method _createSearchResultsMethod;
	private static Method _setPriceMethod;
	private static Method _setUpAdvanceRequestMethod;
	private static Method _setUpRequestMethod;

	private static Object _classInstance;

	private static final String _AUCTION = "Auction";
	private static final String _AUCTION_WITH_BIN = "AuctionWithBIN";
	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";
	private static final String _FIXED_PRICE = "FixedPrice";
	private static final String _GALLERY_URL = "http://www.test.com";
	private static final String _ITEM_ID = "itemId";
	private static final String _ITEM_TITLE = "Item Title";
	private static final String _STORE_INVENTORY = "StoreInventory";
	private static final String _UNKNOWN = "Unknown";

}