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

import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.runnable.SearchResultRunnable;
import com.app.util.ConstantsUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

import com.app.test.BaseTestCase;

import com.app.util.UserUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.mail.Transport;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({
	EntityUtils.class, HttpClients.class, Transport.class
})
@RunWith(PowerMockRunner.class)
@WebAppConfiguration
public class SearchResultRunnableTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();

		setUpProperties();

		setUpExchangeRateUtil();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		PowerMockito.spy(Transport.class);

		PowerMockito.doNothing().when(
			Transport.class, "send", Mockito.anyObject()
		);
	}

	@After
	public void tearDown() throws Exception {
		SearchResultUtil.deleteSearchQueryResults(_searchQueryId);

		SearchQueryUtil.deleteSearchQueries(_userId);

		UserUtil.deleteUserByUserId(_userId);
	}

	@Test
	public void testRunWithException() throws Exception {
		CloseableHttpClient closeableHttpClient =
			_setUpGetEbaySearchResultsWithException();

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_userId);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setActive(true);

		SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_userId);

		searchResultRunnable.run();

		Mockito.verify(
			closeableHttpClient, Mockito.times(1)
		).execute(
			Mockito.anyObject()
		);

		PowerMockito.verifyStatic(Mockito.times(0));

		Transport.send(Mockito.anyObject());

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_searchQueryId);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testRunWithSearchResults() throws Exception {
		CloseableHttpClient closeableHttpClient =
			_setUpGetEbaySearchResults("/json/auction.json");

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_userId);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setActive(true);

		_searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_userId);

		searchResultRunnable.run();

		Mockito.verify(
			closeableHttpClient, Mockito.times(1)
		).execute(
			Mockito.anyObject()
		);

		PowerMockito.verifyStatic(Mockito.times(1));

		Transport.send(Mockito.anyObject());

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_searchQueryId);

		Assert.assertEquals(2, searchResults.size());
	}

	@Test
	public void testRunWithoutSearchResults() throws Exception {
		CloseableHttpClient closeableHttpClient =
			_setUpGetEbaySearchResults("/json/empty.json");

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_userId);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setActive(true);

		_searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_userId);

		searchResultRunnable.run();

		Mockito.verify(
			closeableHttpClient, Mockito.times(1)
		).execute(
			Mockito.anyObject()
		);

		PowerMockito.verifyStatic(Mockito.times(0));

		Transport.send(Mockito.anyObject());

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(_searchQueryId);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testRunWithoutSearchQueries() throws Exception {
		CloseableHttpClient closeableHttpClient =
			_setUpGetEbaySearchResults("/json/empty.json");

		User user = UserUtil.addUser("user@test.com", "password");

		_userId = user.getUserId();

		SearchResultRunnable searchResultRunnable = new SearchResultRunnable(
			_userId);

		searchResultRunnable.run();

		Mockito.verify(
			closeableHttpClient, Mockito.times(0)
		).execute(
			Mockito.anyObject()
		);

		PowerMockito.verifyStatic(Mockito.times(0));

		Transport.send(Mockito.anyObject());
	}

	private static CloseableHttpClient _setUpGetEbaySearchResultsWithException()
		throws Exception {

		CloseableHttpClient closeableHttpClient = Mockito.mock(
			CloseableHttpClient.class);

		PowerMockito.spy(HttpClients.class);

		PowerMockito.doReturn(
			closeableHttpClient
		).when(
			HttpClients.class, "createDefault"
		);

		Mockito.when(
			closeableHttpClient.execute(Mockito.anyObject())
		).thenThrow(
			new IOException()
		);

		return closeableHttpClient;
	}

	private CloseableHttpClient _setUpGetEbaySearchResults(String jsonPath)
		throws Exception {

		CloseableHttpResponse closeableHttpResponse = Mockito.mock(
			CloseableHttpResponse.class);

		CloseableHttpClient closeableHttpClient = Mockito.mock(
			CloseableHttpClient.class);

		PowerMockito.spy(HttpClients.class);

		PowerMockito.doReturn(
			closeableHttpClient
		).when(
			HttpClients.class, "createDefault"
		);

		Mockito.when(
			closeableHttpClient.execute(Mockito.anyObject())
		).thenReturn(
			closeableHttpResponse
		);

		PowerMockito.spy(EntityUtils.class);

		PowerMockito.doReturn(
			new String(
				Files.readAllBytes(
					new ClassPathResource(jsonPath)
				.getFile().toPath()))
		).when(
			EntityUtils.class, "toString", Mockito.anyObject()
		);

		return closeableHttpClient;
	}

	private static int _searchQueryId;
	private static int _userId;

}