package com.app.model;

import com.app.util.eBayAPIUtil;

import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SellingStatus;
import com.ebay.services.finding.SortOrderType;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultModel extends SearchResultModel {

	public static List<SearchResultModel> geteBaySearchResults(
		List<String> searchQueries) {

		_log.info(
			"Getting eBay search results for {} search queries",
			searchQueries.size());

		if (_log.isDebugEnabled()) {
			for (String searchQuery : searchQueries) {
				_log.debug("Searching for {}", searchQuery);
			}
		}

		List<SearchResultModel> searchResultModels =
			new ArrayList<SearchResultModel>();

		for (String searchQuery : searchQueries) {
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

			List<SearchItem> items = result.getSearchResult().getItem();

			for (SearchItem item : items) {
				SearchResultModel searchResultModel = new SearchResultModel();

				ListingInfo listingInfo = item.getListingInfo();

				searchResultModel.setItemId(item.getItemId());
				searchResultModel.setItemTitle(item.getTitle());
				searchResultModel.setTypeOfAuction(
					listingInfo.getListingType());
				searchResultModel.setItemURL(
					_EBAY_URL_PREFIX + searchResultModel.getItemId());
				searchResultModel.setEndingTime(
					listingInfo.getEndTime().getTime());

				SellingStatus sellingStatus = item.getSellingStatus();

				if (searchResultModel.getTypeOfAuction().contains("Auction")) {
					searchResultModel.setAuctionPrice(
						sellingStatus.getCurrentPrice().getValue());
				}

				if (item.getListingInfo().isBuyItNowAvailable()) {
					searchResultModel.setFixedPrice(
						listingInfo.getBuyItNowPrice().getValue());
				}

				searchResultModels.add(searchResultModel);
			}
		}

		return searchResultModels;
	}

	private static final String _EBAY_URL_PREFIX = "http://www.ebay.com/itm/";

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearchResultModel.class);

}