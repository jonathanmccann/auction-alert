package com.app.model;

import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.ListingInfo;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SellingStatus;
import com.ebay.services.finding.SortOrderType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class eBaySearchResultModel extends SearchResultModel {
	public eBaySearchResultModel() {
		Properties properties = new Properties();

		String propertiesFilePath =
			System.getProperty("catalina.base") + "/" + "config.properties";

		try {
			InputStream inputStream = new FileInputStream(propertiesFilePath);

			if (inputStream == null) {
				throw new FileNotFoundException();
			}

			properties.load(inputStream);
		}
		catch (IOException ioe) {
			System.out.println(
				"Cannot find or load properties file: " + propertiesFilePath);
		}

		ClientConfig config = new ClientConfig();

		config.setApplicationId(properties.getProperty("application.id"));

		_serviceClient = FindingServiceClientFactory.getServiceClient(config);
	}

	public List<SearchResultModel> geteBaySearchResults(List<String> searchQueries) {
		List<SearchResultModel> searchResultModels = new ArrayList<SearchResultModel>();

        try {
			for (String searchQuery : searchQueries) {
				FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();

				request.setKeywords(searchQuery);

				PaginationInput paginationInput = new PaginationInput();
				paginationInput.setEntriesPerPage(5);

				request.setPaginationInput(paginationInput);
				request.setSortOrder(SortOrderType.START_TIME_NEWEST);

				FindItemsByKeywordsResponse result = _serviceClient.findItemsByKeywords(request);

				System.out.println("Acknowledgement = " + result.getAck());
				System.out.println("Found " + result.getSearchResult().getCount() + " items.");

				List<SearchItem> items = result.getSearchResult().getItem();

				for (SearchItem item : items) {
					SearchResultModel searchResultModel = new SearchResultModel();

					ListingInfo listingInfo = item.getListingInfo();

					// ID
					System.out.println("item.getItemId() = " + item.getItemId());
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

	private static FindingServicePortType _serviceClient;
}