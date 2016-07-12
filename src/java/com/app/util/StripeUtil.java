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

import com.app.exception.DatabaseConnectionException;
import com.app.model.User;

import com.stripe.model.Customer;
import com.stripe.model.Subscription;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
			userId, UserUtil.generateUnsubscribeToken(customerId),
			customerId, subscription.getId(), true, false);
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

	public static void resubscribe(String customerId, String subscriptionId)
		throws Exception {

		User user = UserUtil.getCurrentUser();

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("plan", PropertiesValues.STRIPE_SUBSCRIPTION_PLAN_ID);

		Subscription subscription = Subscription.retrieve(subscriptionId);

		if (subscription == null) {
			parameters.put("customer", customerId);

			subscription = Subscription.create(parameters);

			subscriptionId = subscription.getId();
		}
		else {
			subscription.update(parameters);
		}

		UserUtil.updateUserSubscription(
			user.getUserId(), user.getUnsubscribeToken(), customerId,
			subscriptionId, true, false);
	}

	public static void updateSubscription(String stripeToken)
		throws Exception {

		Map<String, Object> customerParams = new HashMap<>();

		customerParams.put("source", stripeToken);

		User user = UserUtil.getCurrentUser();

		Customer customer = Customer.retrieve(user.getCustomerId());

		customer.update(customerParams);
	}

	private static final SimpleDateFormat _NEXT_CHARGE_DATE_FORMAT =
		new SimpleDateFormat("MMMM d");

}