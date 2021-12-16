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
import com.app.json.ebay.CurrentBidPrice;
import com.app.json.ebay.EbaySearchResultJsonResponse;
import com.app.json.ebay.Error;
import com.app.json.ebay.ItemSummary;
import com.app.json.ebay.Price;
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

		_log.trace("Searching for: {}", searchQuery.getKeywords());

		User user = UserUtil.getUserByUserId(searchQuery.getUserId());

		String preferredDomain = user.getPreferredDomain();

		String preferredCurrency = ConstantsUtil.getPreferredCurrency(
			preferredDomain);

		String url = _setUpAdvancedRequest(searchQuery, preferredCurrency);

		EbaySearchResultJsonResponse ebaySearchResultJsonResponse =
			_executeFindItemsAdvanced(url);

		boolean isValidResponse = validateResponse(
			ebaySearchResultJsonResponse, searchQuery.getSearchQueryId());

		if (isValidResponse) {
			return _createSearchResults(
				ebaySearchResultJsonResponse, searchQuery.getSearchQueryId(),
				searchQuery.getUserId(), preferredDomain, preferredCurrency);
		}
		else {
			return new ArrayList<>();
		}
	}

	private static SearchResult _createSearchResult(
		ItemSummary itemSummary, String preferredDomain, String preferredCurrency) {

		SearchResult searchResult = new SearchResult();

		searchResult.setItemId(itemSummary.getLegacyItemId());
		searchResult.setItemTitle(
			_ITEM_TITLE_PATTERN.matcher(itemSummary.getTitle()).replaceAll(""));
		searchResult.setItemURL(
			_EBAY_ROOT_URL + searchResult.getItemId() + preferredDomain);
		searchResult.setGalleryURL(itemSummary.getImage().getImageUrl());

		_setPrice(searchResult, preferredCurrency, itemSummary);

		return searchResult;
	}

	private static List<SearchResult> _createSearchResults(
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse,
		int searchQueryId, int userId, String preferredDomain,
		String preferredCurrency) {

		List<SearchResult> searchResults = new ArrayList<>();

		for (ItemSummary itemSummary :
				ebaySearchResultJsonResponse.getItemSummaries()) {

			SearchResult searchResult = _createSearchResult(
				itemSummary, preferredDomain, preferredCurrency);

			searchResult.setSearchQueryId(searchQueryId);
			searchResult.setUserId(userId);

			searchResults.add(searchResult);
		}

		Collections.reverse(searchResults);

		return searchResults;
	}

	private static EbaySearchResultJsonResponse _executeFindItemsAdvanced(
			String url)
		throws IOException {

		HttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpClient.execute(httpGet);

		Gson gson = new Gson();

		return
			gson.fromJson(
				EntityUtils.toString(response.getEntity()),
				EbaySearchResultJsonResponse.class);
	}

	private static void _setPrice(
		SearchResult searchResult, String preferredCurrency,
		ItemSummary itemSummary) {

		List<String> buyingOptions = itemSummary.getBuyingOptions();

		if (buyingOptions.contains("AUCTION")) {
			CurrentBidPrice currentBidPrice = itemSummary.getCurrentBidPrice();

			double auctionPrice = ExchangeRateUtil.convertCurrency(
				currentBidPrice.getCurrency(), preferredCurrency,
				Double.valueOf(currentBidPrice.getValue()));

			searchResult.setAuctionPrice(
				ConstantsUtil.getCurrencySymbol(preferredCurrency) +
					_DISPLAY_DECIMAL_FORMAT.format(auctionPrice));
		}

		if (buyingOptions.contains("FIXED_PRICE")) {
			Price price = itemSummary.getPrice();

			double buyItNowPrice = ExchangeRateUtil.convertCurrency(
				price.getCurrency(), preferredCurrency,
				Double.valueOf(price.getValue()));

			searchResult.setFixedPrice(
				ConstantsUtil.getCurrencySymbol(preferredCurrency) +
					_DISPLAY_DECIMAL_FORMAT.format(buyItNowPrice));
		}
	}

	private static String _setUpAdvancedRequest(
			SearchQuery searchQuery, String preferredCurrency)
		throws UnsupportedEncodingException {

		_log.trace("Setting up advanced request");

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

	private static boolean validateResponse(
		EbaySearchResultJsonResponse ebaySearchResultJsonResponse,
		long searchQueryId) {

		List<Error> errors = ebaySearchResultJsonResponse.getErrors();

		if (errors.isEmpty()) {
			return true;
		}

		for (Error error : errors) {
			_log.error(
				"Unable to perform search request for search query ID: {}. " +
					"Received error ID: {} and error message: {}",
				searchQueryId, error.getErrorId(), error.getLongMessage());
		}

		return false;
	}

	private static final DecimalFormat _DECIMAL_FORMAT = new DecimalFormat(
		"0.00");

	private static final DecimalFormat _DISPLAY_DECIMAL_FORMAT =
		new DecimalFormat("#,##0.00");

	private static final String _EBAY_ROOT_URL = "https://www.ebay.com/itm/";

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