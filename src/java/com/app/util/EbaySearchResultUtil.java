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
import com.app.json.ebay.Error;
import com.app.json.ebay.browse.CurrentBidPrice;
import com.app.json.ebay.browse.EbaySearchResultJsonResponse;
import com.app.json.ebay.browse.ItemSummary;
import com.app.json.ebay.browse.Price;
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

		StringBuilder url = new StringBuilder();

		url.append(_BROWSE_URL_PREFIX);

		url.append("limit=");
		url.append(PropertiesValues.NUMBER_OF_SEARCH_RESULTS);

		url.append("&sort=newlyListed");

		url.append("&q=");
		url.append(URLEncoder.encode(searchQuery.getKeywords(), "UTF-8"));

		if (ValidatorUtil.isNotNull(searchQuery.getSubcategoryId())) {
			url.append("&category_ids=");
			url.append(searchQuery.getSubcategoryId());
		}
		else if (ValidatorUtil.isNotNull(searchQuery.getCategoryId())) {
			url.append("&category_ids=");
			url.append(searchQuery.getCategoryId());
		}

		StringBuilder filter = new StringBuilder();

		if (searchQuery.isSearchDescription()) {
			filter.append("searchInDescription:true");
		}

		if (searchQuery.isFreeShippingOnly()) {
			if (filter.length() > 0) {
				filter.append(",");
			}

			filter.append("maxDeliveryCost:0");
		}

		if (!searchQuery.isNewCondition() || !searchQuery.isUsedCondition() ||
			!searchQuery.isUnspecifiedCondition()) {

			StringBuilder conditions = new StringBuilder();

			if (searchQuery.isNewCondition()) {
				conditions.append("NEW");
			}

			if (searchQuery.isUsedCondition()) {
				if (conditions.length() > 0) {
					conditions.append("|");
				}

				conditions.append("USED");
			}

			if (searchQuery.isUnspecifiedCondition()) {
				if (conditions.length() > 0) {
					conditions.append("|");
				}

				conditions.append("UNSPECIFIED");
			}

			if (filter.length() > 0) {
				filter.append(",");
			}

			filter.append("conditions:{");
			filter.append(conditions.toString());
			filter.append("}");
		}

		StringBuilder listingTypes = new StringBuilder();

		if (searchQuery.isAuctionListing()) {
			listingTypes.append("AUCTION");
		}

		if (searchQuery.isFixedPriceListing()) {
			if (listingTypes.length() > 0) {
				listingTypes.append("|");
			}

			listingTypes.append("FIXED_PRICE");
		}

		if (filter.length() > 0) {
			filter.append(",");
		}

		filter.append("buyingOptions:{");
		filter.append(listingTypes.toString());
		filter.append("}");

		if ((searchQuery.getMinPrice() > 0) ||
			(searchQuery.getMaxPrice() > 0)) {

			if (filter.length() > 0) {
				filter.append(",");
			}

			filter.append("price:[");
			filter.append(searchQuery.getMinPrice());
			filter.append("..");
			filter.append(searchQuery.getMaxPrice());
			filter.append("],");
			filter.append("priceCurrency:USD");
		}

		url.append("&filter=");
		url.append(URLEncoder.encode(filter.toString(), "UTF-8"));

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

	private static final DecimalFormat _DISPLAY_DECIMAL_FORMAT =
		new DecimalFormat("#,##0.00");

	private static final String _EBAY_ROOT_URL = "https://www.ebay.com/itm/";

	private static final String _BROWSE_URL_PREFIX =
		"https://api.ebay.com/buy/browse/v1/item_summary/search?";

	private static final Pattern _ITEM_TITLE_PATTERN = Pattern.compile(
		"\\P{Print}");

	private static final Logger _log = LoggerFactory.getLogger(
		EbaySearchResultUtil.class);

}