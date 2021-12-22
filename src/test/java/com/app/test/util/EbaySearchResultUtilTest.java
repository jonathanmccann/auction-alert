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

import com.app.json.ebay.browse.CurrentBidPrice;
import com.app.json.ebay.browse.EbaySearchResultJsonResponse;
import com.app.json.ebay.browse.ItemSummary;
import com.app.json.ebay.browse.Price;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.ConstantsUtil;
import com.app.util.EbaySearchResultUtil;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({
	HttpClients.class, EntityUtils.class
})
@WebAppConfiguration
public class EbaySearchResultUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();

		Class<?> clazz = Class.forName(EbaySearchResultUtil.class.getName());

		_classInstance = clazz.newInstance();

		_createSearchResultMethod = clazz.getDeclaredMethod(
			"_createSearchResult", ItemSummary.class, String.class);

		_createSearchResultMethod.setAccessible(true);

		_createSearchResultsMethod = clazz.getDeclaredMethod(
			"_createSearchResults", EbaySearchResultJsonResponse.class,
			int.class, int.class, String.class);

		_createSearchResultsMethod.setAccessible(true);

		_setPriceMethod = clazz.getDeclaredMethod(
			"_setPrice", SearchResult.class, String.class, ItemSummary.class);

		_setPriceMethod.setAccessible(true);

		_setUpAdvanceRequestMethod = clazz.getDeclaredMethod(
			"_setUpAdvancedRequest", SearchQuery.class, String.class);

		_setUpAdvanceRequestMethod.setAccessible(true);

		setUpDatabase();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		_USER = UserUtil.addUser("test@liferay.com", "password");
	}

	@After
	public void tearDown() throws Exception {
		UserUtil.deleteUserByUserId(_USER.getUserId());
	}

	@Test
	public void testCreateSearchResult() throws Exception {
		List<ItemSummary> items = _getItems(_getEbaySearchResultJsonResponse());

		SearchResult searchResult =
			(SearchResult)_createSearchResultMethod.invoke(
				_classInstance, items.get(0), "",
				"USD");

		Assert.assertEquals(_ITEM_ID, searchResult.getItemId());
		Assert.assertEquals(_ITEM_TITLE, searchResult.getItemTitle());
		Assert.assertEquals(
			_EBAY_URL_PREFIX + searchResult.getItemId(),
			searchResult.getItemURL());
		Assert.assertEquals(_GALLERY_URL, searchResult.getGalleryURL());
		Assert.assertEquals(_AUCTION_PRICE, searchResult.getAuctionPrice());
		Assert.assertNull(searchResult.getFixedPrice());
	}

	@Test
	public void testCreateSearchResults() throws Exception {
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse =
			_getEbaySearchResultJsonResponse();

		List<SearchResult> searchResults =
			(List<SearchResult>)_createSearchResultsMethod.invoke(
				_classInstance, ebaySearchResultJsonResponse, 1, 1, "USD");

		Assert.assertEquals(2, searchResults.size());

		SearchResult firstSearchResult = searchResults.get(0);
		SearchResult secondSearchResult = searchResults.get(1);

		Assert.assertEquals("itemId2", firstSearchResult.getItemId());
		Assert.assertEquals("itemId1", secondSearchResult.getItemId());
	}

	@Test
	public void testGetEbaySearchResultsWithAuctionResults() throws Exception {
		_setUpGetEbaySearchResults(_AUCTION_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertEquals(2, searchResults.size());

		_assertSearchResult(searchResults.get(0), "2", "$4.00", null);
		_assertSearchResult(searchResults.get(1), "1", "$10.00", null);
	}

	@Test
	public void testGetEbaySearchResultsWithAuctionWithBINResults()
		throws Exception {

		_setUpGetEbaySearchResults(_AUCTION_WITH_BIN_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertEquals(2, searchResults.size());

		_assertSearchResult(searchResults.get(0), "2", "$4.00", "$40.00");
		_assertSearchResult(searchResults.get(1), "1", "$10.00", "$100.00");
	}

	@Test
	public void testGetEbaySearchResultsWithEmptyResults() throws Exception {
		_setUpGetEbaySearchResults(_EMPTY_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertTrue(searchResults.isEmpty());
	}

	@Test
	public void testGetEbaySearchResultsWithFailure() throws Exception {
		_setUpGetEbaySearchResults(_FAILURE_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertTrue(searchResults.isEmpty());
	}

	@Test
	public void testGetEbaySearchResultsWithNullGalleryURL() throws Exception {
		_setUpGetEbaySearchResults(_AUCTION_WITH_NULL_GALLERY_URL_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertNull(searchResults.get(0).getGalleryURL());
	}

	@Test
	public void testGetEbaySearchResultsWithNullResults() throws Exception {
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse =
			new EbaySearchResultJsonResponse();

		List<ItemSummary> itemSummaries =
			ebaySearchResultJsonResponse.getItemSummaries();

		Assert.assertNull(itemSummaries);
	}

	@Test
	public void testGetEbaySearchResultsWithFixedPriceResults()
		throws Exception {

		_setUpGetEbaySearchResults(_FIXED_PRICE_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertEquals(2, searchResults.size());

		_assertSearchResult(searchResults.get(0), "2", null, "$40.00");
		_assertSearchResult(searchResults.get(1), "1", null, "$100.00");
	}

	@Test
	public void testGetEbaySearchResultsWithStoreInventoryResults()
		throws Exception {

		_setUpGetEbaySearchResults(_STORE_INVENTORY_JSON_PATH);

		SearchQuery searchQuery = _createSearchQuery();

		List<SearchResult> searchResults =
			EbaySearchResultUtil.getEbaySearchResults(searchQuery);

		Assert.assertEquals(2, searchResults.size());

		_assertSearchResult(searchResults.get(0), "2", null, "$40.00");
		_assertSearchResult(searchResults.get(1), "1", null, "$100.00");
	}

	@Test
	public void testSetAuctionPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, "USD", _createItemSummary(_AUCTION));

		Assert.assertEquals("$5.00", searchResult.getAuctionPrice());
		Assert.assertNull(searchResult.getFixedPrice());
	}

	@Test
	public void testSetAuctionWithBINPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, "USD",
			_createItemSummary(_AUCTION_WITH_BIN));

		Assert.assertEquals("$5.00", searchResult.getAuctionPrice());
		Assert.assertEquals("$10.00", searchResult.getFixedPrice());
	}

	@Test
	public void testSetConvertedCurrencyPrice() throws Exception {
	}

	@Test
	public void testSetFixedPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, "USD",
			_createItemSummary(_FIXED_PRICE));

		Assert.assertNull(searchResult.getAuctionPrice());
		Assert.assertEquals("$5.00", searchResult.getFixedPrice());
	}

	@Test
	public void testSetStoreInventoryPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, "USD",
			_createItemSummary(_STORE_INVENTORY));

		Assert.assertNull(searchResult.getAuctionPrice());
		Assert.assertEquals("$5.00", searchResult.getFixedPrice());
	}

	@Test
	public void testSetUnknownTypeOfAuctionPrice() throws Exception {
		SearchResult searchResult = new SearchResult();

		_setPriceMethod.invoke(
			_classInstance, searchResult, "USD", _createItemSummary(_UNKNOWN));

		Assert.assertNull(searchResult.getAuctionPrice());
		Assert.assertNull(searchResult.getFixedPrice());
	}

	@Test
	public void testSetUpAdvancedRequest() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(_FIND_ITEMS_ADVANCED_URL_BASE, url);
	}

	@Test
	public void testSetUpAdvancedRequestWithAuctionListing() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=ListingType");
		expectedURL.append("&itemFilter(0).value(0)=AuctionWithBIN");
		expectedURL.append("&itemFilter(0).value(1)=Auction");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithCategoryId() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&categoryId=100");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithDescriptionSearch()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setSearchDescription(true);
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&descriptionSearch=true");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithFixedPriceListing()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=ListingType");
		expectedURL.append("&itemFilter(0).value(0)=AuctionWithBIN");
		expectedURL.append("&itemFilter(0).value(1)=FixedPrice");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithFreeShippingOnly()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setFreeShippingOnly(true);
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=FreeShippingOnly");
		expectedURL.append("&itemFilter(0).value=true");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithMaxPrice() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);
		searchQuery.setMaxPrice(10.00);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=MaxPrice");
		expectedURL.append("&itemFilter(0).value=10.00");
		expectedURL.append("&itemFilter(0).paramName=Currency");
		expectedURL.append("&itemFilter(0).paramValue=USD");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithMinPrice() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);
		searchQuery.setMinPrice(5.00);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=MinPrice");
		expectedURL.append("&itemFilter(0).value=5.00");
		expectedURL.append("&itemFilter(0).paramName=Currency");
		expectedURL.append("&itemFilter(0).paramValue=USD");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithMultipleParameters()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");
		searchQuery.setSearchDescription(true);
		searchQuery.setFreeShippingOnly(true);
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setMinPrice(5.00);
		searchQuery.setMaxPrice(10.00);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&categoryId=200");
		expectedURL.append("&descriptionSearch=true");
		expectedURL.append("&itemFilter(0).name=FreeShippingOnly");
		expectedURL.append("&itemFilter(0).value=true");
		expectedURL.append("&itemFilter(1).name=Condition");
		expectedURL.append("&itemFilter(1).value(0)=New");
		expectedURL.append("&itemFilter(1).value(1)=Used");
		expectedURL.append("&itemFilter(2).name=ListingType");
		expectedURL.append("&itemFilter(2).value(0)=AuctionWithBIN");
		expectedURL.append("&itemFilter(2).value(1)=Auction");
		expectedURL.append("&itemFilter(3).name=MinPrice");
		expectedURL.append("&itemFilter(3).value=5.00");
		expectedURL.append("&itemFilter(3).paramName=Currency");
		expectedURL.append("&itemFilter(3).paramValue=USD");
		expectedURL.append("&itemFilter(4).name=MaxPrice");
		expectedURL.append("&itemFilter(4).value=10.00");
		expectedURL.append("&itemFilter(4).paramName=Currency");
		expectedURL.append("&itemFilter(4).paramValue=USD");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithNewAndUnspecifiedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=Condition");
		expectedURL.append("&itemFilter(0).value(0)=New");
		expectedURL.append("&itemFilter(0).value(1)=Unspecified");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithNewAndUsedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=Condition");
		expectedURL.append("&itemFilter(0).value(0)=New");
		expectedURL.append("&itemFilter(0).value(1)=Used");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithNewCondition() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setNewCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=Condition");
		expectedURL.append("&itemFilter(0).value(0)=New");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithSubcategoryId() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&categoryId=200");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithUnspecifiedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=Condition");
		expectedURL.append("&itemFilter(0).value(0)=Unspecified");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithUsedAndUnspecifiedCondition()
		throws Exception {

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=Condition");
		expectedURL.append("&itemFilter(0).value(0)=Used");
		expectedURL.append("&itemFilter(0).value(1)=Unspecified");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	@Test
	public void testSetUpAdvancedRequestWithUsedCondition() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setUsedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		StringBuilder expectedURL = new StringBuilder();

		expectedURL.append(_FIND_ITEMS_ADVANCED_URL_BASE);
		expectedURL.append("&itemFilter(0).name=Condition");
		expectedURL.append("&itemFilter(0).value(0)=Used");

		String url = (String)_setUpAdvanceRequestMethod.invoke(
			_classInstance, searchQuery, "USD");

		Assert.assertEquals(expectedURL.toString(), url);
	}

	private static ItemSummary _createItemSummary(String listingType)
		throws Exception {

		ItemSummary itemSummary = new ItemSummary();

		Class<?> itemSummaryClass = itemSummary.getClass();

		Field buyingOptionsField = itemSummaryClass.getDeclaredField(
			"buyingOptions");

		Field currentBidPriceField = itemSummaryClass.getDeclaredField(
			"currentBidPrice");

		Field priceField = itemSummaryClass.getDeclaredField("price");

		buyingOptionsField.setAccessible(true);
		currentBidPriceField.setAccessible(true);
		priceField.setAccessible(true);

		List<String> buyingOptionsList = new ArrayList<>();

		buyingOptionsList.add(listingType);

		buyingOptionsField.set(itemSummary, buyingOptionsList);

		CurrentBidPrice currentBidPrice = new CurrentBidPrice();

		Class<?> currentBidPriceClass = currentBidPrice.getClass();

		Field currentBidPriceValue = currentBidPriceClass.getDeclaredField(
			"value");

		currentBidPriceValue.setAccessible(true);

		currentBidPriceValue.set(currentBidPrice, "5");

		currentBidPriceField.set(itemSummary, currentBidPrice);

		Price price = new Price();

		Class<?> priceClass = price.getClass();

		Field priceValue = priceClass.getDeclaredField("value");

		priceValue.setAccessible(true);

		priceValue.set(price, "10");

		priceField.set(itemSummary, price);

		return itemSummary;
	}

	private static SearchQuery _createSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER.getUserId());
		searchQuery.setSearchQueryId(1);
		searchQuery.setKeywords("Test Keywords");

		return searchQuery;
	}

	private static List<ItemSummary> _getItems(
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse) {

		return ebaySearchResultJsonResponse.getItemSummaries();
	}

	private static EbaySearchResultJsonResponse
			_getEbaySearchResultJsonResponse()
		throws Exception {

		Gson gson = new Gson();

		return
			gson.fromJson(
				new String(
					Files.readAllBytes(
						new ClassPathResource(_AUCTION_JSON_PATH)
					.getFile().toPath())),
				EbaySearchResultJsonResponse.class);
	}

	private void _assertSearchResult(
			SearchResult searchResult, String itemNumber, String auctionPrice,
			String fixedPrice)
		throws Exception {

		Assert.assertEquals("itemId" + itemNumber, searchResult.getItemId());
		Assert.assertEquals(
			"Item Title " + itemNumber, searchResult.getItemTitle());
		Assert.assertEquals(
			_EBAY_URL_PREFIX + "itemId" + itemNumber +
				ConstantsUtil.DEFAULT_MARKETPLACE_ID,
			searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/" + itemNumber + ".jpg",
			searchResult.getGalleryURL());

		if (ValidatorUtil.isNull(auctionPrice)) {
			Assert.assertNull(searchResult.getAuctionPrice());
		}
		else {
			Assert.assertEquals(auctionPrice, searchResult.getAuctionPrice());
		}

		if (ValidatorUtil.isNull(fixedPrice)) {
			Assert.assertNull(searchResult.getFixedPrice());
		}
		else {
			Assert.assertEquals(fixedPrice, searchResult.getFixedPrice());
		}
	}

	private void _setUpGetEbaySearchResults(String jsonPath) throws Exception {
		CloseableHttpResponse closeableHttpResponse = Mockito.mock(
			CloseableHttpResponse.class);

		CloseableHttpClient closeableHttpClient = Mockito.mock(
			CloseableHttpClient.class);

		PowerMockito.spy(HttpClients.class);

		PowerMockito.doReturn(
			closeableHttpClient
		).when(
			HttpClients.class, "createDefault"
		);

		Mockito.when(
			closeableHttpClient.execute(Mockito.anyObject())
		).thenReturn(
			closeableHttpResponse
		);

		PowerMockito.spy(EntityUtils.class);

		PowerMockito.doReturn(
			new String(
				Files.readAllBytes(
					new ClassPathResource(jsonPath)
				.getFile().toPath()))
		).when(
			EntityUtils.class, "toString", Mockito.anyObject()
		);
	}

	private static final String _AUCTION = "AUCTION";

	private static final String _AUCTION_JSON_PATH = "/json/ebay/auction.json";

	private static final String _AUCTION_PRICE = "$10.00";

	private static final String _AUCTION_WITH_BIN = "AuctionWithBIN";

	private static final String _AUCTION_WITH_BIN_JSON_PATH =
		"/json/ebay/auctionWithBIN.json";

	private static final String _AUCTION_WITH_NULL_GALLERY_URL_JSON_PATH =
		"/json/ebay/auctionWithNullGalleryURL.json";

	private static final String _EBAY_URL_PREFIX = "https://www.ebay.com/itm/";

	private static final String _EMPTY_JSON_PATH = "/json/ebay/empty.json";

	private static final String _FAILURE_JSON_PATH = "/json/ebay/failure.json";

	private static final String _FIND_ITEMS_ADVANCED_URL_BASE =
		"https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME" +
			"=findItemsAdvanced&SERVICE-VERSION=1.0.0&RESPONSE-DATA-FORMAT=JSON" +
			"&SECURITY-APPNAME=applicationId&GLOBAL-ID=EBAY-US&REST-PAYLOAD" +
			"&affiliate.trackingId=ebayCampaignId&affiliate.networkId=9" +
			"&paginationInput.entriesPerPage=5&sortOrder=StartTimeNewest" +
			"&keywords=Test+keywords";

	private static final String _FIXED_PRICE = "FIXED_PRICE";

	private static final String _FIXED_PRICE_JSON_PATH = "/json/ebay/fixedPrice.json";

	private static final String _GALLERY_URL = "http://www.ebay.com/1.jpg";

	private static final String _ITEM_ID = "itemId1";

	private static final String _ITEM_TITLE = "Item Title 1";

	private static final String _STORE_INVENTORY = "StoreInventory";

	private static final String _STORE_INVENTORY_JSON_PATH =
		"/json/ebay/storeInventory.json";

	private static final String _UNKNOWN = "Unknown";

	private static final int _USER_ID = 1;

	private static Object _classInstance;
	private static Method _createSearchResultMethod;
	private static Method _createSearchResultsMethod;
	private static Method _setPriceMethod;
	private static Method _setUpAdvanceRequestMethod;
	private static User _USER;

}