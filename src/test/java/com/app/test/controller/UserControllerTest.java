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

package com.app.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.UserUtil;

import com.stripe.model.Customer;

import com.stripe.model.CustomerSubscriptionCollection;
import com.stripe.model.Subscription;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@PrepareForTest(Customer.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpDatabase();

		_USER = UserUtil.addUser("test@test.com", "password");
	}

	@Test
	public void testCreateSubscription()
		throws Exception {

		setUpCustomer();
		setUpUserUtil();
		setUpProperties();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeDoesNotExist("paymentException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("userActiveException"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testCreateSubscriptionWithActiveUser()
		throws Exception {

		setUpUserUtil();

		_USER.setActive(true);

		UserUtil.updateUser(_USER);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeExists("userActiveException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("paymentException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
	}

	@Test
	public void testCreateSubscriptionWithInvalidEmailAddress()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test2@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeExists("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("paymentException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("userActiveException"));
	}

	@Test
	public void testCreateSubscriptionWithInvalidStripeToken()
		throws Exception {

		setUpUserUtil();
		setUpProperties();

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("paymentException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("userActiveException"));
	}

	@Test
	public void testUpdateMyAccount() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));

		_assertUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithDuplicateEmailAddress()
		throws Exception {

		setUpUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeExists("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidEmailAddress() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		request.param("emailAddress", "test");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeExists("invalidEmailAddressException"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testViewMyAccount() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/my_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"));
	}

	protected static void setUpCustomer() throws Exception {
		Customer customer = new Customer();

		customer.setId("customerId");

		CustomerSubscriptionCollection customerSubscriptionCollection =
			new CustomerSubscriptionCollection();

		List<Subscription> subscriptions = new ArrayList<>();

		Subscription subscription = new Subscription();

		subscription.setId("subscriptionId");

		subscriptions.add(subscription);

		customerSubscriptionCollection.setData(subscriptions);

		customer.setSubscriptions(customerSubscriptionCollection);

		PowerMockito.spy(Customer.class);

		PowerMockito.doReturn(
			customer
		).when(
			Customer.class, "create", Mockito.anyMap()
		);
	}
	private void _assertNotUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_USER.getUserId());

		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertTrue(user.isEmailNotification());
	}

	private void _assertUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_USER.getUserId());

		Assert.assertEquals("test2@test.com", user.getEmailAddress());
		Assert.assertFalse(user.isEmailNotification());
	}

	private MockHttpServletRequestBuilder _buildUpdateMyAccountRequest() {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/my_account");

		request.param("userId", String.valueOf(_USER.getUserId()));
		request.param("emailAddress", "test2@test.com");
		request.param("emailNotification", "false");

		return request;
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private static User _USER;

}