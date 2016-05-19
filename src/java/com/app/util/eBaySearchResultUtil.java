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
import com.ebay.services.finding.ItemFilter;
import com.ebay.services.finding.ItemFilterType;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SellingStatus;
import com.ebay.services.finding.SortOrderType;

import java.text.DecimalFormat;

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

		FindItemsAdvancedRequest request = setUpAdvancedRequest(searchQuery);

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
		SearchQuery searchQuery) {

		_log.info("Setting up advanced request");

		FindItemsAdvancedRequest request = new FindItemsAdvancedRequest();

		request.setKeywords(searchQuery.getKeywords());

		if (ValidatorUtil.isNotNull(searchQuery.getCategoryId())) {
			request.getCategoryId().add(searchQuery.getCategoryId());
		}

		if (searchQuery.isSearchDescription()) {
			request.setDescriptionSearch(true);
		}

		if (searchQuery.isFreeShippingOnly()) {
			ItemFilter freeShipping = new ItemFilter();

			freeShipping.setName(ItemFilterType.FREE_SHIPPING_ONLY);
			freeShipping.getValue().add("true");

			request.getItemFilter().add(freeShipping);
		}

		if (!searchQuery.isNewCondition() || !searchQuery.isUsedCondition() ||
			!searchQuery.isUnspecifiedCondition()) {

			if (searchQuery.isNewCondition()) {
				ItemFilter newCondition = new ItemFilter();
				newCondition.setName(ItemFilterType.CONDITION);
				newCondition.getValue().add("New");
				request.getItemFilter().add(newCondition);
			}

			if (searchQuery.isUsedCondition()) {
				ItemFilter usedCondition = new ItemFilter();
				usedCondition.setName(ItemFilterType.CONDITION);
				usedCondition.getValue().add("Used");
				request.getItemFilter().add(usedCondition);
			}

			if (searchQuery.isUnspecifiedCondition()) {
				ItemFilter unspecifiedCondition = new ItemFilter();
				unspecifiedCondition.setName(ItemFilterType.CONDITION);
				unspecifiedCondition.getValue().add("Unspecified");
				request.getItemFilter().add(unspecifiedCondition);
			}
		}

		if (!searchQuery.isAuctionListing() ||
			!searchQuery.isFixedPriceListing()) {

			ItemFilter listingType = new ItemFilter();
			listingType.setName(ItemFilterType.LISTING_TYPE);
			listingType.getValue().add("AuctionWithBIN");

			if (searchQuery.isAuctionListing()) {
				listingType.getValue().add("Auction");
			}
			else {
				listingType.getValue().add("FixedPrice");
			}

			request.getItemFilter().add(listingType);
		}

		if (searchQuery.getMinPrice() > 0) {
			ItemFilter minPrice = new ItemFilter();
			minPrice.setName(ItemFilterType.MIN_PRICE);
			minPrice.getValue().add(
				_DECIMAL_FORMAT.format(searchQuery.getMinPrice()));
			request.getItemFilter().add(minPrice);
		}

		if (searchQuery.getMaxPrice() > 0) {
			ItemFilter maxPrice = new ItemFilter();
			maxPrice.setName(ItemFilterType.MAX_PRICE);
			maxPrice.getValue().add(
				_DECIMAL_FORMAT.format(searchQuery.getMaxPrice()));
			request.getItemFilter().add(maxPrice);
		}

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(
			PropertiesValues.NUMBER_OF_SEARCH_RESULTS);

		request.setPaginationInput(paginationInput);
		request.setSortOrder(SortOrderType.START_TIME_NEWEST);

		return request;
	}

	private static final DecimalFormat _DECIMAL_FORMAT = new DecimalFormat(
		"0.00");

	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearchResultUtil.class);

}