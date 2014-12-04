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

public class eBaySearchResultModel extends SearchResultModel {

	public static List<SearchResultModel> geteBaySearchResults(List<String> searchQueries) {
		List<SearchResultModel> searchResultModels = new ArrayList<SearchResultModel>();

        try {
			for (String searchQuery : searchQueries) {
				FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();

				request.setKeywords(searchQuery);

				PaginationInput paginationInput = new PaginationInput();
				paginationInput.setEntriesPerPage(5);

				request.setPaginationInput(paginationInput);
				request.setSortOrder(SortOrderType.START_TIME_NEWEST);

				FindingServicePortType serviceClient = eBayAPIUtil.getServiceClient();

				FindItemsByKeywordsResponse result = serviceClient.findItemsByKeywords(request);

				List<SearchItem> items = result.getSearchResult().getItem();

				for (SearchItem item : items) {
					SearchResultModel searchResultModel = new SearchResultModel();

					ListingInfo listingInfo = item.getListingInfo();

					// ID
					searchResultModel.setItemId(item.getItemId());

					// Title
					searchResultModel.setItemTitle(item.getTitle());

					// Type of Auction (Auction, AuctionWithBIN, FixedPrice, StoreInventory
					searchResultModel.setTypeOfAuction(listingInfo.getListingType());

					// Price
					SellingStatus sellingStatus = item.getSellingStatus();

					if (searchResultModel.getTypeOfAuction().contains("Auction")) {
						searchResultModel.setItemAuctionPrice(
							sellingStatus.getCurrentPrice().getValue());
					}

					if (item.getListingInfo().isBuyItNowAvailable()) {
						searchResultModel.setItemFixedPrice(
							listingInfo.getBuyItNowPrice().getValue());
					}

					// URL
					searchResultModel.setItemURL(
						_ebayURLPrefix + searchResultModel.getItemId());

					// Ending time
					searchResultModel.setItemEndingTime(
						listingInfo.getEndTime().getTime());

					searchResultModels.add(searchResultModel);
				}
			}
        }
		catch (Exception e) {
            e.printStackTrace();
        }

		return searchResultModels;
    }

	private static final String _ebayURLPrefix = "http://www.ebay.com/itm/";
}