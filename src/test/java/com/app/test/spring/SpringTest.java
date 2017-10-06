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

package com.app.test.spring;

import com.app.dao.CategoryDAO;
import com.app.dao.ReleaseDAO;
import com.app.dao.SearchQueryDAO;
import com.app.dao.SearchResultDAO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SpringTest {

	@Autowired
	private CategoryDAO categoryDAO;

	@Autowired
	private ReleaseDAO releaseDAO;

	@Autowired
	private SearchQueryDAO searchQueryDAO;

	@Autowired
	private SearchResultDAO searchResultDAO;

	@Test
	public void testCategoryDAO() {
		Assert.assertNotNull(categoryDAO);
	}

	@Test
	public void testReleaseDAO() {
		Assert.assertNotNull(releaseDAO);
	}

	@Test
	public void testSearchQueryDAO() {
		Assert.assertNotNull(searchQueryDAO);
	}

	@Test
	public void testSearchResultDAO() {
		Assert.assertNotNull(searchResultDAO);
	}

}