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

package com.app.model;

/**
 * @author Jonathan McCann
 */
public class StripeCustomer {

	public StripeCustomer() {
	}

	public StripeCustomer(
		int userId, String customerId, String subscriptionId) {

		_userId = userId;
		_customerId = customerId;
		_subscriptionId = subscriptionId;
	}

	public int getUserId() {
		return _userId;
	}

	public String getCustomerId() {
		return _customerId;
	}

	public String getSubscriptionId() {
		return _subscriptionId;
	}

	public void setUserId(int userId) {
		_userId = userId;
	}

	public void setCustomerId(String customerId) {
		_customerId = customerId;
	}

	public void setSubscriptionId(String subscriptionId) {
		_subscriptionId = subscriptionId;
	}

	private int _userId;

	private String _customerId;
	private String _subscriptionId;

}