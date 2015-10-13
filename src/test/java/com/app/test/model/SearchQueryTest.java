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
	public void testSetAndGetSearchQueryId() {
		_searchQuery.setSearchQueryId(1);

		Assert.assertEquals(1, _searchQuery.getSearchQueryId());
	}



	SearchQuery _searchQuery;
}