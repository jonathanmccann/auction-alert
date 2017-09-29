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

package com.app.controller;

import com.app.exception.DatabaseConnectionException;
import com.app.model.User;
import com.app.util.UserUtil;

import java.sql.SQLException;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class ErrorController {

	@RequestMapping(
		value = "/error", method = {RequestMethod.GET, RequestMethod.HEAD}
	)
	public String getError(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		model.put("isActive", UserUtil.isCurrentUserActive());

		return "error";
	}

}