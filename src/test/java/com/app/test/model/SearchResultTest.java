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

import com.app.model.SearchResult;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SearchResultTest {

	@Before
	public void setUp() {
		_searchResult = new SearchResult();
	}

	@Test
	public void testConstructor() throws Exception {
		Date endingTime = new Date();

		SearchResult searchResult = new SearchResult(
			1, "1234", "itemTitle", 14.99, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			endingTime, "Buy It Now");

		Assert.assertEquals(1, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("itemTitle", searchResult.getItemTitle());
		Assert.assertEquals(14.99, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertEquals(endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Buy It Now", searchResult.getTypeOfAuction());
	}

	@Test
	public void testSetAndGetAuctionPrice() throws Exception {
		_searchResult.setAuctionPrice(14.99);

		Assert.assertEquals(14.99, _searchResult.getAuctionPrice(), 0);
	}

	@Test
	public void testSetAndGetFixedPrice() throws Exception {
		_searchResult.setFixedPrice(14.99);

		Assert.assertEquals(14.99, _searchResult.getFixedPrice(), 0);
	}

	@Test
	public void testSetAndGetGalleryURL() throws Exception {
		_searchResult.setGalleryURL("http://www.ebay.com/123.jpg");

		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", _searchResult.getGalleryURL());
	}

	@Test
	public void testSetAndGetItemEndingTime() throws Exception {
		Date endingTime = new Date();

		_searchResult.setEndingTime(endingTime);

		Assert.assertEquals(endingTime, _searchResult.getEndingTime());
	}

	@Test
	public void testSetAndGetItemId() throws Exception {
		_searchResult.setItemId("1234");

		Assert.assertEquals("1234", _searchResult.getItemId());
	}

	@Test
	public void testSetAndGetItemTitle() throws Exception {
		_searchResult.setItemTitle("itemTitle");

		Assert.assertEquals("itemTitle", _searchResult.getItemTitle());
	}

	@Test
	public void testSetAndGetItemURL() throws Exception {
		_searchResult.setItemURL("http://www.ebay.com/itm/1234");

		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", _searchResult.getItemURL());
	}

	@Test
	public void testSetAndGetTypeOfAuction() throws Exception {
		_searchResult.setTypeOfAuction("Buy It Now");

		Assert.assertEquals(
			"Buy It Now", _searchResult.getTypeOfAuction());
	}

	private SearchResult _searchResult;

}