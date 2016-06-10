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

import com.app.model.Customer;
import com.app.test.BaseTestCase;
import com.app.util.CustomerUtil;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomerUtilTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpDatabase();
	}

	@Test
	public void testAddCustomer() throws Exception {
		CustomerUtil.addCustomer(_USER_ID, "customerId", "subscriptionId");

		Customer customer = CustomerUtil.getCustomer(_USER_ID);

		Assert.assertEquals(_USER_ID, customer.getUserId());
		Assert.assertEquals("customerId", customer.getCustomerId());
		Assert.assertEquals("subscriptionId", customer.getSubscriptionId());
	}

	@Test(expected = SQLException.class)
	public void testDeleteCustomer() throws Exception {
		CustomerUtil.addCustomer(_USER_ID, "customerId", "subscriptionId");

		CustomerUtil.deleteCustomer(_USER_ID);

		CustomerUtil.getCustomer(_USER_ID);
	}

	@Test
	public void testUpdateCustomer() throws Exception {
		CustomerUtil.addCustomer(_USER_ID, "customerId", "subscriptionId");

		Customer customer = CustomerUtil.getCustomer(_USER_ID);

		Assert.assertEquals(_USER_ID, customer.getUserId());
		Assert.assertEquals("customerId", customer.getCustomerId());
		Assert.assertEquals("subscriptionId", customer.getSubscriptionId());

		CustomerUtil.updateCustomer(
			_USER_ID, "newCustomerId", "newSubscriptionId");

		customer = CustomerUtil.getCustomer(_USER_ID);

		Assert.assertEquals(_USER_ID, customer.getUserId());
		Assert.assertEquals("newCustomerId", customer.getCustomerId());
		Assert.assertEquals("newSubscriptionId", customer.getSubscriptionId());
	}

}