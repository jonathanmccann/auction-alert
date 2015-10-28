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

import com.app.model.SearchQuery;
import com.app.model.SearchResult;

import com.ebay.services.finding.Amount;
import com.ebay.services.finding.FindItemsAdvancedRequest;
import com.ebay.services.finding.FindItemsAdvancedResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SellingStatus;
import com.ebay.services.finding.SortOrderType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultUtil {

	public static List<SearchResult> geteBaySearchResults(
		SearchQuery searchQuery) {

		_log.debug("Searching for: {}", searchQuery.getKeywords());

		FindingServicePortType serviceClient = eBayAPIUtil.getServiceClient();

		com.ebay.services.finding.SearchResult searchResults = null;

		FindItemsAdvancedRequest request = setUpAdvancedRequest(
			searchQuery.getKeywords(),
			searchQuery.getCategoryId());

		FindItemsAdvancedResponse result = serviceClient.findItemsAdvanced(
			request);

		searchResults = result.getSearchResult();

		List<SearchItem> searchItems = searchResults.getItem();

		return createSearchResults(
			searchItems, searchQuery.getSearchQueryId());
	}

	private static SearchResult createSearchResult(SearchItem item) {
		SearchResult searchResult = new SearchResult();

		ListingInfo listingInfo = item.getListingInfo();

		searchResult.setItemId(item.getItemId());
		searchResult.setItemTitle(item.getTitle());
		searchResult.setItemURL(
			_EBAY_URL_PREFIX + searchResult.getItemId());
		searchResult.setGalleryURL(item.getGalleryURL());

		Calendar endTimeCalendar = listingInfo.getEndTime();

		searchResult.setEndingTime(endTimeCalendar.getTime());

		String typeOfAuction = listingInfo.getListingType();

		searchResult.setTypeOfAuction(typeOfAuction);

		setPrice(
			searchResult, listingInfo, item.getSellingStatus(),
			typeOfAuction);

		return searchResult;
	}

	private static List<SearchResult> createSearchResults(
		List<SearchItem> searchItems, int searchQueryId) {

		List<SearchResult> searchResults = new ArrayList<>();

		Collections.reverse(searchItems);

		for (SearchItem searchItem : searchItems) {
			SearchResult searchResult = createSearchResult(
				searchItem);

			searchResult.setSearchQueryId(searchQueryId);

			searchResults.add(searchResult);
		}

		return searchResults;
	}

	private static void setPrice(
		SearchResult searchResult, ListingInfo listingInfo,
		SellingStatus sellingStatus, String typeOfAuction) {

		if ("Auction".equals(typeOfAuction)) {
			Amount currentPrice = sellingStatus.getCurrentPrice();

			searchResult.setAuctionPrice(currentPrice.getValue());
		}
		else if ("FixedPrice".equals(typeOfAuction) ||
				 "StoreInventory".equals(typeOfAuction)) {

			Amount currentPrice = sellingStatus.getCurrentPrice();

			searchResult.setFixedPrice(currentPrice.getValue());
		}
		else if ("AuctionWithBIN".equals(typeOfAuction)) {
			Amount currentPrice = sellingStatus.getCurrentPrice();
			Amount buyItNowPrice = listingInfo.getBuyItNowPrice();

			searchResult.setAuctionPrice(currentPrice.getValue());
			searchResult.setFixedPrice(buyItNowPrice.getValue());
		}
		else {
			_log.error("Unknown type of auction: {}", typeOfAuction);
		}
	}

	private static FindItemsAdvancedRequest setUpAdvancedRequest(
		String keywords, String categoryId) {

		_log.info("Setting up advanced request");

		FindItemsAdvancedRequest request = new FindItemsAdvancedRequest();

		request.setKeywords(keywords);

		if (ValidatorUtil.isNotNull(categoryId)) {
			request.getCategoryId().add(categoryId);
		}

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS);

		request.setPaginationInput(paginationInput);
		request.setSortOrder(SortOrderType.START_TIME_NEWEST);

		return request;
	}

	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearchResultUtil.class);

}