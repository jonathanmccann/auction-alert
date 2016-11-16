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

package com.app.util;

import com.app.model.User;

import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventData;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class StripeUtil {

	public static void createSubscription(
			int userId, String emailAddress, String stripeToken)
		throws Exception {

		Map<String, Object> customerParams = new HashMap<>();

		customerParams.put("email", emailAddress);
		customerParams.put(
			"plan", PropertiesValues.STRIPE_SUBSCRIPTION_PLAN_ID);
		customerParams.put("source", stripeToken);

		Customer customer = Customer.create(customerParams);

		Subscription subscription =
			customer.getSubscriptions().getData().get(0);

		String customerId = customer.getId();

		UserUtil.updateUserSubscription(
			userId, UserUtil.generateUnsubscribeToken(customerId), customerId,
			subscription.getId(), true, false);
	}

	public static void deleteCustomer(String customerId) throws Exception {
		Customer customer = Customer.retrieve(customerId);

		customer.delete();
	}

	public static void deleteSubscription(String subscriptionId)
		throws Exception {

		Subscription subscription = Subscription.retrieve(subscriptionId);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("at_period_end", true);

		subscription.cancel(parameters);

		User user = UserUtil.getCurrentUser();

		UserUtil.updateUserSubscription(
			user.getUserId(), user.getUnsubscribeToken(), user.getCustomerId(),
			user.getSubscriptionId(), true, true);
	}

	public static String getNextChargeDate() throws Exception {
		User user = UserUtil.getCurrentUser();

		String subscriptionId = user.getSubscriptionId();

		if (ValidatorUtil.isNotNull(subscriptionId)) {
			Subscription subscription = Subscription.retrieve(subscriptionId);

			return _NEXT_CHARGE_DATE_FORMAT.format(
				new Date(subscription.getCurrentPeriodEnd() * 1000));
		}
		else {
			return "";
		}
	}

	public static void handleStripeEvent(String stripeJsonEvent)
		throws Exception {

		Event event = Event.GSON.fromJson(stripeJsonEvent, Event.class);

		event = Event.retrieve(event.getId());

		EventData eventData = event.getData();

		StripeObject stripeObject = eventData.getObject();

		if (event.getType().equals(_CHARGE_FAILED)) {
			Charge charge = (Charge)stripeObject;

			UserUtil.deactivateUser(charge.getCustomer());
		}
		else if (event.getType().equals(_CUSTOMER_SUBSCRIPTION_DELETED)) {
			Subscription subscription = (Subscription)stripeObject;

			UserUtil.deactivateUser(subscription.getCustomer());
		}
		else if (event.getType().equals(_INVOICE_PAYMENT_FAILED)) {
			Invoice invoice = (Invoice)stripeObject;

			UserUtil.deactivateUser(invoice.getCustomer());
		}
	}

	public static void resubscribe(
			String customerId, String subscriptionId, String stripeToken)
		throws Exception {

		Map<String, Object> customerParameters = new HashMap<>();

		customerParameters.put("source", stripeToken);

		Customer customer = Customer.retrieve(customerId);

		customer.update(customerParameters);

		Map<String, Object> subscriptionParameters = new HashMap<>();

		subscriptionParameters.put(
			"plan", PropertiesValues.STRIPE_SUBSCRIPTION_PLAN_ID);

		Subscription subscription = Subscription.retrieve(subscriptionId);

		if (subscription == null) {
			subscriptionParameters.put("customer", customerId);

			subscription = Subscription.create(subscriptionParameters);

			subscriptionId = subscription.getId();
		}
		else {
			subscription.update(subscriptionParameters);
		}

		User user = UserUtil.getCurrentUser();

		UserUtil.updateUserSubscription(
			user.getUserId(), user.getUnsubscribeToken(), customerId,
			subscriptionId, true, false);
	}

	public static void updateCustomerEmailAddress() throws Exception {
		User user = UserUtil.getCurrentUser();

		Customer customer = Customer.retrieve(user.getCustomerId());

		Map<String, Object> customerParams = new HashMap<>();

		customerParams.put("email", user.getEmailAddress());

		customer.update(customerParams);
	}

	public static void updateSubscription(String stripeToken, String customerId)
		throws Exception {

		Map<String, Object> customerParams = new HashMap<>();

		customerParams.put("source", stripeToken);

		Customer customer = Customer.retrieve(customerId);

		customer.update(customerParams);
	}

	private static final String _CHARGE_FAILED = "charge.failed";

	private static final String _CUSTOMER_SUBSCRIPTION_DELETED =
		"customer.subscription.deleted";

	private static final String _INVOICE_PAYMENT_FAILED =
		"invoice.payment_failed";

	private static final SimpleDateFormat _NEXT_CHARGE_DATE_FORMAT =
		new SimpleDateFormat("MMMM d");

}