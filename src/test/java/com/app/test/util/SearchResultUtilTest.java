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

import com.app.dao.SearchQueryPreviousResultDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.SearchResultUtil;

import java.sql.SQLException;

import java.util.Date;
import java.util.List;

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
public class SearchResultUtilTest extends BaseTestCase {

	@After
	public void tearDown() throws Exception {
		SearchResultUtil.deleteSearchQueryResults(1);
	}

	@Test
	public void testAddSearchResult() throws Exception {
		Date endingTime = new Date();

		SearchResult searchResult = new SearchResult(
			1, "1234", "First Item", 10.00, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			endingTime, "Auction");

		int searchResultId = SearchResultUtil.addSearchResult(searchResult);

		searchResult = SearchResultUtil.getSearchResult(searchResultId);

		Assert.assertEquals(1, searchResult.getSearchQueryId());
		Assert.assertEquals("1234", searchResult.getItemId());
		Assert.assertEquals("First Item", searchResult.getItemTitle());
		Assert.assertEquals(10.00, searchResult.getAuctionPrice(), 0);
		Assert.assertEquals(14.99, searchResult.getFixedPrice(), 0);
		Assert.assertEquals(
			"http://www.ebay.com/itm/1234", searchResult.getItemURL());
		Assert.assertEquals(
			"http://www.ebay.com/123.jpg", searchResult.getGalleryURL());
		Assert.assertEquals(endingTime, searchResult.getEndingTime());
		Assert.assertEquals("Auction", searchResult.getTypeOfAuction());
	}

	@Test
	public void testDeleteSearchQueryResults() throws Exception {
		Date endingTime = new Date();

		SearchResult searchResult = new SearchResult(
			1, "1234", "First Item", 10.00, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			endingTime, "Auction");

		SearchResultUtil.addSearchResult(searchResult);

		searchResult = new SearchResult(
			1, "2345", "Second Item", 14.99, 14.99,
			"http://www.ebay.com/itm/2345", "http://www.ebay.com/234.jpg",
			endingTime, "FixedPrice");

		SearchResultUtil.addSearchResult(searchResult);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(2, searchResults.size());

		SearchResultUtil.deleteSearchQueryResults(1);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteSearchResult() throws Exception {
		Date endingTime = new Date();

		SearchResult searchResult = new SearchResult(
			1, "1234", "First Item", 10.00, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg",
			endingTime, "Auction");

		int firstSearchResultId = SearchResultUtil.addSearchResult(
			searchResult);

		searchResult = new SearchResult(
			1, "2345", "Second Item", 14.99, 14.99,
			"http://www.ebay.com/itm/2345", "http://www.ebay.com/234.jpg",
			endingTime, "FixedPrice");

		SearchResultUtil.addSearchResult(searchResult);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(2, searchResults.size());

		SearchResultUtil.deleteSearchResult(firstSearchResultId);

		searchResults = SearchResultUtil.getSearchQueryResults(1);

		Assert.assertEquals(1, searchResults.size());
	}

	@Test(expected = SQLException.class)
	public void testGetNonExistentSearchResult() throws Exception {
		SearchResultUtil.getSearchResult(1);
	}

}