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

import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;

import com.ebay.services.finding.Amount;
import com.ebay.services.finding.FindItemsAdvancedRequest;
import com.ebay.services.finding.FindItemsAdvancedResponse;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SearchResult;
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

	public static List<SearchResultModel> geteBaySearchResults(
		SearchQueryModel searchQueryModel) {

		_log.debug("Searching for: {}", searchQueryModel.getSearchQuery());

		FindingServicePortType serviceClient = eBayAPIUtil.getServiceClient();

		SearchResult searchResults = null;

		if (searchQueryModel.getCategoryId() == null) {
			FindItemsByKeywordsRequest request = setUpRequest(
				searchQueryModel.getSearchQuery());

			FindItemsByKeywordsResponse result =
				serviceClient.findItemsByKeywords(request);

			searchResults = result.getSearchResult();
		}
		else {
			FindItemsAdvancedRequest request = setUpAdvancedRequest(
				searchQueryModel.getSearchQuery(),
				searchQueryModel.getCategoryId());

			FindItemsAdvancedResponse result = serviceClient.findItemsAdvanced(
				request);

			searchResults = result.getSearchResult();
		}

		List<SearchItem> searchItems = searchResults.getItem();

		return createSearchResults(
			searchItems, searchQueryModel.getSearchQueryId());
	}

	private static SearchResultModel createSearchResult(SearchItem item) {
		SearchResultModel searchResultModel = new SearchResultModel();

		ListingInfo listingInfo = item.getListingInfo();

		searchResultModel.setItemId(item.getItemId());
		searchResultModel.setItemTitle(item.getTitle());
		searchResultModel.setItemURL(
			_EBAY_URL_PREFIX + searchResultModel.getItemId());
		searchResultModel.setGalleryURL(item.getGalleryURL());

		Calendar endTimeCalendar = listingInfo.getEndTime();

		searchResultModel.setEndingTime(endTimeCalendar.getTime());

		String typeOfAuction = listingInfo.getListingType();

		searchResultModel.setTypeOfAuction(typeOfAuction);

		setPrice(
			searchResultModel, listingInfo, item.getSellingStatus(),
			typeOfAuction);

		return searchResultModel;
	}

	private static List<SearchResultModel> createSearchResults(
		List<SearchItem> searchItems, int searchQueryId) {

		List<SearchResultModel> searchResultModels = new ArrayList<>();

		Collections.reverse(searchItems);

		for (SearchItem searchItem : searchItems) {
			SearchResultModel searchResultModel = createSearchResult(
				searchItem);

			searchResultModel.setSearchQueryId(searchQueryId);

			searchResultModels.add(searchResultModel);
		}

		return searchResultModels;
	}

	private static void setPrice(
		SearchResultModel searchResultModel, ListingInfo listingInfo,
		SellingStatus sellingStatus, String typeOfAuction) {

		if ("Auction".equals(typeOfAuction)) {
			Amount currentPrice = sellingStatus.getCurrentPrice();

			searchResultModel.setAuctionPrice(currentPrice.getValue());
		}
		else if ("FixedPrice".equals(typeOfAuction) ||
				 "StoreInventory".equals(typeOfAuction)) {

			Amount currentPrice = sellingStatus.getCurrentPrice();

			searchResultModel.setFixedPrice(currentPrice.getValue());
		}
		else if ("AuctionWithBIN".equals(typeOfAuction)) {
			Amount currentPrice = sellingStatus.getCurrentPrice();
			Amount buyItNowPrice = listingInfo.getBuyItNowPrice();

			searchResultModel.setAuctionPrice(currentPrice.getValue());
			searchResultModel.setFixedPrice(buyItNowPrice.getValue());
		}
		else {
			_log.error("Unknown type of auction: {}", typeOfAuction);
		}
	}

	private static FindItemsAdvancedRequest setUpAdvancedRequest(
		String searchQuery, String categoryId) {

		_log.info("Setting up advanced request with category");

		FindItemsAdvancedRequest request = new FindItemsAdvancedRequest();

		request.setKeywords(searchQuery);
		request.getCategoryId().add(categoryId);

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS);

		request.setPaginationInput(paginationInput);
		request.setSortOrder(SortOrderType.START_TIME_NEWEST);

		return request;
	}

	private static FindItemsByKeywordsRequest setUpRequest(String searchQuery) {
		_log.info("Setting up request without category");

		FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();

		request.setKeywords(searchQuery);

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