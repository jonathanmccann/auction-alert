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

import com.app.model.SearchQuery;
import com.app.util.ValidatorUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SearchQueryTest {

	@Before
	public void setUp() {
		_searchQuery = new SearchQuery();
	}

	@Test
	public void testAdvancedConstructor() {
		SearchQuery searchQuery = new SearchQuery(
			1, "Test keywords", "100", true, true, true, true, true, true,
			true, 5.00, 10.00);

		Assert.assertEquals(1, searchQuery.getSearchQueryId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
	}

	@Test
	public void testConstructor() {
		Assert.assertEquals(0, _searchQuery.getSearchQueryId());
		Assert.assertTrue(ValidatorUtil.isNull(_searchQuery.getKeywords()));
		Assert.assertTrue(ValidatorUtil.isNull(_searchQuery.getCategoryId()));
	}

	@Test
	public void testConstructorWithKeywords() {
		SearchQuery searchQuery = new SearchQuery(1, "Test keywords");

		Assert.assertEquals(1, searchQuery.getSearchQueryId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
	}

	@Test
	public void testConstructorWithKeywordsAndCategoryId() {
		SearchQuery searchQuery = new SearchQuery(1, "Test keywords", "100");

		Assert.assertEquals(1, searchQuery.getSearchQueryId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
	}

	@Test
	public void testIsAuctionListing() {
		_searchQuery.setAuctionListing(true);

		Assert.assertTrue(_searchQuery.isAuctionListing());
	}

	@Test
	public void testIsFixedPriceListing() {
		_searchQuery.setFixedPriceListing(true);

		Assert.assertTrue(_searchQuery.isFixedPriceListing());
	}

	@Test
	public void testIsFreeShippingOnly() {
		_searchQuery.setFreeShippingOnly(true);

		Assert.assertTrue(_searchQuery.isFreeShippingOnly());
	}

	@Test
	public void testIsNewCondition() {
		_searchQuery.setNewCondition(true);

		Assert.assertTrue(_searchQuery.isNewCondition());
	}

	@Test
	public void testIsSearchDescription() {
		_searchQuery.setSearchDescription(true);

		Assert.assertTrue(_searchQuery.isSearchDescription());
	}

	@Test
	public void testIsUnspecifiedCondition() {
		_searchQuery.setUnspecifiedCondition(true);

		Assert.assertTrue(_searchQuery.isUnspecifiedCondition());
	}

	@Test
	public void testIsUsedCondition() {
		_searchQuery.setUsedCondition(true);

		Assert.assertTrue(_searchQuery.isUsedCondition());
	}

	@Test
	public void testSetAndGetCategoryId() {
		_searchQuery.setCategoryId("100");

		Assert.assertEquals("100", _searchQuery.getCategoryId());
	}

	@Test
	public void testSetAndGetKeywords() {
		_searchQuery.setKeywords("Test keywords");

		Assert.assertEquals("Test keywords", _searchQuery.getKeywords());
	}

	@Test
	public void testSetAndGetMaxPrice() {
		_searchQuery.setMaxPrice(10.00);

		Assert.assertEquals(10.00, _searchQuery.getMaxPrice(), 0);
	}

	@Test
	public void testSetAndGetMinPrice() {
		_searchQuery.setMinPrice(5.00);

		Assert.assertEquals(5.00, _searchQuery.getMinPrice(), 0);
	}

	@Test
	public void testSetAndGetSearchQueryId() {
		_searchQuery.setSearchQueryId(1);

		Assert.assertEquals(1, _searchQuery.getSearchQueryId());
	}



	SearchQuery _searchQuery;
}