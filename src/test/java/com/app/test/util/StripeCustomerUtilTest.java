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

import com.app.model.StripeCustomer;
import com.app.test.BaseTestCase;
import com.app.util.StripeCustomerUtil;

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
public class StripeCustomerUtilTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		setUpDatabase();
	}

	@Test
	public void testAddCustomer() throws Exception {
		StripeCustomerUtil.addCustomer(_USER_ID, "customerId", "subscriptionId");

		StripeCustomer stripeCustomer = StripeCustomerUtil.getCustomer(_USER_ID);

		Assert.assertEquals(_USER_ID, stripeCustomer.getUserId());
		Assert.assertEquals("customerId", stripeCustomer.getCustomerId());
		Assert.assertEquals("subscriptionId", stripeCustomer.getSubscriptionId());
	}

	@Test(expected = SQLException.class)
	public void testDeleteCustomer() throws Exception {
		StripeCustomerUtil.addCustomer(_USER_ID, "customerId", "subscriptionId");

		StripeCustomerUtil.deleteCustomer(_USER_ID);

		StripeCustomerUtil.getCustomer(_USER_ID);
	}

	@Test
	public void testUpdateCustomer() throws Exception {
		StripeCustomerUtil.addCustomer(_USER_ID, "customerId", "subscriptionId");

		StripeCustomer stripeCustomer = StripeCustomerUtil.getCustomer(_USER_ID);

		Assert.assertEquals(_USER_ID, stripeCustomer.getUserId());
		Assert.assertEquals("customerId", stripeCustomer.getCustomerId());
		Assert.assertEquals("subscriptionId", stripeCustomer.getSubscriptionId());

		StripeCustomerUtil.updateCustomer(
			_USER_ID, "newCustomerId", "newSubscriptionId");

		stripeCustomer = StripeCustomerUtil.getCustomer(_USER_ID);

		Assert.assertEquals(_USER_ID, stripeCustomer.getUserId());
		Assert.assertEquals("newCustomerId", stripeCustomer.getCustomerId());
		Assert.assertEquals("newSubscriptionId", stripeCustomer.getSubscriptionId());
	}

}