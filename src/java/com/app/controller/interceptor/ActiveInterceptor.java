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

package com.app.controller.interceptor;

import com.app.language.LanguageUtil;
import com.app.model.User;
import com.app.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

/**
 * @author Jonathan McCann
 */
@Component
public class ActiveInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response,
			Object handler)
		throws Exception {

		User currentUser = UserUtil.getCurrentUser();

		if (!currentUser.isActive()) {
			WebUtils.setSessionAttribute(
				request, "info",
				LanguageUtil.getMessage("resubscribe-for-access"));

			response.sendRedirect("my_account");

			return false;
		}

		return true;
	}

}