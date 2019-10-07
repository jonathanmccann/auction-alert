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

package com.app.json.sendgrid;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SendGridBounceJsonResponse {

	public SendGridBounceJsonResponse(
		List<Integer> searchResultIds, String type) {

		_searchResultIds = searchResultIds;
		_type = type;
	}

	public List<Integer> getSearchResultIds() {
		return _searchResultIds;
	}

	public String getType() {
		return _type;
	}

	@SerializedName("searchResultIds")
	private List<Integer> _searchResultIds;

	@SerializedName("type")
	private String _type;

}