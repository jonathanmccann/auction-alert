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

package com.app.test.runnable;

import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.runnable.SearchResultRunnable;
import com.app.util.EbaySearchResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import com.app.test.BaseTestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@PrepareForTest({
	SearchResultUtil.class, SearchQueryUtil.class
})
public class SearchResultRunnableTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpProperties();

		PowerMockito.spy(EbaySearchResultUtil.class);
		PowerMockito.spy(SearchQueryUtil.class);
		PowerMockito.spy(SearchResultUtil.class);

		setUpMailSender();
	}

	@Test
	public void testRunWithException() throws Exception {
		PowerMockito.doThrow(new DatabaseConnectionException()).when(
			SearchQueryUtil.class, "getSearchQueries",
			Mockito.anyInt(), Mockito.anyBoolean()
		);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_USER_ID);

		searchResultRunnable.run();

		PowerMockito.verifyStatic(Mockito.times(0));
		EbaySearchResultUtil.getEbaySearchResults(Mockito.anyObject());

		Mockito.verify(
			_mockMailSender, Mockito.times(0)
		).sendSearchResultsToRecipient(
			Mockito.anyInt(), Mockito.anyMap()
		);

		PowerMockito.verifyStatic(Mockito.times(0));
		SearchResultUtil.filterSearchResults(
			Mockito.anyObject(), Mockito.anyObject());

		PowerMockito.verifyStatic(Mockito.times(1));
		SearchQueryUtil.getSearchQueries(
			Mockito.anyInt(), Mockito.anyBoolean());
	}

	@Test
	public void testRunWithSearchResults() throws Exception {
		List<SearchResult> searchResults = new ArrayList<>();

		PowerMockito.doReturn(searchResults).when(
			EbaySearchResultUtil.class, "getEbaySearchResults",
			Mockito.anyObject()
		);

		List<SearchQuery> searchQueries = new ArrayList<>();

		searchQueries.add(new SearchQuery());

		PowerMockito.doReturn(searchQueries).when(
			SearchQueryUtil.class, "getSearchQueries",
			Mockito.anyInt(), Mockito.anyBoolean()
		);

		searchResults.add(new SearchResult());

		PowerMockito.doReturn(searchResults).when(
			SearchResultUtil.class, "filterSearchResults",
			Mockito.anyObject(), Mockito.anyObject()
		);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_USER_ID);

		searchResultRunnable.run();

		PowerMockito.verifyStatic(Mockito.times(1));
		EbaySearchResultUtil.getEbaySearchResults(Mockito.anyObject());

		Mockito.verify(
			_mockMailSender, Mockito.times(1)
		).sendSearchResultsToRecipient(
			Mockito.anyInt(), Mockito.anyMap()
		);

		PowerMockito.verifyStatic(Mockito.times(1));
		SearchResultUtil.filterSearchResults(
			Mockito.anyObject(), Mockito.anyObject());

		PowerMockito.verifyStatic(Mockito.times(1));
		SearchQueryUtil.getSearchQueries(
			Mockito.anyInt(), Mockito.anyBoolean());
	}

	@Test
	public void testRunWithoutSearchResults() throws Exception {
		List<SearchResult> searchResults = new ArrayList<>();

		PowerMockito.doReturn(searchResults).when(
			EbaySearchResultUtil.class, "getEbaySearchResults",
			Mockito.anyObject()
		);

		List<SearchQuery> searchQueries = new ArrayList<>();

		searchQueries.add(new SearchQuery());

		PowerMockito.doReturn(searchQueries).when(
			SearchQueryUtil.class, "getSearchQueries",
			Mockito.anyInt(), Mockito.anyBoolean()
		);

		PowerMockito.doReturn(searchResults).when(
			SearchResultUtil.class, "filterSearchResults",
			Mockito.anyObject(), Mockito.anyObject()
		);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_USER_ID);

		searchResultRunnable.run();

		PowerMockito.verifyStatic(Mockito.times(1));
		EbaySearchResultUtil.getEbaySearchResults(Mockito.anyObject());

		Mockito.verify(
			_mockMailSender, Mockito.times(0)
		).sendSearchResultsToRecipient(
			Mockito.anyInt(), Mockito.anyMap()
		);

		PowerMockito.verifyStatic(Mockito.times(1));
		SearchResultUtil.filterSearchResults(
			Mockito.anyObject(), Mockito.anyObject());

		PowerMockito.verifyStatic(Mockito.times(1));
		SearchQueryUtil.getSearchQueries(
			Mockito.anyInt(), Mockito.anyBoolean());
	}

	@Test
	public void testRunWithoutSearchQueries() throws Exception {
		List<SearchQuery> searchQueries = new ArrayList<>();

		PowerMockito.doReturn(searchQueries).when(
			SearchQueryUtil.class, "getSearchQueries",
			Mockito.anyInt(), Mockito.anyBoolean()
		);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_USER_ID);

		searchResultRunnable.run();

		PowerMockito.verifyStatic(Mockito.times(0));
		EbaySearchResultUtil.getEbaySearchResults(Mockito.anyObject());

		Mockito.verify(
			_mockMailSender, Mockito.times(0)
		).sendSearchResultsToRecipient(
			Mockito.anyInt(), Mockito.anyMap()
		);

		PowerMockito.verifyStatic(Mockito.times(0));
		SearchResultUtil.filterSearchResults(
			Mockito.anyObject(), Mockito.anyObject());

		PowerMockito.verifyStatic(Mockito.times(1));
		SearchQueryUtil.getSearchQueries(
			Mockito.anyInt(), Mockito.anyBoolean());
	}

}