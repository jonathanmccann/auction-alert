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

package com.app.json;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListingInfo {

	@SerializedName("buyItNowPrice")
	private List<BuyItNowPrice> buyItNowPrice = null;

	@SerializedName("listingType")
	private List<String> listingType = null;

	public BuyItNowPrice getBuyItNowPrice() {
		return buyItNowPrice.get(0);
	}

	public String getListingType() {
		return listingType.get(0);
	}

}
