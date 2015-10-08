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

package com.app.test.spring;

import com.app.dao.CategoryDAO;
import com.app.dao.impl.ReleaseDAOImpl;
import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.dao.impl.SearchQueryPreviousResultDAOImpl;
import com.app.dao.impl.SearchResultDAOImpl;
import org.junit.Assert;
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
public class SpringTest {

	@Autowired
	CategoryDAO categoryDAO;

	@Autowired
	ReleaseDAOImpl releaseDAOImpl;

	@Autowired
	SearchQueryDAOImpl searchQueryDAOImpl;

	@Autowired
	SearchQueryPreviousResultDAOImpl searchQueryPreviousResultDAOImpl;

	@Autowired
	SearchResultDAOImpl searchResultDAOImpl;

	@Test
	public void testCategoryDAO() {
		Assert.assertNotNull(categoryDAO);
	}

	@Test
	public void testReleaseDAOImpl() {
		Assert.assertNotNull(releaseDAOImpl);
	}

	@Test
	public void testSearchQueryDAOImpl() {
		Assert.assertNotNull(searchQueryDAOImpl);
	}

	@Test
	public void testSearchQueryPreviousResultDAOImpl() {
		Assert.assertNotNull(searchQueryPreviousResultDAOImpl);
	}

	@Test
	public void testSearchResultDAOImpl() {
		Assert.assertNotNull(searchResultDAOImpl);
	}
}