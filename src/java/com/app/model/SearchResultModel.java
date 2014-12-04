package com.app.model;

import java.util.Date;

/**
 * @author Jonathan McCann
 */
public class SearchResultModel {

	public SearchResultModel() {
	}

	public SearchResultModel(
		String itemId, String itemTitle, String itemDetails,
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

	public double getItemAuctionPrice() {
		return _itemAuctionPrice;
	}

	public String getItemDetails() {
		return _itemDetails;
	}

	public Date getItemEndingTime() {
		return _itemEndingTime;
	}

	public double getItemFixedPrice() {
		return _itemFixedPrice;
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

	public String getTypeOfAuction() {
		return _typeOfAuction;
	}

	public void setItemAuctionPrice(double itemPrice) {
		this._itemAuctionPrice = itemPrice;
	}

	public void setItemDetails(String itemDetails) {
		this._itemDetails = itemDetails;
	}

	public void setItemEndingTime(Date itemEndingTime) {
		this._itemEndingTime = itemEndingTime;
	}

	public void setItemFixedPrice(double itemPrice) {
		this._itemFixedPrice = itemPrice;
	}

	public void setItemId(String itemId) {
		this._itemId = itemId;
	}

	public void setItemTitle(String itemTitle) {
		this._itemTitle = itemTitle;
	}

	public void setItemURL(String itemURL) {
		this._itemURL = itemURL;
	}

	public void setTypeOfAuction(String typeOfAuction) {
		this._typeOfAuction = typeOfAuction;
	}

	private double _itemAuctionPrice;
	private String _itemDetails;
	private Date _itemEndingTime;
	private double _itemFixedPrice;
	private String _itemId;
	private String _itemTitle;
	private String _itemURL;
	private String _typeOfAuction;

}