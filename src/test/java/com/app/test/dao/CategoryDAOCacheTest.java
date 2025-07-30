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

import com.app.dao.CategoryDAO;
import com.app.model.Category;
import com.app.test.BaseTestCase;

import java.util.List;

import com.app.util.CategoryUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CategoryDAOCacheTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
	}

	@After
	public void tearDown() throws Exception {
		CategoryUtil.deleteCategories();
	}

	@Test
	public void testAddCategoriesCacheEvict() throws Exception {
		Cache categoriesCache = _cacheManager.getCache("categories");

		StatisticsGateway categoriesStatistics =
			categoriesCache.getStatistics();

		long categoriesMissCount = categoriesStatistics.cacheMissCount();

		long categoriesHitCount = categoriesStatistics.cacheHitCount();

		_addCategory("1", "test1");

		_categoryDAO.getCategories();

		Assert.assertEquals(
			categoriesMissCount + 1, categoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			categoriesHitCount, categoriesStatistics.cacheHitCount());

		_addCategory("2", "test2");

		_categoryDAO.getCategories();

		Assert.assertEquals(
			categoriesMissCount + 2, categoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			categoriesHitCount, categoriesStatistics.cacheHitCount());
	}

	@Test
	public void testDeleteCategoriesCacheEvict() throws Exception {
		Cache categoriesCache =
			_cacheManager.getCache("categories");

		StatisticsGateway categoriesStatistics =
			categoriesCache.getStatistics();

		long categoriesMissCount = categoriesStatistics.cacheMissCount();

		long categoriesHitCount = categoriesStatistics.cacheHitCount();

		_categoryDAO.deleteCategories();

		_categoryDAO.getCategories();

		Assert.assertEquals(
			categoriesMissCount + 1, categoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			categoriesHitCount, categoriesStatistics.cacheHitCount());

		_categoryDAO.deleteCategories();

		_categoryDAO.getCategories();

		Assert.assertEquals(
			categoriesMissCount + 2, categoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			categoriesHitCount, categoriesStatistics.cacheHitCount());
	}

	@Test
	public void testGetCategories() throws Exception {
		Cache cache = _cacheManager.getCache("categories");

		StatisticsGateway statistics = cache.getStatistics();

		long categoriesMissCount = statistics.cacheMissCount();
		long categoriesHitCount = statistics.cacheHitCount();

		_addCategory("1", "test1");
		_addCategory("2", "test2");

		List<Category> categories = _categoryDAO.getCategories();

		_assertCategories(categories);

		categories = _categoryDAO.getCategories();

		_assertCategories(categories);

		Assert.assertEquals(
			categoriesMissCount + 1, statistics.cacheMissCount());
		Assert.assertEquals(
			categoriesHitCount + 1, statistics.cacheHitCount());

		_categoryDAO.deleteCategories();

		categories = _categoryDAO.getCategories();

		Assert.assertTrue(categories.isEmpty());

		categories = _categoryDAO.getCategories();

		Assert.assertTrue(categories.isEmpty());

		Assert.assertEquals(
			categoriesMissCount + 2, statistics.cacheMissCount());
		Assert.assertEquals(
			categoriesHitCount + 2, statistics.cacheHitCount());
	}

	private void _assertCategories(List<Category> categories) {
		Assert.assertEquals(2, categories.size());

		Category firstCategory = categories.get(0);
		Category secondCategory = categories.get(1);

		Assert.assertEquals("1", firstCategory.getCategoryId());
		Assert.assertEquals("test1", firstCategory.getCategoryName());

		Assert.assertEquals("2", secondCategory.getCategoryId());
		Assert.assertEquals("test2", secondCategory.getCategoryName());
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private CategoryDAO _categoryDAO;

}