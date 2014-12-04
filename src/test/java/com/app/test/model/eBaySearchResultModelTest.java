package com.app.test.model;

import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResultModel;
import com.app.util.PropertiesUtil;
import com.app.util.eBayAPIUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class eBaySearchResultModelTest {

	@Before
	public void setUp() throws IOException {
		Properties properties = new Properties();

		properties.setProperty(
			PropertiesUtil.APPLICATION_ID,
				System.getProperty(PropertiesUtil.APPLICATION_ID));

		PropertiesUtil.setConfigurationProperties(properties);

		eBayAPIUtil.loadeBayServiceClient();
	}

	@Test
	public void testGeteBaySearchResults() throws Exception {
		List<String> searchQueries = new ArrayList<String>();

		searchQueries.add("ebay");

		List<SearchResultModel> eBaySearchResults =
			eBaySearchResultModel.geteBaySearchResults(searchQueries);

		Assert.assertEquals(5, eBaySearchResults.size());
	}

}