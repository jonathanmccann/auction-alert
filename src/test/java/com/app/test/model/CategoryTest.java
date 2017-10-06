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

import com.app.model.Category;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class CategoryTest {

	@Before
	public void setUp() {
		_category = new Category();
	}

	@Test
	public void testConstructor() {
		Category category = new Category("1", "Category", "1", 1);

		Assert.assertEquals("1", category.getCategoryId());
		Assert.assertEquals("Category", category.getCategoryName());
		Assert.assertEquals("1", category.getCategoryParentId());
		Assert.assertEquals(1, category.getCategoryLevel());
	}

	@Test
	public void testSetAndGetCategoryId() {
		_category.setCategoryId("1");

		Assert.assertEquals("1", _category.getCategoryId());
	}

	@Test
	public void testSetAndGetCategoryLevel() {
		_category.setCategoryLevel(1);

		Assert.assertEquals(1, _category.getCategoryLevel());
	}

	@Test
	public void testSetAndGetCategoryName() {
		_category.setCategoryName("Category");

		Assert.assertEquals("Category", _category.getCategoryName());
	}

	@Test
	public void testSetAndGetCategoryParentId() {
		_category.setCategoryParentId("1");

		Assert.assertEquals("1", _category.getCategoryParentId());
	}

	private static Category _category;

}