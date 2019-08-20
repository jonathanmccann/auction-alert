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

package com.app.util;

import com.app.exception.DatabaseConnectionException;
import com.app.json.BuyItNowPrice;
import com.app.json.CurrentPrice;
import com.app.json.EbaySearchResultJsonResponse;
import com.app.json.FindItemsAdvancedResponse;
import com.app.json.Item;
import com.app.json.JsonSearchResult;
import com.app.json.ListingInfo;
import com.app.json.SellingStatus;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class EbaySearchResultUtil {

	public static List<SearchResult> getEbaySearchResults(
			SearchQuery searchQuery)
		throws DatabaseConnectionException, IOException, SQLException {

		_log.debug("Searching for: {}", searchQuery.getKeywords());

		User user = UserUtil.getUserByUserId(searchQuery.getUserId());

		String preferredDomain = user.getPreferredDomain();

		String preferredCurrency = ConstantsUtil.getPreferredCurrency(
			preferredDomain);

		String url = _setUpAdvancedRequest(searchQuery, preferredCurrency);

		HttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpClient.execute(httpGet);

		Gson gson = new Gson();

		EbaySearchResultJsonResponse ebaySearchResultJsonResponse =
			gson.fromJson(
				EntityUtils.toString(response.getEntity()),
				EbaySearchResultJsonResponse.class);

		return _createSearchResults(
			ebaySearchResultJsonResponse, searchQuery.getSearchQueryId(),
			preferredDomain, preferredCurrency);
	}

	private static SearchResult _createSearchResult(
		Item item, String preferredDomain, String preferredCurrency) {

		SearchResult searchResult = new SearchResult();

		ListingInfo listingInfo = item.getListingInfo();

		searchResult.setItemId(item.getItemId());
		searchResult.setItemTitle(
			_ITEM_TITLE_PATTERN.matcher(item.getTitle()).replaceAll(""));
		searchResult.setItemURL(preferredDomain + searchResult.getItemId());
		searchResult.setGalleryURL(item.getGalleryURL());

		_setPrice(
			searchResult, preferredCurrency, listingInfo,
			item.getSellingStatus(), listingInfo.getListingType());

		return searchResult;
	}

	private static List<Item> getSearchResultJsonObject(
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse) {

		FindItemsAdvancedResponse findItemsAdvancedResponse =
			ebaySearchResultJsonResponse.getFindItemsAdvancedResponse();

		JsonSearchResult jsonSearchResult =
			findItemsAdvancedResponse.getJsonSearchResult();

		return jsonSearchResult.getItems();
	}

	private static List<SearchResult> _createSearchResults(
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse,
		int searchQueryId, String preferredDomain, String preferredCurrency) {

		List<SearchResult> searchResults = new ArrayList<>();

		for (Item item :
				getSearchResultJsonObject(ebaySearchResultJsonResponse)) {

			SearchResult searchResult = _createSearchResult(
				item, preferredDomain, preferredCurrency);

			searchResult.setSearchQueryId(searchQueryId);

			searchResults.add(searchResult);
		}

		Collections.reverse(searchResults);

		return searchResults;
	}

	private static void _setPrice(
		SearchResult searchResult, String preferredCurrency,
		ListingInfo listingInfo, SellingStatus sellingStatus,
		String typeOfAuction) {

		CurrentPrice currentPrice = sellingStatus.getCurrentPrice();
		BuyItNowPrice buyItNowPrice = listingInfo.getBuyItNowPrice();

		double auctionPrice = ExchangeRateUtil.convertCurrency(
			currentPrice.getCurrencyId(), preferredCurrency,
			currentPrice.getValue());

		if ("Auction".equals(typeOfAuction)) {
			searchResult.setAuctionPrice(
				ConstantsUtil.getCurrencySymbol(preferredCurrency) +
					_DISPLAY_DECIMAL_FORMAT.format(auctionPrice));
		}
		else if ("FixedPrice".equals(typeOfAuction) ||
				 "StoreInventory".equals(typeOfAuction)) {

			searchResult.setFixedPrice(
				ConstantsUtil.getCurrencySymbol(preferredCurrency) +
					_DISPLAY_DECIMAL_FORMAT.format(auctionPrice));
		}
		else if ("AuctionWithBIN".equals(typeOfAuction)) {
			double fixedPrice = ExchangeRateUtil.convertCurrency(
				buyItNowPrice.getCurrencyId(), preferredCurrency,
				buyItNowPrice.getValue());

			searchResult.setAuctionPrice(
				ConstantsUtil.getCurrencySymbol(preferredCurrency) +
					_DISPLAY_DECIMAL_FORMAT.format(auctionPrice));
			searchResult.setFixedPrice(
				ConstantsUtil.getCurrencySymbol(preferredCurrency) +
					_DISPLAY_DECIMAL_FORMAT.format(fixedPrice));
		}
		else {
			_log.error(
				"Unknown type of auction: {} for item ID: {}", typeOfAuction,
				searchResult.getItemId());
		}
	}

	private static String _setUpAdvancedRequest(
			SearchQuery searchQuery, String preferredCurrency)
		throws UnsupportedEncodingException {

		_log.debug("Setting up advanced request");

		int itemFilterCount = 0;

		StringBuilder url = new StringBuilder();

		url.append(_FIND_ITEMS_ADVANCED_URL_PREFIX);

		url.append("&GLOBAL-ID=");
		url.append(searchQuery.getGlobalId());

		url.append("&REST-PAYLOAD");

		url.append("&affiliate.trackingId=");
		url.append(PropertiesValues.EBAY_CAMPAIGN_ID);
		url.append("&affiliate.networkId=");
		url.append(_NETWORK_ID);

		url.append("&paginationInput.entriesPerPage=");
		url.append(PropertiesValues.NUMBER_OF_SEARCH_RESULTS);

		url.append("&sortOrder=StartTimeNewest");

		url.append("&keywords=");
		url.append(URLEncoder.encode(searchQuery.getKeywords(), "UTF-8"));

		if (ValidatorUtil.isNotNull(searchQuery.getSubcategoryId())) {
			url.append("&categoryId=");
			url.append(searchQuery.getSubcategoryId());
		}
		else if (ValidatorUtil.isNotNull(searchQuery.getCategoryId())) {
			url.append("&categoryId=");
			url.append(searchQuery.getCategoryId());
		}

		if (searchQuery.isSearchDescription()) {
			url.append("&descriptionSearch=true");
		}

		if (searchQuery.isFreeShippingOnly()) {
			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").name=FreeShippingOnly");

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").value=true");

			itemFilterCount++;
		}

		if (!searchQuery.isNewCondition() || !searchQuery.isUsedCondition() ||
			!searchQuery.isUnspecifiedCondition()) {

			int valueCount = 0;

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").name=Condition");

			if (searchQuery.isNewCondition()) {
				url.append("&itemFilter(");
				url.append(itemFilterCount);
				url.append(").value(");
				url.append(valueCount);
				url.append(")=New");

				valueCount++;
			}

			if (searchQuery.isUsedCondition()) {
				url.append("&itemFilter(");
				url.append(itemFilterCount);
				url.append(").value(");
				url.append(valueCount);
				url.append(")=Used");

				valueCount++;
			}

			if (searchQuery.isUnspecifiedCondition()) {
				url.append("&itemFilter(");
				url.append(itemFilterCount);
				url.append(").value(");
				url.append(valueCount);
				url.append(")=Unspecified");
			}

			itemFilterCount++;
		}

		if (!searchQuery.isAuctionListing() ||
			!searchQuery.isFixedPriceListing()) {

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").name=ListingType");

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").value(0)=AuctionWithBIN");

			if (searchQuery.isAuctionListing()) {
				url.append("&itemFilter(");
				url.append(itemFilterCount);
				url.append(").value(1)=Auction");
			}
			else {
				url.append("&itemFilter(");
				url.append(itemFilterCount);
				url.append(").value(1)=FixedPrice");
			}

			itemFilterCount++;
		}

		if (searchQuery.getMinPrice() > 0) {
			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").name=MinPrice");

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").value=");
			url.append(_DECIMAL_FORMAT.format(searchQuery.getMinPrice()));

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").paramName=Currency");

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").paramValue=");
			url.append(preferredCurrency);

			itemFilterCount++;
		}

		if (searchQuery.getMaxPrice() > 0) {
			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").name=MaxPrice");

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").value=");
			url.append(_DECIMAL_FORMAT.format(searchQuery.getMaxPrice()));

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").paramName=Currency");

			url.append("&itemFilter(");
			url.append(itemFilterCount);
			url.append(").paramValue=");
			url.append(preferredCurrency);
		}

		return url.toString();
	}

	private static final DecimalFormat _DECIMAL_FORMAT = new DecimalFormat(
		"0.00");

	private static final DecimalFormat _DISPLAY_DECIMAL_FORMAT =
		new DecimalFormat("#,##0.00");

	private static final String _FIND_ITEMS_ADVANCED_URL_PREFIX =
		"https://svcs.ebay.com/services/search/FindingService/v1?" +
			"OPERATION-NAME=findItemsAdvanced" +
			"&SERVICE-VERSION=1.0.0" +
			"&RESPONSE-DATA-FORMAT=JSON" +
			"&SECURITY-APPNAME=" + PropertiesValues.APPLICATION_ID;

	private static final Pattern _ITEM_TITLE_PATTERN = Pattern.compile(
		"\\P{Print}");

	private static final int _NETWORK_ID = 9;

	private static final Logger _log = LoggerFactory.getLogger(
		EbaySearchResultUtil.class);

}