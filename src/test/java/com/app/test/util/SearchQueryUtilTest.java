/**
 * Copyright (c) 2015-present Jonathan McCann
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

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.test.BaseDatabaseTestCase;
import com.app.util.PropertiesUtil;
import com.app.util.SearchQueryUtil;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SearchQueryUtilTest extends BaseDatabaseTestCase {

	@Before
	public void doSetUp() throws Exception {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		_searchQueryDAOImpl = new SearchQueryDAOImpl();
	}

	@Test
	public void testIsExceedsTotalNumberOfSearchQueriesAllowed()
		throws Exception {

		Assert.assertFalse(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed());

		_searchQueryDAOImpl.addSearchQuery("First test search query");
		_searchQueryDAOImpl.addSearchQuery("Second test search query");

		Assert.assertTrue(
			SearchQueryUtil.isExceedsTotalNumberOfSearchQueriesAllowed());
	}

	private static SearchQueryDAOImpl _searchQueryDAOImpl;

}