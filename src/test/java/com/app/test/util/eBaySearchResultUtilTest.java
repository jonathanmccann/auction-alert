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

import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;
import com.app.util.PropertiesKeys;
import com.app.util.PropertiesUtil;
import com.app.util.PropertiesValues;
import com.app.util.eBayAPIUtil;
import com.app.util.eBaySearchResultUtil;

import com.ebay.services.finding.Amount;
import com.ebay.services.finding.FindItemsAdvancedRequest;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SellingStatus;
import com.ebay.services.finding.SortOrderType;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultUtilTest {

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		eBayAPIUtil.loadeBayServiceClient(
			System.getProperty(PropertiesKeys.APPLICATION_ID));

		clazz = Class.forName(eBaySearchResultUtil.class.getName());

		_classInstance = clazz.newInstance();

		_setPriceMethod = clazz.getDeclaredMethod(
			"setPrice", SearchResultModel.class, ListingInfo.class,
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
	public void testGeteBaySearchResults() throws Exception {
		SearchQueryModel searchQueryModel = new SearchQueryModel(1, "eBay");

		List<SearchResultModel> eBaySearchResults =
			eBaySearchResultUtil.geteBaySearchResults(searchQueryModel);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

	@Test
	public void testSetAuctionPrice() throws Exception {
		SearchResultModel searchResultModel = new SearchResultModel();

		_setPriceMethod.invoke(
			_classInstance, searchResultModel, createListingInfo(),
			createSellingStatus(), _AUCTION);

		Assert.assertEquals(5.00, searchResultModel.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResultModel.getFixedPrice(), 0);
	}

	@Test
	public void testSetAuctionWithBINPrice() throws Exception {
		SearchResultModel searchResultModel = new SearchResultModel();

		_setPriceMethod.invoke(
			_classInstance, searchResultModel, createListingInfo(),
			createSellingStatus(), _AUCTION_WITH_BIN);

		Assert.assertEquals(5.00, searchResultModel.getAuctionPrice(), 0);
		Assert.assertEquals(10.00, searchResultModel.getFixedPrice(), 0);
	}

	@Test
	public void testSetFixedPrice() throws Exception {
		SearchResultModel searchResultModel = new SearchResultModel();

		_setPriceMethod.invoke(
			_classInstance, searchResultModel, createListingInfo(),
			createSellingStatus(), _FIXED_PRICE);

		Assert.assertEquals(0.00, searchResultModel.getAuctionPrice(), 0);
		Assert.assertEquals(5.00, searchResultModel.getFixedPrice(), 0);
	}

	@Test
	public void testSetStoreInventoryPrice() throws Exception {
		SearchResultModel searchResultModel = new SearchResultModel();

		_setPriceMethod.invoke(
			_classInstance, searchResultModel, createListingInfo(),
			createSellingStatus(), _STORE_INVENTORY);

		Assert.assertEquals(0.00, searchResultModel.getAuctionPrice(), 0);
		Assert.assertEquals(5.00, searchResultModel.getFixedPrice(), 0);
	}

	@Test
	public void testSetUnknownTypeOfAuctionPrice() throws Exception {
		SearchResultModel searchResultModel = new SearchResultModel();

		_setPriceMethod.invoke(
			_classInstance, searchResultModel, createListingInfo(),
			createSellingStatus(), _UNKNOWN);

		Assert.assertEquals(0.00, searchResultModel.getAuctionPrice(), 0);
		Assert.assertEquals(0.00, searchResultModel.getFixedPrice(), 0);
	}

	@Test
	public void testSetUpAdvancedRequest() throws Exception {
		String searchQuery = "Test search query";
		String categoryId = "1";

		FindItemsAdvancedRequest findItemsAdvancedRequest =
			(FindItemsAdvancedRequest)_setUpAdvanceRequestMethod.invoke(
				_classInstance, searchQuery, categoryId);

		Assert.assertEquals(
			searchQuery, findItemsAdvancedRequest.getKeywords());

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
		String searchQuery = "Test search query";

		FindItemsByKeywordsRequest findItemsAdvancedRequest =
			(FindItemsByKeywordsRequest)_setUpRequestMethod.invoke(
				_classInstance, searchQuery);

		Assert.assertEquals(
			searchQuery, findItemsAdvancedRequest.getKeywords());

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

	private static SellingStatus createSellingStatus() {
		Amount currentPrice = new Amount();
		currentPrice.setValue(5.00);

		SellingStatus sellingStatus = new SellingStatus();
		sellingStatus.setCurrentPrice(currentPrice);

		return sellingStatus;
	}

	private static Method _setPriceMethod;
	private static Method _setUpAdvanceRequestMethod;
	private static Method _setUpRequestMethod;

	private static Object _classInstance;

	private static final String _AUCTION = "Auction";
	private static final String _AUCTION_WITH_BIN = "AuctionWithBIN";
	private static final String _FIXED_PRICE = "FixedPrice";
	private static final String _STORE_INVENTORY = "StoreInventory";
	private static final String _UNKNOWN = "Unknown";

}