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

package com.app;

import com.app.util.SearchResultUtil;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Jonathan McCann
 */
@EnableScheduling
public class eBaySearch {

	@Scheduled(fixedRate = 300000)
	public static void main() {
		try {
			SearchResultUtil.performSearch();
		}
		catch (SQLException sqle) {
			_log.error("Unable to perform eBay search: " + sqle.getMessage());
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearch.class);

}