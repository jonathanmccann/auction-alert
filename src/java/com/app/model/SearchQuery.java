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

package com.app.model;

/**
 * @author Jonathan McCann
 */
public class SearchQuery {

	public SearchQuery() {
	}

	public SearchQuery(int searchQueryId, String keywords) {
		_searchQueryId = searchQueryId;
		_keywords = keywords;
	}

	public SearchQuery(int searchQueryId, String keywords, String categoryId) {
		_searchQueryId = searchQueryId;
		_keywords = keywords;
		_categoryId = categoryId;
	}

	public SearchQuery(
		int searchQueryId, String keywords, String categoryId,
		boolean searchDescription, boolean freeShippingOnly,
		boolean newCondition, boolean usedCondition,
		boolean unspecifiedCondition, boolean auctionListing,
		boolean fixedPriceListing, double minPrice, double maxPrice) {

		_auctionListing = auctionListing;
		_categoryId = categoryId;
		_fixedPriceListing = fixedPriceListing;
		_freeShippingOnly = freeShippingOnly;
		_keywords = keywords;
		_maxPrice = maxPrice;
		_minPrice = minPrice;
		_newCondition = newCondition;
		_searchDescription = searchDescription;
		_searchQueryId = searchQueryId;
		_unspecifiedCondition = unspecifiedCondition;
		_usedCondition = usedCondition;
	}

	public String getCategoryId() {
		return _categoryId;
	}

	public String getKeywords() {
		return _keywords;
	}

	public double getMaxPrice() {
		return _maxPrice;
	}

	public double getMinPrice() {
		return _minPrice;
	}

	public int getSearchQueryId() {
		return _searchQueryId;
	}

	public boolean isAuctionListing() {
		return _auctionListing;
	}

	public boolean isFixedPriceListing() {
		return _fixedPriceListing;
	}

	public boolean isFreeShippingOnly() {
		return _freeShippingOnly;
	}

	public boolean isNewCondition() {
		return _newCondition;
	}

	public boolean isSearchDescription() {
		return _searchDescription;
	}

	public boolean isUnspecifiedCondition() {
		return _unspecifiedCondition;
	}

	public boolean isUsedCondition() {
		return _usedCondition;
	}

	public void setAuctionListing(boolean auctionListing) {
		this._auctionListing = auctionListing;
	}

	public void setCategoryId(String categoryId) {
		_categoryId = categoryId;
	}

	public void setFixedPriceListing(boolean fixedPriceListing) {
		this._fixedPriceListing = fixedPriceListing;
	}

	public void setFreeShippingOnly(boolean freeShippingOnly) {
		this._freeShippingOnly = freeShippingOnly;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setMaxPrice(double maxPrice) {
		this._maxPrice = maxPrice;
	}

	public void setMinPrice(double minPrice) {
		this._minPrice = minPrice;
	}

	public void setNewCondition(boolean newCondition) {
		this._newCondition = newCondition;
	}

	public void setSearchDescription(boolean searchDescription) {
		this._searchDescription = searchDescription;
	}

	public void setSearchQueryId(int searchQueryId) {
		_searchQueryId = searchQueryId;
	}

	public void setUnspecifiedCondition(boolean unspecifiedCondition) {
		this._unspecifiedCondition = unspecifiedCondition;
	}

	public void setUsedCondition(boolean usedCondition) {
		this._usedCondition = usedCondition;
	}

	private boolean _auctionListing;
	private String _categoryId;
	private boolean _fixedPriceListing;
	private boolean _freeShippingOnly;
	private String _keywords;
	private double _maxPrice;
	private double _minPrice;
	private boolean _newCondition;
	private boolean _searchDescription;
	private int _searchQueryId;
	private boolean _unspecifiedCondition;
	private boolean _usedCondition;

}