package com.app.model;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultModel extends SearchResultModel {

	public static List<SearchResultModel> geteBaySearchResults(
		String searchQuery) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Searching for {}", searchQuery);
		}

		List<SearchResultModel> searchResultModels =
			new ArrayList<SearchResultModel>();

		FindItemsByKeywordsRequest request =
			new FindItemsByKeywordsRequest();

		request.setKeywords(searchQuery);

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(5);

		request.setPaginationInput(paginationInput);
		request.setSortOrder(SortOrderType.START_TIME_NEWEST);

		FindingServicePortType serviceClient =
			eBayAPIUtil.getServiceClient();

		FindItemsByKeywordsResponse result =
			serviceClient.findItemsByKeywords(request);

		SearchResult searchResults = result.getSearchResult();

		List<SearchItem> items = searchResults.getItem();

		for (SearchItem item : items) {
			SearchResultModel searchResultModel = new SearchResultModel();

			ListingInfo listingInfo = item.getListingInfo();

			searchResultModel.setItemId(item.getItemId());
			searchResultModel.setItemTitle(item.getTitle());
			searchResultModel.setItemURL(
				_EBAY_URL_PREFIX + searchResultModel.getItemId());

			Calendar endTimeCalendar = listingInfo.getEndTime();

			searchResultModel.setEndingTime(endTimeCalendar.getTime());

			String typeOfAuction = listingInfo.getListingType();

			searchResultModel.setTypeOfAuction(
				typeOfAuction);

			SellingStatus sellingStatus = item.getSellingStatus();

			if (typeOfAuction.equals("Auction")) {
				Amount currentPrice = sellingStatus.getCurrentPrice();

				searchResultModel.setAuctionPrice(currentPrice.getValue());
			}
			else if (typeOfAuction.equals("FixedPrice") ||
				typeOfAuction.equals("StoreInventory")) {

				Amount currentPrice = sellingStatus.getCurrentPrice();

				searchResultModel.setFixedPrice(currentPrice.getValue());
			}
			else if (typeOfAuction.equals("AuctionWithBIN")) {
				Amount currentPrice = sellingStatus.getCurrentPrice();
				Amount buyItNowPrice = listingInfo.getBuyItNowPrice();

				searchResultModel.setAuctionPrice(currentPrice.getValue());

				searchResultModel.setFixedPrice(buyItNowPrice.getValue());
			}
			else {
				_log.error("Unknown type of auction: {}", typeOfAuction);
			}

			searchResultModels.add(searchResultModel);
		}

		return searchResultModels;
	}

	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearchResultModel.class);

}