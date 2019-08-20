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

public class Item {

	@SerializedName("itemId")
	private List<String> itemId = null;

	@SerializedName("title")
	private List<String> title = null;

	@SerializedName("galleryURL")
	private List<String> galleryURL = null;

	@SerializedName("sellingStatus")
	private List<SellingStatus> sellingStatus = null;

	@SerializedName("listingInfo")
	private List<ListingInfo> listingInfo = null;

	public String getItemId() {
		return itemId.get(0);
	}

	public String getTitle() {
		return title.get(0);
	}

	public String getGalleryURL() {
		return galleryURL.get(0);
	}

	public SellingStatus getSellingStatus() {
		return sellingStatus.get(0);
	}

	public ListingInfo getListingInfo() {
		return listingInfo.get(0);
	}

}
