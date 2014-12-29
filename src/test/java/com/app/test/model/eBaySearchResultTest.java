package com.app.test.model;

import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResult;
import com.app.util.PropertiesUtil;
import com.app.util.eBayAPIUtil;

import java.io.IOException;

import java.net.URL;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultTest {

	@Before
	public void setUp() throws IOException {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		Properties properties = PropertiesUtil.getConfigurationProperties();

		properties.setProperty(
			PropertiesUtil.APPLICATION_ID,
				System.getProperty(PropertiesUtil.APPLICATION_ID));

		PropertiesUtil.setConfigurationProperties(properties);

		eBayAPIUtil.loadeBayServiceClient();
	}

	@Test
	public void testGeteBaySearchResults() throws Exception {
		SearchQueryModel searchQueryModel = new SearchQueryModel(1, "eBay");

		List<SearchResultModel> eBaySearchResults =
			eBaySearchResult.geteBaySearchResults(searchQueryModel);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

}