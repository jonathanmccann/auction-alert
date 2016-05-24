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
import com.ebay.services.finding.ItemFilter;
import com.ebay.services.finding.ItemFilterType;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class eBaySearchResultUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> clazz = Class.forName(eBaySearchResultUtil.class.getName());

		_classInstance = clazz.newInstance();

		_createSearchResultMethod = clazz.getDeclaredMethod(
			"_createSearchResult", SearchItem.class);

		_createSearchResultMethod.setAccessible(true);

		_createSearchResultsMethod = clazz.getDeclaredMethod(
			"_createSearchResults", List.class, int.class);

		_createSearchResultsMethod.setAccessible(true);

		_setPriceMethod = clazz.getDeclaredMethod(
			"_setPrice", SearchResult.class, ListingInfo.class,
			SellingStatus.class, String.class);

		_setPriceMethod.setAccessible(true);

		_setUpAdvanceRequestMethod = clazz.getDeclaredMethod(
			"_setUpAdvancedRequest", SearchQuery.class);

		_setUpAdvanceRequestMethod.setAccessible(true);

		setUpProperties();
		setUpServiceClient();
	}

	@Test
	public void testCreateSearchResult() throws Exception {
		SearchItem searchItem = _createSearchItem();

		SearchResult searchResult =
			(SearchResult)_createSearchResultMethod.invoke(
				_classInstance, searchItem);

		Assert.assertEquals(_ITEM_ID, searchResult.getItemId());
		Assert.assertEquals(_ITEM_TITLE, searchResult.getItemTitle());
		Assert.assertEquals(
			_EBAY_URL_PREFIX + searchResult.getItemId(),
			searchResult.getItemURL());
		Assert.assertEquals(_GALLERY_URL, searchResult.getGalleryURL());
		Assert.assertEquals(5.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testCreateSearchResults() throws Exception {
		List<SearchItem> searchItems = new ArrayList<>();

		searchItems.add(_createSearchItem("firstItem"));
		searchItems.add(_createSearchItem("secondItem"));

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
		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "eBay");

		List<SearchResult> eBaySearchResults =
			eBaySearchResultUtil.geteBaySearchResults(searchQuery);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

	@Test
	public void testGeteBaySearchResultsWithCategory() throws Exception {
		SearchQuery searchQuery = new SearchQuery(1, _USER_ID, "eBay", "267");

		List<SearchResult> eBaySearchResults =
			eBaySearchResultUtil.geteBaySearchResults(searchQuery);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

	@Test
	public void testSetAuctionPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, _createListingInfo(),
			_createSellingStatus(), _AUCTION);

		Assert.assertEquals(5.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetAuctionWithBINPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, _createListingInfo(),
			_createSellingStatus(), _AUCTION_WITH_BIN);

		Assert.assertEquals(5.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(10.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetFixedPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, _createListingInfo(),
			_createSellingStatus(), _FIXED_PRICE);

		Assert.assertEquals(0.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(5.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetStoreInventoryPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, _createListingInfo(),
			_createSellingStatus(), _STORE_INVENTORY);

		Assert.assertEquals(0.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(5.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetUnknownTypeOfAuctionPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, _createListingInfo(),
			_createSellingStatus(), _UNKNOWN);

		Assert.assertEquals(0.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetUpAdvancedRequest() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", false, false, true,
			true, true, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		Assert.assertEquals(
			"Test keywords", findItemsAdvancedRequest.getKeywords());

		List<String> categoryIds = findItemsAdvancedRequest.getCategoryId();

		Assert.assertEquals("100", categoryIds.get(0));
		Assert.assertEquals(1, categoryIds.size());

		PaginationInput paginationInput =
			findItemsAdvancedRequest.getPaginationInput();

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS,
			(int)paginationInput.getEntriesPerPage());

		Assert.assertEquals(
			SortOrderType.START_TIME_NEWEST,
			findItemsAdvancedRequest.getSortOrder());
	}

	@Test
	public void testSetUpAdvancedRequestWithAuctionListing() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, true,
			true, true, false, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.LISTING_TYPE, itemFilter.getName());
		Assert.assertEquals("AuctionWithBIN", itemFilter.getValue().get(0));
		Assert.assertEquals("Auction", itemFilter.getValue().get(1));
	}

	@Test
	public void testSetUpAdvancedRequestWithDescriptionSearch()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", true, false, true, true, true,
			true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		Assert.assertTrue(findItemsAdvancedRequest.isDescriptionSearch());
	}

	@Test
	public void testSetUpAdvancedRequestWithFixedPriceListing()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, true,
			true, false, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.LISTING_TYPE, itemFilter.getName());
		Assert.assertEquals("AuctionWithBIN", itemFilter.getValue().get(0));
		Assert.assertEquals("FixedPrice", itemFilter.getValue().get(1));
	}

	@Test
	public void testSetUpAdvancedRequestWithFreeShippingOnly()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, true, true, true, true,
			true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(
			ItemFilterType.FREE_SHIPPING_ONLY, itemFilter.getName());
		Assert.assertEquals("true", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithMaxPrice() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, true,
			true, true, true, 0.00, 10.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.MAX_PRICE, itemFilter.getName());
		Assert.assertEquals("10.00", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithMinPrice() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, true,
			true, true, true, 5.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.MIN_PRICE, itemFilter.getName());
		Assert.assertEquals("5.00", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithMultipleParameters()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "100", "200", true, true, true, true,
			false, true, false, 5.00, 10.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		Assert.assertTrue(findItemsAdvancedRequest.isDescriptionSearch());

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(
			ItemFilterType.FREE_SHIPPING_ONLY, itemFilter.getName());
		Assert.assertEquals("true", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(1);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("New", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(2);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Used", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(3);

		Assert.assertEquals(ItemFilterType.LISTING_TYPE, itemFilter.getName());
		Assert.assertEquals("AuctionWithBIN", itemFilter.getValue().get(0));
		Assert.assertEquals("Auction", itemFilter.getValue().get(1));

		itemFilter = itemFilters.get(4);

		Assert.assertEquals(ItemFilterType.MIN_PRICE, itemFilter.getName());
		Assert.assertEquals("5.00", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(5);

		Assert.assertEquals(ItemFilterType.MAX_PRICE, itemFilter.getName());
		Assert.assertEquals("10.00", itemFilter.getValue().get(0));

		List<String> categoryIds = findItemsAdvancedRequest.getCategoryId();

		Assert.assertEquals("100", categoryIds.get(0));
		Assert.assertEquals(1, categoryIds.size());

		PaginationInput paginationInput =
			findItemsAdvancedRequest.getPaginationInput();

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS,
			(int)paginationInput.getEntriesPerPage());

		Assert.assertEquals(
			SortOrderType.START_TIME_NEWEST,
			findItemsAdvancedRequest.getSortOrder());
	}

	@Test
	public void testSetUpAdvancedRequestWithNewAndUnspecifiedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, false,
			true, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("New", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(1);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Unspecified", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithNewAndUsedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, true,
			false, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("New", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(1);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Used", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithNewCondition() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, false,
			false, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("New", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithoutCategoryId() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, true, true,
			true, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		Assert.assertEquals(
			"Test keywords", findItemsAdvancedRequest.getKeywords());

		List<String> categoryIds = findItemsAdvancedRequest.getCategoryId();

		Assert.assertEquals(0, categoryIds.size());

		PaginationInput paginationInput =
			findItemsAdvancedRequest.getPaginationInput();

		Assert.assertEquals(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS,
			(int)paginationInput.getEntriesPerPage());

		Assert.assertEquals(
			SortOrderType.START_TIME_NEWEST,
			findItemsAdvancedRequest.getSortOrder());
	}

	@Test
	public void testSetUpAdvancedRequestWithUnspecifiedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, false, false,
			true, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Unspecified", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithUsedAndUnspecifiedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, false, true,
			true, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Used", itemFilter.getValue().get(0));

		itemFilter = itemFilters.get(1);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Unspecified", itemFilter.getValue().get(0));
	}

	@Test
	public void testSetUpAdvancedRequestWithUsedCondition() throws Exception {
		SearchQuery searchQuery = new SearchQuery(
			1, _USER_ID, "Test keywords", "", "", false, false, false, true,
			false, true, true, 0.00, 0.00, false);

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery);

		List<ItemFilter> itemFilters = findItemsAdvancedRequest.getItemFilter();

		ItemFilter itemFilter = itemFilters.get(0);

		Assert.assertEquals(ItemFilterType.CONDITION, itemFilter.getName());
		Assert.assertEquals("Used", itemFilter.getValue().get(0));
	}

	private static ListingInfo _createListingInfo() {
		Amount buyItNowPrice = new Amount();

		buyItNowPrice.setValue(10.00);

		ListingInfo listingInfo = new ListingInfo();

		listingInfo.setBuyItNowPrice(buyItNowPrice);

		return listingInfo;
	}

	private static SellingStatus _createSellingStatus() {
		Amount currentPrice = new Amount();

		currentPrice.setValue(5.00);

		SellingStatus sellingStatus = new SellingStatus();

		sellingStatus.setCurrentPrice(currentPrice);

		return sellingStatus;
	}

	private static SearchItem _createSearchItem() {
		return _createSearchItem(null);
	}

	private static SearchItem _createSearchItem(String itemId) {
		SearchItem searchItem = new SearchItem();

		if (ValidatorUtil.isNull(itemId)) {
			itemId = _ITEM_ID;
		}

		ListingInfo listingInfo = _createListingInfo();

		listingInfo.setEndTime(_CALENDAR);
		listingInfo.setListingType(_AUCTION);

		searchItem.setGalleryURL(_GALLERY_URL);
		searchItem.setItemId(itemId);
		searchItem.setListingInfo(listingInfo);
		searchItem.setTitle(_ITEM_TITLE);
		searchItem.setSellingStatus(_createSellingStatus());

		return searchItem;
	}

	private static final String _AUCTION = "Auction";

	private static final String _AUCTION_WITH_BIN = "AuctionWithBIN";

	private static final Calendar _CALENDAR = Calendar.getInstance();

	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";

	private static final String _FIXED_PRICE = "FixedPrice";

	private static final String _GALLERY_URL = "http://www.test.com";

	private static final String _ITEM_ID = "itemId";

	private static final String _ITEM_TITLE = "Item Title";

	private static final String _STORE_INVENTORY = "StoreInventory";

	private static final String _UNKNOWN = "Unknown";

	private static final int _USER_ID = 1;

	private static Object _classInstance;
	private static Method _createSearchResultMethod;
	private static Method _createSearchResultsMethod;
	private static Method _setPriceMethod;
	private static Method _setUpAdvanceRequestMethod;

}