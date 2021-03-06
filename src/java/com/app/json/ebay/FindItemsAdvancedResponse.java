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

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class FindItemsAdvancedResponse {

	@SerializedName("errorMessage")
	private List<ErrorMessage> errorMessage = new ArrayList<>();

	@SerializedName("searchResult")
	private List<JsonSearchResult> jsonSearchResult = new ArrayList<>();

	public ErrorMessage getErrorMessage() {
		if (errorMessage.isEmpty()) {
			return null;
		}

		return errorMessage.get(0);
	}

	public JsonSearchResult getJsonSearchResult() {
		return jsonSearchResult.get(0);
	}

}
