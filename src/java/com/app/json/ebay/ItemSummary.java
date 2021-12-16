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

package com.app.json.ebay;

import java.util.List;

public class ItemSummary {

	public String getTitle() {
		return title;
	}

	public Image getImage() {
		return image;
	}

	public Price getPrice() {
		return price;
	}

	public List<String> getBuyingOptions() {
		return buyingOptions;
	}

	public CurrentBidPrice getCurrentBidPrice() {
		return currentBidPrice;
	}

	public String getLegacyItemId() {
		return legacyItemId;
	}

	private String title;
	private Image image;
	private Price price;
	private List<String> buyingOptions = null;
	private CurrentBidPrice currentBidPrice;
	private String legacyItemId;

}