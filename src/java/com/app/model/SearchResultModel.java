package com.app.model;

import java.util.Date;
import java.util.Map;

public class SearchResultModel {
	public SearchResultModel() {
	}

	public SearchResultModel(String itemTitle, String itemDetails,
		String itemPrice, String itemURL,
		Map<String, String> sellerInformation, Date itemEndingTime,
		String typeOfAuction) {

		this._itemTitle = itemTitle;
		this._itemDetails = itemDetails;
		this._itemPrice = itemPrice;
		this._itemURL = itemURL;
		this._sellerInformation = sellerInformation;
		this._itemEndingTime = itemEndingTime;
		this._typeOfAuction = typeOfAuction;
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

	public String getItemPrice() {
		return _itemPrice;
	}

	public void setItemPrice(String itemPrice) {
		this._itemPrice = itemPrice;
	}

	public String getItemURL() {
		return _itemURL;
	}

	public void setItemURL(String itemURL) {
		this._itemURL = itemURL;
	}

	public Map<String, String> getSellerInformation() {
		return _sellerInformation;
	}

	public void setSellerInformation(Map<String, String> sellerInformation) {
		this._sellerInformation = sellerInformation;
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

	private String _itemTitle;
	private String _itemDetails;
	private String _itemPrice;
	private String _itemURL;
	private Map<String, String> _sellerInformation;
	private Date _itemEndingTime;
	private String _typeOfAuction;
}