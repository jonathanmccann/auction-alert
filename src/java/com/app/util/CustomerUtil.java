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

import com.app.dao.CustomerDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.Category;

import com.app.model.Customer;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class CustomerUtil {

	public static void addCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_customerDAO.addCustomer(userId, customerId, subscriptionId);
	}

	public static void deleteCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		_customerDAO.deleteCustomer(userId);
	}

	public static Customer getCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		return _customerDAO.getCustomer(userId);
	}

	public static void updateCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_customerDAO.updateCustomer(userId, customerId, subscriptionId);
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		_customerDAO = customerDAO;
	}

	private static CustomerDAO _customerDAO;

}