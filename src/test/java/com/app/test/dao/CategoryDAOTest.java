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

package com.app.test.dao;

import com.app.dao.impl.CategoryDAOImpl;
import com.app.exception.DatabaseConnectionException;
import com.app.model.CategoryModel;
import com.app.test.BaseDatabaseTestCase;

import java.sql.SQLException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class CategoryDAOTest extends BaseDatabaseTestCase {

	@Override
	public void doSetUp() throws DatabaseConnectionException {
		_categoryDAOImpl = new CategoryDAOImpl();
	}

	@Test
	public void testCategoryDAO()
		throws DatabaseConnectionException, SQLException {

		// Test add

		_categoryDAOImpl.addCategory("1", "First Category");
		_categoryDAOImpl.addCategory("2", "Second Category");

		// Test get

		CategoryModel category = _categoryDAOImpl.getCategory("1");

		Assert.assertEquals("First Category", category.getCategoryName());

		// Test get multiple

		List<CategoryModel> categoryModels = _categoryDAOImpl.getCategories();

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

		_categoryDAOImpl.deleteCategory("1");
		_categoryDAOImpl.deleteCategory("2");

		categoryModels = _categoryDAOImpl.getCategories();

		Assert.assertEquals(0, categoryModels.size());
	}

	@Test
	public void testDeleteAllCategories()
		throws DatabaseConnectionException, SQLException {

		_categoryDAOImpl.addCategory("1", "First Category");
		_categoryDAOImpl.addCategory("2", "Second Category");

		_categoryDAOImpl.deleteCategories();

		List<CategoryModel> categoryModels = _categoryDAOImpl.getCategories();

		Assert.assertEquals(0, categoryModels.size());
	}

	private static CategoryDAOImpl _categoryDAOImpl;

}