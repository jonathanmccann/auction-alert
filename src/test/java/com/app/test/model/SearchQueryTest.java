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
	public void testConstructor() {
		Assert.assertEquals(0, _searchQuery.getSearchQueryId());
		Assert.assertTrue(ValidatorUtil.isNull(_searchQuery.getKeywords()));
		Assert.assertTrue(ValidatorUtil.isNull(_searchQuery.getCategoryId()));
	}

	@Test
	public void testEqualsWithEqualObject() {
		_searchQuery.setSearchQueryId(1);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(1);

		Assert.assertTrue(_searchQuery.equals(searchQuery));
	}

	@Test
	public void testEqualsWithInequalItemId() {
		_searchQuery.setSearchQueryId(1);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(2);

		Assert.assertFalse(_searchQuery.equals(searchQuery));
	}

	@Test
	public void testEqualsWithInequalObject() {
		Assert.assertFalse(_searchQuery.equals(new Object()));
	}

	@Test
	public void testEqualsWithNullObject() {
		Assert.assertFalse(_searchQuery.equals(null));
	}

	@Test
	public void testHashCode() {
		_searchQuery.setSearchQueryId(1);

		Assert.assertEquals(1, _searchQuery.hashCode());
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
	public void testIsActive() {
		_searchQuery.setActive(true);

		Assert.assertTrue(_searchQuery.isActive());
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
		_searchQuery.setCategoryId("1");

		Assert.assertEquals("1", _searchQuery.getCategoryId());
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

	@Test
	public void testSetAndGetUserId() {
		_searchQuery.setUserId(_USER_ID);

		Assert.assertEquals(_USER_ID, _searchQuery.getUserId());
	}

	private static SearchQuery _searchQuery;

	private static final int _USER_ID = 1;

}