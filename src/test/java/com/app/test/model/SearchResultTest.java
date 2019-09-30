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
	public void testEqualsWithEqualObject() {
		_searchResult.setItemId("1234");

		SearchResult searchResult = new SearchResult();

		searchResult.setItemId("1234");

		Assert.assertTrue(_searchResult.equals(searchResult));
	}

	@Test
	public void testEqualsWithInequalItemId() {
		_searchResult.setItemId("1234");

		SearchResult searchResult = new SearchResult();

		searchResult.setItemId("2345");

		Assert.assertFalse(_searchResult.equals(searchResult));
	}

	@Test
	public void testEqualsWithInequalObject() {
		Assert.assertFalse(_searchResult.equals(new Object()));
	}

	@Test
	public void testEqualsWithNullObject() {
		Assert.assertFalse(_searchResult.equals(null));
	}

	@Test
	public void testHashCode() {
		_searchResult.setItemId("1234");

		Assert.assertEquals(1234, _searchResult.hashCode());
	}

	@Test
	public void testSetAndGetDelivered() {
		_searchResult.setDelivered(false);

		Assert.assertFalse(_searchResult.isDelivered());
	}

	@Test
	public void testSetAndGetAuctionPrice() {
		_searchResult.setAuctionPrice("$14.99");

		Assert.assertEquals("$14.99", _searchResult.getAuctionPrice());
	}

	@Test
	public void testSetAndGetFixedPrice() {
		_searchResult.setFixedPrice("$14.99");

		Assert.assertEquals("$14.99", _searchResult.getFixedPrice());
	}

	@Test
	public void testSetAndGetGalleryURL() {
		_searchResult.setGalleryURL("http://www.ebay.com/123.jpg");

		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", _searchResult.getGalleryURL());
	}

	@Test
	public void testSetAndGetItemId() {
		_searchResult.setItemId("1234");

		Assert.assertEquals("1234", _searchResult.getItemId());
	}

	@Test
	public void testSetAndGetItemTitle() {
		_searchResult.setItemTitle("itemTitle");

		Assert.assertEquals("itemTitle", _searchResult.getItemTitle());
	}

	@Test
	public void testSetAndGetItemURL() {
		_searchResult.setItemURL("http://www.ebay.com/itm/1234");

		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", _searchResult.getItemURL());
	}

	@Test
	public void testSetAndGetSearchQueryId() {
		_searchResult.setSearchQueryId(1);

		Assert.assertEquals(1, _searchResult.getSearchQueryId());
	}

	@Test
	public void testSetAndGetSearchResultId() {
		_searchResult.setSearchResultId(1);

		Assert.assertEquals(1, _searchResult.getSearchResultId());
	}

	@Test
	public void testSetAndGetUserId() {
		_searchResult.setUserId(1);

		Assert.assertEquals(1, _searchResult.getUserId());
	}

	private SearchResult _searchResult;

}