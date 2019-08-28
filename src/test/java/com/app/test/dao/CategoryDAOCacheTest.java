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

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
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
		Cache parentCategoriesCache =
			_cacheManager.getCache("parentCategories");

		Cache subCategoriesCache =
			_cacheManager.getCache("subcategories");

		StatisticsGateway parentCategoriesStatistics =
			parentCategoriesCache.getStatistics();

		StatisticsGateway subCategoriesStatistics =
			subCategoriesCache.getStatistics();

		long parentCategoriesMissCount =
			parentCategoriesStatistics.cacheMissCount();

		long parentCategoriesHitCount =
			parentCategoriesStatistics.cacheHitCount();

		long subCategoriesMissCount = subCategoriesStatistics.cacheMissCount();
		long subCategoriesHitCount = subCategoriesStatistics.cacheHitCount();

		_addCategory("1", "test1", "1", 1);

		_categoryDAO.getParentCategories();

		Assert.assertEquals(
			parentCategoriesMissCount + 1,
			parentCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			parentCategoriesHitCount,
			parentCategoriesStatistics.cacheHitCount());

		_categoryDAO.getSubcategories("1");

		Assert.assertEquals(
			subCategoriesMissCount + 1,
			subCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			subCategoriesHitCount, subCategoriesStatistics.cacheHitCount());

		_addCategory("2", "test2", "2", 1);

		_categoryDAO.getParentCategories();

		Assert.assertEquals(
			parentCategoriesMissCount + 2,
			parentCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			parentCategoriesHitCount,
			parentCategoriesStatistics.cacheHitCount());

		_categoryDAO.getSubcategories("1");

		Assert.assertEquals(
			subCategoriesMissCount + 2,
			subCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			subCategoriesHitCount, subCategoriesStatistics.cacheHitCount());
	}

	@Test
	public void testDeleteCategoriesCacheEvict() throws Exception {
		Cache parentCategoriesCache =
			_cacheManager.getCache("parentCategories");

		Cache subCategoriesCache =
			_cacheManager.getCache("subcategories");

		StatisticsGateway parentCategoriesStatistics =
			parentCategoriesCache.getStatistics();

		StatisticsGateway subCategoriesStatistics =
			subCategoriesCache.getStatistics();

		long parentCategoriesMissCount =
			parentCategoriesStatistics.cacheMissCount();

		long parentCategoriesHitCount =
			parentCategoriesStatistics.cacheHitCount();

		long subCategoriesMissCount = subCategoriesStatistics.cacheMissCount();
		long subCategoriesHitCount = subCategoriesStatistics.cacheHitCount();

		_categoryDAO.deleteCategories();

		_categoryDAO.getParentCategories();

		Assert.assertEquals(
			parentCategoriesMissCount + 1,
			parentCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			parentCategoriesHitCount,
			parentCategoriesStatistics.cacheHitCount());

		_categoryDAO.getSubcategories("1");

		Assert.assertEquals(
			subCategoriesMissCount + 1,
			subCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			subCategoriesHitCount, subCategoriesStatistics.cacheHitCount());

		_categoryDAO.deleteCategories();

		_categoryDAO.getParentCategories();

		Assert.assertEquals(
			parentCategoriesMissCount + 2,
			parentCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			parentCategoriesHitCount,
			parentCategoriesStatistics.cacheHitCount());

		_categoryDAO.getSubcategories("1");

		Assert.assertEquals(
			subCategoriesMissCount + 2,
			subCategoriesStatistics.cacheMissCount());
		Assert.assertEquals(
			subCategoriesHitCount, subCategoriesStatistics.cacheHitCount());
	}

	@Test
	public void testGetParentCategories() throws Exception {
		Cache cache = _cacheManager.getCache("parentCategories");

		StatisticsGateway statistics = cache.getStatistics();

		long parentCategoriesMissCount = statistics.cacheMissCount();
		long parentCategoriesHitCount = statistics.cacheHitCount();

		_addCategory("1", "test1", "1", 1);
		_addCategory("2", "test2", "2", 1);

		List<Category> categories = _categoryDAO.getParentCategories();

		_assertCategories(categories);

		categories = _categoryDAO.getParentCategories();

		_assertCategories(categories);

		Assert.assertEquals(
			parentCategoriesMissCount + 1, statistics.cacheMissCount());
		Assert.assertEquals(
			parentCategoriesHitCount + 1, statistics.cacheHitCount());

		_categoryDAO.deleteCategories();

		categories = _categoryDAO.getParentCategories();

		Assert.assertTrue(categories.isEmpty());

		categories = _categoryDAO.getParentCategories();

		Assert.assertTrue(categories.isEmpty());

		Assert.assertEquals(
			parentCategoriesMissCount + 2, statistics.cacheMissCount());
		Assert.assertEquals(
			parentCategoriesHitCount + 2, statistics.cacheHitCount());
	}

	@Test
	public void testGetSubCategories() throws Exception {
		Cache cache = _cacheManager.getCache("subcategories");

		StatisticsGateway statistics = cache.getStatistics();

		long subCategoriesMissCount = statistics.cacheMissCount();
		long subCategoriesHitCount = statistics.cacheHitCount();

		_addCategory("1", "test1", "1", 1);
		_addCategory("2", "test2", "1", 2);

		List<Category> categories = _categoryDAO.getSubcategories("1");

		_assertSubcategories(categories);

		categories = _categoryDAO.getSubcategories("1");

		_assertSubcategories(categories);

		Assert.assertEquals(
			subCategoriesMissCount + 1, statistics.cacheMissCount());
		Assert.assertEquals(
			subCategoriesHitCount + 1, statistics.cacheHitCount());

		_categoryDAO.deleteCategories();

		categories = _categoryDAO.getSubcategories("1");

		Assert.assertTrue(categories.isEmpty());

		categories = _categoryDAO.getSubcategories("1");

		Assert.assertTrue(categories.isEmpty());

		Assert.assertEquals(
			subCategoriesMissCount + 2, statistics.cacheMissCount());
		Assert.assertEquals(
			subCategoriesHitCount + 2, statistics.cacheHitCount());
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

	private void _assertSubcategories(List<Category> categories) {
		Assert.assertEquals(1, categories.size());

		Category subcategory = categories.get(0);

		Assert.assertEquals("2", subcategory.getCategoryId());
		Assert.assertEquals("test2", subcategory.getCategoryName());
		Assert.assertEquals("1", subcategory.getCategoryParentId());
	}

	@Autowired
	private CacheManager _cacheManager;

	@Autowired
	private CategoryDAO _categoryDAO;

}