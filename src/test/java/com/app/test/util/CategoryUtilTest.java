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

import com.app.model.CategoryModel;
import com.app.test.BaseDatabaseTestCase;
import com.app.util.CategoryUtil;
import com.app.util.PropertiesKeys;
import com.app.util.PropertiesUtil;
import com.app.util.ReleaseUtil;
import com.app.util.eBayAPIUtil;

import java.net.URL;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import org.junit.After;
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
	public void doSetUp() throws Exception {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		eBayAPIUtil.loadApiContext(
			System.getProperty(PropertiesKeys.EBAY_TOKEN));
	}

	@After
	public void tearDown() throws Exception {
		CategoryUtil.deleteCategories();

		ReleaseUtil.deleteRelease(_RELEASE_CATEGORY_NAME);
	}

	@Test
	public void testAddCategory() throws Exception {
		addCategory();

		List<CategoryModel> categories = CategoryUtil.getCategories();

		Assert.assertEquals(1, categories.size());

		CategoryModel category = categories.get(0);

		Assert.assertEquals(_CATEGORY_ID, category.getCategoryId());
		Assert.assertEquals(_CATEGORY_NAME, category.getCategoryName());
	}

	@Test
	public void testDeleteCategories() throws Exception {
		addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));
		addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));

		CategoryUtil.deleteCategories();

		List<CategoryModel> categories = CategoryUtil.getCategories();

		Assert.assertEquals(0, categories.size());
	}

	@Test
	public void testDeleteCategory() throws Exception {
		addCategory();

		CategoryUtil.deleteCategory(_CATEGORY_ID);

		List<CategoryModel> categories = CategoryUtil.getCategories();

		Assert.assertEquals(0, categories.size());
	}

	@Test
	public void testGetCategories() throws Exception {
		addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));
		addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));

		List<CategoryModel> categories = CategoryUtil.getCategories();

		Assert.assertEquals(2, categories.size());
	}

	@Test
	public void testGetCategory() throws Exception {
		addCategory();

		CategoryModel category = CategoryUtil.getCategory(_CATEGORY_ID);

		Assert.assertEquals(_CATEGORY_ID, category.getCategoryId());
		Assert.assertEquals(_CATEGORY_NAME, category.getCategoryName());
	}

	@Test
	public void testInitializeCategories() throws Exception {
		CategoryUtil.initializeCategories();

		List<CategoryModel> categories = CategoryUtil.getCategories();

		Assert.assertFalse(categories.isEmpty());
	}

	private static void addCategory() throws Exception {
		addCategory(_CATEGORY_ID, _CATEGORY_NAME);
	}

	private static void addCategory(String categoryId, String categoryName)
		throws Exception {

		CategoryUtil.addCategory(categoryId, categoryName);
	}

	private static final String _CATEGORY_ID = "categoryId";
	private static final String _CATEGORY_NAME = "categoryName";
	private static final String _RELEASE_CATEGORY_NAME = "category";

}