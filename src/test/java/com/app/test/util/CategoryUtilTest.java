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

import com.app.exception.CategoryException;
import com.app.model.Category;
import com.app.test.BaseTestCase;
import com.app.util.CategoryUtil;
import com.app.util.OAuthTokenUtil;
import com.app.util.ReleaseUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({
	EntityUtils.class, HttpClients.class, OAuthTokenUtil.class
})
@RunWith(PowerMockRunner.class)
@WebAppConfiguration
public class CategoryUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		_clazz = Class.forName(CategoryUtil.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@After
	public void tearDown() throws Exception {
		CategoryUtil.deleteCategories();
	}

	@Test
	public void testAddCategory() throws Exception {
		_addCategory(_CATEGORY_ID, _CATEGORY_NAME);

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(1, categories.size());

		Category category = categories.get(0);

		Assert.assertEquals(_CATEGORY_ID, category.getCategoryId());
		Assert.assertEquals(_CATEGORY_NAME, category.getCategoryName());
	}

	@Test
	public void testDeleteCategories() throws Exception {
		_addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));
		_addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(2, categories.size());

		CategoryUtil.deleteCategories();

		categories = CategoryUtil.getCategories();

		Assert.assertEquals(0, categories.size());
	}

	@Test
	public void testGetParentCategories() throws Exception {
		_addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));
		_addCategory(
			RandomStringUtils.randomAlphanumeric(5),
			RandomStringUtils.randomAlphanumeric(5));

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(2, categories.size());
	}

	@Test
	public void testIsNewerCategoryVersion() throws Exception {
		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, "");

		Method method = _clazz.getDeclaredMethod(
			"_isNewerCategoryVersion", String.class);

		method.setAccessible(true);

		Assert.assertTrue((boolean)method.invoke(_classInstance, ""));
		Assert.assertTrue((boolean)method.invoke(_classInstance, "1"));
		Assert.assertTrue((boolean)method.invoke(_classInstance, "100"));
		Assert.assertTrue((boolean)method.invoke(_classInstance, "200"));

		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, "100");

		Assert.assertFalse((boolean)method.invoke(_classInstance, ""));
		Assert.assertFalse((boolean)method.invoke(_classInstance, "1"));
		Assert.assertFalse((boolean)method.invoke(_classInstance, "100"));
		Assert.assertTrue((boolean)method.invoke(_classInstance, "200"));
	}

	@Test
	public void testInitializeCategoriesWithError() throws Exception {
		setUpOAuth2Api();

		CloseableHttpClient closeableHttpClient = setUpHttpRequest(
			_ERROR_JSON_PATH);

		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, "100");

		_addCategory("1", "parentCategory");

		CategoryUtil.initializeCategories();

		Mockito.verify(
			closeableHttpClient, Mockito.times(1)
		).execute(
			Mockito.anyObject()
		);

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(1, categories.size());

		Category category = categories.get(0);

		Assert.assertEquals("1", category.getCategoryId());
		Assert.assertEquals("parentCategory", category.getCategoryName());

		Assert.assertEquals(
			"100", ReleaseUtil.getReleaseVersion(_CATEGORY_RELEASE_NAME));
	}

	@Test
	public void testInitializeCategoriesWithNewerVersion() throws Exception {
		setUpOAuth2Api();

		CloseableHttpClient closeableHttpClient = setUpHttpRequest(
			_CATEGORY_NEWER_VERSION_PATH);

		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, "100");

		_addCategory("1", "parentCategory");

		CategoryUtil.initializeCategories();

		Mockito.verify(
			closeableHttpClient, Mockito.times(2)
		).execute(
			Mockito.anyObject()
		);

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(1, categories.size());

		Category category = categories.get(0);

		Assert.assertEquals("10", category.getCategoryId());
		Assert.assertEquals("newParentCategory", category.getCategoryName());

		Assert.assertEquals(
			"200", ReleaseUtil.getReleaseVersion(_CATEGORY_RELEASE_NAME));
	}

	@Test
	public void testInitializeCategoriesWithOlderVersion() throws Exception {
		setUpOAuth2Api();

		CloseableHttpClient closeableHttpClient = setUpHttpRequest(
			_CATEGORY_OLDER_VERSION_PATH);

		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, "100");

		_addCategory("1", "parentCategory");

		CategoryUtil.initializeCategories();

		Mockito.verify(
			closeableHttpClient, Mockito.times(1)
		).execute(
			Mockito.anyObject()
		);

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(1, categories.size());

		Category category = categories.get(0);

		Assert.assertEquals("1", category.getCategoryId());
		Assert.assertEquals("parentCategory", category.getCategoryName());

		Assert.assertEquals(
			"100", ReleaseUtil.getReleaseVersion(_CATEGORY_RELEASE_NAME));
	}

	@Test
	public void testPopulateCategoriesWithError() throws Exception {
		setUpOAuth2Api();

		CloseableHttpClient closeableHttpClient =
			setUpHttpRequest("/json/ebay/error.json");

		Method populateCategories = _clazz.getDeclaredMethod(
			"_populateCategories");

		populateCategories.setAccessible(true);

		ReleaseUtil.addRelease(_CATEGORY_RELEASE_NAME, "100");

		_addCategory("1", "parentCategory");

		try {
			populateCategories.invoke(_classInstance);
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(ite.getCause() instanceof CategoryException);
		}

		Mockito.verify(
			closeableHttpClient, Mockito.times(1)
		).execute(
			Mockito.anyObject()
		);

		List<Category> categories = CategoryUtil.getCategories();

		Assert.assertEquals(1, categories.size());

		Category category = categories.get(0);

		Assert.assertEquals("1", category.getCategoryId());
		Assert.assertEquals("parentCategory", category.getCategoryName());
	}

	private static final String _CATEGORY_NEWER_VERSION_PATH =
		"/json/ebay/categoryNewerVersion.json";

	private static final String _CATEGORY_OLDER_VERSION_PATH =
		"/json/ebay/categoryOlderVersion.json";

	private static final String _ERROR_JSON_PATH = "/json/ebay/error.json";

	private static Object _classInstance;
	private static Class _clazz;

}