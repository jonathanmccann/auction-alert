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

import com.app.util.UserUtil;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class FaqController {

	@RequestMapping(value ="/account_faq", method = RequestMethod.GET)
	public String accountFaq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "account_faq";
	}

	@RequestMapping(value ="/faq", method = RequestMethod.GET)
	public String faq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "faq";
	}

	@RequestMapping(value ="/general_faq", method = RequestMethod.GET)
	public String generalFaq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "general_faq";
	}

	@RequestMapping(value ="/new_faq", method = RequestMethod.GET)
	public String newFaq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "new_faq";
	}

	@RequestMapping(value ="/query_faq", method = RequestMethod.GET)
	public String queryFaq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "query_faq";
	}

	@RequestMapping(value ="/result_faq", method = RequestMethod.GET)
	public String resultFaq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "result_faq";
	}

}