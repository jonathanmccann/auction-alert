package com.app.model;

import java.util.Date;
import java.util.Map;

public class SearchResultModel {
	public SearchResultModel() {
	}

	public SearchResultModel(String itemId, String itemTitle, String itemDetails,
		double itemAuctionPrice, double itemFixedPrice, String itemURL,
		Date itemEndingTime, String typeOfAuction) {

		this._itemId = itemId;
		this._itemTitle = itemTitle;
		this._itemDetails = itemDetails;
		this._itemAuctionPrice = itemAuctionPrice;
		this._itemFixedPrice = itemFixedPrice;
		this._itemURL = itemURL;
		this._itemEndingTime = itemEndingTime;
		this._typeOfAuction = typeOfAuction;
	}

	public String getItemId() {
		return _itemId;
	}

	public void setItemId(String itemId) {
		this._itemId = itemId;
	}

	public String getItemTitle() {
		return _itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this._itemTitle = itemTitle;
	}

	public String getItemDetails() {
		return _itemDetails;
	}

	public void setItemDetails(String itemDetails) {
		this._itemDetails = itemDetails;
	}

	public double getItemAuctionPrice() {
		return _itemAuctionPrice;
	}

	public void setItemAuctionPrice(double itemPrice) {
		this._itemAuctionPrice = itemPrice;
	}

	public double getItemFixedPrice() {
		return _itemFixedPrice;
	}

	public void setItemFixedPrice(double itemPrice) {
		this._itemFixedPrice = itemPrice;
	}

	public String getItemURL() {
		return _itemURL;
	}

	public void setItemURL(String itemURL) {
		this._itemURL = itemURL;
	}

	public Date getItemEndingTime() {
		return _itemEndingTime;
	}

	public void setItemEndingTime(Date itemEndingTime) {
		this._itemEndingTime = itemEndingTime;
	}

	public String getTypeOfAuction() {
		return _typeOfAuction;
	}

	public void setTypeOfAuction(String typeOfAuction) {
		this._typeOfAuction = typeOfAuction;
	}

	private String _itemId;
	private String _itemTitle;
	private String _itemDetails;
	private double _itemAuctionPrice;
	private double _itemFixedPrice;
	private String _itemURL;
	private Map<String, String> _sellerInformation;
	private Date _itemEndingTime;
	private String _typeOfAuction;
}