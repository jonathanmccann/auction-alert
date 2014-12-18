package com.app.model;

import com.app.util.PropertiesUtil;
import com.app.util.eBayAPIUtil;

import com.ebay.services.finding.Amount;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SearchResult;
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
public class eBaySearchResult {

	public static List<SearchResultModel> geteBaySearchResults(
		SearchQueryModel searchQueryModel) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Searching for {}", searchQueryModel.getSearchQuery());
		}

		List<SearchResultModel> searchResultModels = new ArrayList<>();

		FindItemsByKeywordsRequest request = setUpRequest(
			searchQueryModel.getSearchQuery());

		FindingServicePortType serviceClient =
			eBayAPIUtil.getServiceClient();

		FindItemsByKeywordsResponse result =
			serviceClient.findItemsByKeywords(request);

		SearchResult searchResults = result.getSearchResult();

		List<SearchItem> items = searchResults.getItem();

		Collections.reverse(items);

		for (SearchItem item : items) {
			SearchResultModel searchResultModel = createSearchResult(item);

			searchResultModel.setSearchQueryId(
				searchQueryModel.getSearchQueryId());

			searchResultModels.add(searchResultModel);
		}

		return searchResultModels;
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

		searchResultModel.setTypeOfAuction(
			typeOfAuction);

		setPrice(
			searchResultModel, listingInfo, item.getSellingStatus(),
			typeOfAuction);

		return searchResultModel;
	}

	private static void setPrice(
		SearchResultModel searchResultModel, ListingInfo listingInfo,
		SellingStatus sellingStatus, String typeOfAuction) {

		DecimalFormat decimalFormat = new DecimalFormat("0.00");

		if ("Auction".equals(typeOfAuction)) {
			Amount currentPrice = sellingStatus.getCurrentPrice();

			String auctionPrice = decimalFormat.format(currentPrice.getValue());

			searchResultModel.setAuctionPrice(Double.valueOf(auctionPrice));
		}
		else if ("FixedPrice".equals(typeOfAuction) ||
			"StoreInventory".equals(typeOfAuction)) {

			Amount currentPrice = sellingStatus.getCurrentPrice();

			String fixedPrice = decimalFormat.format(currentPrice.getValue());

			searchResultModel.setFixedPrice(Double.valueOf(fixedPrice));
		}
		else if ("AuctionWithBIN".equals(typeOfAuction)) {
			Amount currentPrice = sellingStatus.getCurrentPrice();
			Amount buyItNowPrice = listingInfo.getBuyItNowPrice();

			String auctionPrice = decimalFormat.format(currentPrice.getValue());
			String fixedPrice = decimalFormat.format(buyItNowPrice.getValue());

			searchResultModel.setAuctionPrice(Double.valueOf(auctionPrice));
			searchResultModel.setFixedPrice(Double.valueOf(fixedPrice));
		}
		else {
			_log.error("Unknown type of auction: {}", typeOfAuction);
		}
	}

	private static FindItemsByKeywordsRequest setUpRequest(String searchQuery) {
		FindItemsByKeywordsRequest request =
			new FindItemsByKeywordsRequest();

		request.setKeywords(searchQuery);

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(_NUMBER_OF_SEARCH_RESULTS);

		request.setPaginationInput(paginationInput);
		request.setSortOrder(SortOrderType.START_TIME_NEWEST);

		return request;
	}

	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";

	private static final int _NUMBER_OF_SEARCH_RESULTS =
		Integer.valueOf(PropertiesUtil.getConfigurationProperty(
			PropertiesUtil.NUMBER_OF_SEARCH_RESULTS));

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearchResult.class);

}