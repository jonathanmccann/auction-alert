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

import com.app.exception.SearchQueryException;
import com.app.model.SearchQuery;
import com.app.test.BaseTestCase;
import com.app.util.SearchQueryUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SearchQueryUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
		setUpProperties();

		_clazz = Class.forName(SearchQueryUtil.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@After
	public void tearDown() throws Exception {
		SearchQueryUtil.deleteSearchQueries(_USER_ID);
	}

	@Test
	public void testActivateSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchQueryUtil.activateSearchQuery(_USER_ID, searchQueryId);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testAddSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");
		searchQuery.setSearchDescription(true);
		searchQuery.setFreeShippingOnly(true);
		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);
		searchQuery.setMinPrice(5.00);
		searchQuery.setMaxPrice(10.00);
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test(expected = SearchQueryException.class)
	public void testAddSearchQueryExceedingMaximumNumberOfSearchQueries()
		throws Exception {

		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("Test keywords");

		SearchQueryUtil.addSearchQuery(firstSearchQuery);

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");

		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		SearchQuery thirdSearchQuery = new SearchQuery();

		thirdSearchQuery.setUserId(_USER_ID);
		thirdSearchQuery.setKeywords("Third test keywords");

		SearchQueryUtil.addSearchQuery(thirdSearchQuery);
	}

	@Test
	public void testAddSearchQueryWithKeywords() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("", searchQuery.getCategoryId());
	}

	@Test
	public void testAddSearchQueryWithKeywordsAndCategory() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("First test keywords");
		searchQuery.setCategoryId("100");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals("First test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
	}

	@Test
	public void testAddSearchQueryWithNormalizedValues() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");
		searchQuery.setSearchDescription(true);
		searchQuery.setFreeShippingOnly(true);
		searchQuery.setMinPrice(5.00);
		searchQuery.setMaxPrice(10.00);
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testDeactivateSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchQueryUtil.deactivateSearchQuery(_USER_ID, searchQueryId);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertFalse(searchQuery.isActive());
	}

	@Test
	public void testDeleteSearchQueries() throws Exception {
		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");
		firstSearchQuery.setActive(true);

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");
		secondSearchQuery.setActive(true);

		SearchQueryUtil.addSearchQuery(firstSearchQuery);
		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(2, searchQueries.size());

		SearchQueryUtil.deleteSearchQueries(_USER_ID);

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID, true);

		Assert.assertEquals(0, searchQueries.size());
	}

	@Test
	public void testDeleteSearchQuery() throws Exception {
		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");
		firstSearchQuery.setActive(true);

		int firstSearchQueryId = SearchQueryUtil.addSearchQuery(
			firstSearchQuery);

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");
		secondSearchQuery.setActive(true);

		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(2, searchQueries.size());

		SearchQueryUtil.deleteSearchQuery(_USER_ID, firstSearchQueryId);

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID, true);

		Assert.assertEquals(1, searchQueries.size());
	}

	@Test(expected = SQLException.class)
	public void testGetNonExistantSearchQuery() throws Exception {
		SearchQueryUtil.getSearchQuery(1);
	}

	@Test
	public void testGetSearchQueries() throws Exception {
		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");
		firstSearchQuery.setActive(true);

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");
		secondSearchQuery.setActive(false);

		SearchQueryUtil.addSearchQuery(firstSearchQuery);
		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID);

		Assert.assertEquals(2, searchQueries.size());
	}

	@Test
	public void testGetActiveAndInactiveSearchQueries() throws Exception {
		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");
		firstSearchQuery.setActive(true);

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");
		secondSearchQuery.setActive(true);

		SearchQueryUtil.addSearchQuery(firstSearchQuery);
		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
			_USER_ID, true);

		Assert.assertEquals(2, searchQueries.size());

		SearchQuery searchQuery = searchQueries.get(0);

		searchQuery.setActive(false);

		SearchQueryUtil.updateSearchQuery(_USER_ID, searchQuery);

		searchQueries = SearchQueryUtil.getSearchQueries(_USER_ID, false);

		Assert.assertEquals(1, searchQueries.size());
	}

	@Test
	public void testIsExceedsTotalNumberOfSearchQueriesAllowed()
		throws Exception {

		Assert.assertFalse(
			SearchQueryUtil.exceedsMaximumNumberOfSearchQueries(
				_USER_ID));

		SearchQuery firstSearchQuery = new SearchQuery();

		firstSearchQuery.setUserId(_USER_ID);
		firstSearchQuery.setKeywords("First test keywords");

		SearchQuery secondSearchQuery = new SearchQuery();

		secondSearchQuery.setUserId(_USER_ID);
		secondSearchQuery.setKeywords("Second test keywords");

		SearchQueryUtil.addSearchQuery(firstSearchQuery);
		SearchQueryUtil.addSearchQuery(secondSearchQuery);

		Assert.assertTrue(
			SearchQueryUtil.exceedsMaximumNumberOfSearchQueries(
				_USER_ID));
	}

	@Test
	public void testEscapeSearchQueryKeywords() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_escapeSearchQueryKeywords", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setKeywords("<script>alert('Site XSS');</script>");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals(
			"scriptalert('Site XSS');/script", searchQuery.getKeywords());
	}

	@Test
	public void testNormalizeSearchQueryCategoryId() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_normalizeSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setCategoryId("");
		searchQuery.setSubcategoryId("");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());

		searchQuery.setCategoryId("All Categories");
		searchQuery.setSubcategoryId("All Subcategories");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());

		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("All Subcategories");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());

		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());

		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());

		searchQuery.setCategoryId("");
		searchQuery.setSubcategoryId("200");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());

		searchQuery.setCategoryId("All Categories");
		searchQuery.setSubcategoryId("200");

		method.invoke(_classInstance, searchQuery);

		Assert.assertEquals("", searchQuery.getCategoryId());
		Assert.assertEquals("", searchQuery.getSubcategoryId());
	}

	@Test
	public void testNormalizeSearchQueryCondition() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_normalizeSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setNewCondition(false);
		searchQuery.setUsedCondition(false);
		searchQuery.setUnspecifiedCondition(false);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());

		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(false);
		searchQuery.setUnspecifiedCondition(false);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());

		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(false);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());

		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(true);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());

		searchQuery.setNewCondition(true);
		searchQuery.setUsedCondition(false);
		searchQuery.setUnspecifiedCondition(true);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());

		searchQuery.setNewCondition(false);
		searchQuery.setUsedCondition(true);
		searchQuery.setUnspecifiedCondition(false);

		method.invoke(_classInstance, searchQuery);

		Assert.assertFalse(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());

		searchQuery.setNewCondition(false);
		searchQuery.setUsedCondition(false);
		searchQuery.setUnspecifiedCondition(true);

		method.invoke(_classInstance, searchQuery);

		Assert.assertFalse(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
	}

	@Test
	public void testNormalizeSearchQueryListingType() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_normalizeSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setAuctionListing(false);
		searchQuery.setFixedPriceListing(false);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());

		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(false);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());

		searchQuery.setAuctionListing(false);
		searchQuery.setFixedPriceListing(true);

		method.invoke(_classInstance, searchQuery);

		Assert.assertFalse(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());

		searchQuery.setAuctionListing(true);
		searchQuery.setFixedPriceListing(true);

		method.invoke(_classInstance, searchQuery);

		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
	}

	@Test
	public void testUpdateSearchQuery() throws Exception {
		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setCategoryId("100");
		searchQuery.setSubcategoryId("200");

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("Test keywords", searchQuery.getKeywords());
		Assert.assertEquals("100", searchQuery.getCategoryId());
		Assert.assertEquals("200", searchQuery.getSubcategoryId());
		Assert.assertFalse(searchQuery.isSearchDescription());
		Assert.assertFalse(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertTrue(searchQuery.isUsedCondition());
		Assert.assertTrue(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertTrue(searchQuery.isFixedPriceListing());
		Assert.assertEquals(0.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(0.00, searchQuery.getMaxPrice(), 0);
		Assert.assertFalse(searchQuery.isActive());

		searchQuery = new SearchQuery();

		searchQuery.setSearchQueryId(searchQueryId);
		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("New test keywords");
		searchQuery.setCategoryId("101");
		searchQuery.setSubcategoryId("201");
		searchQuery.setSearchDescription(true);
		searchQuery.setFreeShippingOnly(true);
		searchQuery.setNewCondition(true);
		searchQuery.setAuctionListing(true);
		searchQuery.setMinPrice(5.00);
		searchQuery.setMaxPrice(10.00);
		searchQuery.setActive(true);

		SearchQueryUtil.updateSearchQuery(_USER_ID, searchQuery);

		searchQuery = SearchQueryUtil.getSearchQuery(searchQueryId);

		Assert.assertEquals(_USER_ID, searchQuery.getUserId());
		Assert.assertEquals("New test keywords", searchQuery.getKeywords());
		Assert.assertEquals("101", searchQuery.getCategoryId());
		Assert.assertEquals("201", searchQuery.getSubcategoryId());
		Assert.assertTrue(searchQuery.isSearchDescription());
		Assert.assertTrue(searchQuery.isFreeShippingOnly());
		Assert.assertTrue(searchQuery.isNewCondition());
		Assert.assertFalse(searchQuery.isUsedCondition());
		Assert.assertFalse(searchQuery.isUnspecifiedCondition());
		Assert.assertTrue(searchQuery.isAuctionListing());
		Assert.assertFalse(searchQuery.isFixedPriceListing());
		Assert.assertEquals(5.00, searchQuery.getMinPrice(), 0);
		Assert.assertEquals(10.00, searchQuery.getMaxPrice(), 0);
		Assert.assertTrue(searchQuery.isActive());
	}

	@Test
	public void testValidateSearchQueryKeywords() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_validateSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setKeywords("Test keywords");

		method.invoke(_classInstance, searchQuery);
	}

	@Test
	public void testValidateSearchQueryInvalidKeywords() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_validateSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		try {
			method.invoke(_classInstance, searchQuery);

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof SearchQueryException);
		}
	}

	@Test
	public void testValidateSearchQueryPriceRange() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_validateSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setKeywords("Test keywords");
		searchQuery.setMinPrice(0.00);
		searchQuery.setMaxPrice(5.00);

		method.invoke(_classInstance, searchQuery);
	}

	@Test
	public void testValidateSearchQueryInvalidPriceRange() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"_validateSearchQuery", SearchQuery.class);

		method.setAccessible(true);

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setKeywords("Test keywords");
		searchQuery.setMinPrice(5.00);
		searchQuery.setMaxPrice(0.00);

		try {
			method.invoke(_classInstance, searchQuery);

			Assert.fail();
		}
		catch (InvocationTargetException ite) {
			Assert.assertTrue(
				ite.getCause() instanceof SearchQueryException);
		}
	}


	private static Object _classInstance;
	private static Class _clazz;

}