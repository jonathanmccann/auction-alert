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

import com.app.model.SearchResult;
import com.app.test.BaseTestCase;
import com.app.util.PropertiesValues;
import com.app.util.SearchResultUtil;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.app.util.SendGridUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SendGridUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpDatabase();
		setUpProperties();
	}

	@After
	public void tearDown() throws Exception {
		SearchResultUtil.deleteSearchQueryResults(_SEARCH_QUERY_ID);
	}

	@Test
	public void testHandleSendGridEventWithInvalidKey() throws Exception {
		_addSearchResults();

		List<SearchResult> searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}

		SendGridUtil.handleSendGridEvent(
			_getSendGridResponseJson(_BLOCKED_RESPONSE_JSON_PATH), "invalidKey",
			PropertiesValues.SENDGRID_WEBHOOK_VALUE);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}
	}

	@Test
	public void testHandleSendGridEventWithInvalidValue() throws Exception {
		_addSearchResults();

		List<SearchResult> searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}

		SendGridUtil.handleSendGridEvent(
			_getSendGridResponseJson(_BLOCKED_RESPONSE_JSON_PATH),
			PropertiesValues.SENDGRID_WEBHOOK_KEY, "invalidValue");

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}
	}

	@Test
	public void testHandleSendGridEventWithNullKey() throws Exception {
		_addSearchResults();

		List<SearchResult> searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}

		SendGridUtil.handleSendGridEvent(
			_getSendGridResponseJson(_BLOCKED_RESPONSE_JSON_PATH), null,
			PropertiesValues.SENDGRID_WEBHOOK_VALUE);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}
	}

	@Test
	public void testHandleSendGridEventWithNullValue() throws Exception {
		_addSearchResults();

		List<SearchResult> searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}

		SendGridUtil.handleSendGridEvent(
			_getSendGridResponseJson(_BLOCKED_RESPONSE_JSON_PATH),
			PropertiesValues.SENDGRID_WEBHOOK_KEY, null);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}
	}

	@Test
	public void testHandleSendGridEventWithBlockedResponse() throws Exception {
		_addSearchResults();

		List<SearchResult> searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}

		SendGridUtil.handleSendGridEvent(
			_getSendGridResponseJson(_BLOCKED_RESPONSE_JSON_PATH),
			PropertiesValues.SENDGRID_WEBHOOK_KEY,
			PropertiesValues.SENDGRID_WEBHOOK_VALUE);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertFalse(searchResult.isDelivered());
		}
	}

	@Test
	public void testHandleSendGridEventWithBounceResponse() throws Exception {
		_addSearchResults();

		List<SearchResult> searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}

		SendGridUtil.handleSendGridEvent(
			_getSendGridResponseJson(_BOUNCE_RESPONSE_JSON_PATH),
			PropertiesValues.SENDGRID_WEBHOOK_KEY,
			PropertiesValues.SENDGRID_WEBHOOK_VALUE);

		searchResults = SearchResultUtil.getSearchQueryResults(
			_SEARCH_QUERY_ID);

		for (SearchResult searchResult : searchResults) {
			Assert.assertTrue(searchResult.isDelivered());
		}
	}

	private static void _addSearchResults() throws Exception {
		List<SearchResult> searchResults = new ArrayList<>();

		SearchResult searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("1234");

		searchResults.add(searchResult);

		searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("1234");

		searchResults.add(searchResult);

		searchResult = new SearchResult();

		searchResult.setSearchQueryId(_SEARCH_QUERY_ID);
		searchResult.setUserId(_USER_ID);
		searchResult.setItemId("1234");

		searchResults.add(searchResult);

		SearchResultUtil.addSearchResults(_SEARCH_QUERY_ID, searchResults);

		_firstSearchResultIds = String.valueOf(
			searchResults.get(0).getSearchResultId());

		_secondSearchResultIds = String.valueOf(
			searchResults.get(1).getSearchResultId() + "," +
				searchResults.get(2).getSearchResultId());
	}

	private static String _getSendGridResponseJson(String jsonPath)
		throws Exception {

		String blockedResponseJson = new String(
				Files.readAllBytes(
					new ClassPathResource(jsonPath)
				.getFile().toPath()));

		blockedResponseJson = blockedResponseJson.replace(
			"firstSearchResultIds", _firstSearchResultIds);

		blockedResponseJson = blockedResponseJson.replace(
			"secondSearchResultIds", _secondSearchResultIds);

		return blockedResponseJson;
	}

	private static String _firstSearchResultIds;
	private static String _secondSearchResultIds;

	private static final String _BLOCKED_RESPONSE_JSON_PATH =
		"/json/sendgrid/blockedResponse.json";
	private static final String _BOUNCE_RESPONSE_JSON_PATH =
		"/json/sendgrid/bounceResponse.json";
	private static final int _SEARCH_QUERY_ID = 1;

}