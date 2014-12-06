package com.app.model;

import java.util.Date;

/**
 * @author Jonathan McCann
 */
public class SearchResultModel {

	public SearchResultModel() {
	}

	public SearchResultModel(
		String itemId, String itemTitle, double auctionPrice, double fixedPrice,
		String itemURL, Date endingTime, String typeOfAuction) {

		_itemId = itemId;
		_itemTitle = itemTitle;
		_auctionPrice = auctionPrice;
		_fixedPrice = fixedPrice;
		_itemURL = itemURL;
		_endingTime = endingTime;
		_typeOfAuction = typeOfAuction;
	}

	public double getAuctionPrice() {
		return _auctionPrice;
	}

	public Date getEndingTime() {
		return _endingTime;
	}

	public double getFixedPrice() {
		return _fixedPrice;
	}

	public String getItemId() {
		return _itemId;
	}

	public String getItemTitle() {
		return _itemTitle;
	}

	public String getItemURL() {
		return _itemURL;
	}

	public int getSearchResultId() {
		return _searchResultId;
	}

	public String getTypeOfAuction() {
		return _typeOfAuction;
	}

	public void setAuctionPrice(double auctionPrice) {
		_auctionPrice = auctionPrice;
	}

	public void setEndingTime(Date itemEndingTime) {
		_endingTime = itemEndingTime;
	}

	public void setFixedPrice(double fixedPrice) {
		_fixedPrice = fixedPrice;
	}

	public void setItemId(String itemId) {
		_itemId = itemId;
	}

	public void setItemTitle(String itemTitle) {
		_itemTitle = itemTitle;
	}

	public void setItemURL(String itemURL) {
		_itemURL = itemURL;
	}

	public void setSearchResultId(int searchResultId) {
		_searchResultId = searchResultId;
	}

	public void setTypeOfAuction(String typeOfAuction) {
		_typeOfAuction = typeOfAuction;
	}

	private double _auctionPrice;
	private Date _endingTime;
	private double _fixedPrice;
	private String _itemId;
	private String _itemTitle;
	private String _itemURL;
	private int _searchResultId;
	private String _typeOfAuction;

}