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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class UtilityController {

	@RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
	public String getFavicon() {
		return "forward:resources/images/favicon.ico";
	}

	@RequestMapping(value = "/robots.txt", method = RequestMethod.GET)
	public String getRobots() {
		return "forward:resources/robots.txt";
	}

	@RequestMapping(value = "/sitemap.xml", method = RequestMethod.GET)
	public String getSitemap() {
		return "forward:resources/sitemap.xml";
	}

}