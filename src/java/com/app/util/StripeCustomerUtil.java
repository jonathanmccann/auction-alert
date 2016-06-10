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

import com.app.dao.StripeCustomerDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.StripeCustomer;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class StripeCustomerUtil {

	public static void addCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_stripeCustomerDAO.addCustomer(userId, customerId, subscriptionId);
	}

	public static void deleteCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		_stripeCustomerDAO.deleteCustomer(userId);
	}

	public static StripeCustomer getCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		return _stripeCustomerDAO.getCustomer(userId);
	}

	public static void updateCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_stripeCustomerDAO.updateCustomer(userId, customerId, subscriptionId);
	}

	@Autowired
	public void setCustomerDAO(StripeCustomerDAO stripeCustomerDAO) {
		_stripeCustomerDAO = stripeCustomerDAO;
	}

	private static StripeCustomerDAO _stripeCustomerDAO;

}