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

package com.app.test.model;

import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResult;
import com.app.util.PropertiesKeys;
import com.app.util.PropertiesUtil;
import com.app.util.eBayAPIUtil;

import java.io.IOException;

import java.net.URL;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultTest {

	@Before
	public void setUp() throws IOException {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		eBayAPIUtil.loadeBayServiceClient(
			System.getProperty(PropertiesKeys.APPLICATION_ID));
	}

	@Test
	public void testGeteBaySearchResults() throws Exception {
		SearchQueryModel searchQueryModel = new SearchQueryModel(1, "eBay");

		List<SearchResultModel> eBaySearchResults =
			eBaySearchResult.geteBaySearchResults(searchQueryModel);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

}