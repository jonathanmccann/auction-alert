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

package com.app.test.util;

import com.app.exception.DatabaseConnectionException;
import com.app.model.CategoryModel;
import com.app.test.BaseDatabaseTestCase;
import com.app.util.CategoryUtil;

import java.sql.SQLException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoryUtilTest extends BaseDatabaseTestCase {

	@Override
	public void doSetUp() throws DatabaseConnectionException {
	}

	@Test
	public void testCategoryUtil()
		throws DatabaseConnectionException, SQLException {

		// Test add

		CategoryUtil.addCategory("1", "First Category");
		CategoryUtil.addCategory("2", "Second Category");

		// Test get

		CategoryModel category = CategoryUtil.getCategory("1");

		Assert.assertEquals("First Category", category.getCategoryName());

		// Test get multiple

		List<CategoryModel> categoryModels = CategoryUtil.getCategories();

		CategoryModel firstCategoryModel = categoryModels.get(0);
		CategoryModel secondCategoryModel = categoryModels.get(1);

		Assert.assertEquals(2, categoryModels.size());
		Assert.assertEquals("1", firstCategoryModel.getCategoryId());
		Assert.assertEquals("2", secondCategoryModel.getCategoryId());
		Assert.assertEquals(
			"First Category", firstCategoryModel.getCategoryName());
		Assert.assertEquals(
			"Second Category", secondCategoryModel.getCategoryName());

		// Test delete multiple

		CategoryUtil.deleteCategory("1");
		CategoryUtil.deleteCategory("2");

		categoryModels = CategoryUtil.getCategories();

		Assert.assertEquals(0, categoryModels.size());
	}

	@Test
	public void testDeleteAllCategories()
		throws DatabaseConnectionException, SQLException {

		CategoryUtil.addCategory("1", "First Category");
		CategoryUtil.addCategory("2", "Second Category");

		CategoryUtil.deleteCategories();

		List<CategoryModel> categoryModels = CategoryUtil.getCategories();

		Assert.assertEquals(0, categoryModels.size());
	}

}