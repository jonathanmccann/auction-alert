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

package com.app.constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class AccountConstants {

	public static Map<Integer, String> getHours() {
		return _hours;
	}

	public static Map<String, String> getMobileCarrierSuffixes() {
		return _mobileCarrierSuffixes;
	}

	public static List<String> getMobileOperatingSystems() {
		return _mobileOperatingSystems;
	}

	public static Map<String, String> getTimeZones() {
		return _timeZones;
	}

	private static final Map<Integer, String> _hours =
		new LinkedHashMap<Integer, String>() {
			{
				put(1, "12 AM");
				put(2, "1 AM");
				put(3, "2 AM");
				put(4, "3 AM");
				put(5, "4 AM");
				put(6, "5 AM");
				put(7, "6 AM");
				put(8, "7 AM");
				put(9, "8 AM");
				put(10, "9 AM");
				put(11, "10 AM");
				put(12, "11 AM");
				put(13, "12 PM");
				put(14, "1 PM");
				put(15, "2 PM");
				put(16, "3 PM");
				put(17, "4 PM");
				put(18, "5 PM");
				put(19, "6 PM");
				put(20, "7 PM");
				put(21, "8 PM");
				put(22, "9 PM");
				put(23, "10 PM");
				put(24, "11 PM");
			}
		};
	private static final Map<String, String> _mobileCarrierSuffixes =
		new LinkedHashMap<String, String>() {
			{
				put("@message.alltel.com", "Alltel");
				put("@txt.att.net", "AT&T");
				put("@myboostmobile.com", "Boost Mobile");
				put("@sms.mycricket.com", "Cricket");
				put("@mymetropcs.com", "Metro PCS");
				put("@ptel.com", "Ptel");
				put("@messaging.sprintpcs.com", "Sprint");
				put("@tmomail.net", "T-Mobile");
				put("@mmst5.tracfone.com", "Tracfone");
				put("@email.uscc.net", "U.S. Cellular");
				put("@vtext.com", "Verizon");
				put("@vmobl.com", "Virgin Mobile");
			}
		};
	private static final List<String> _mobileOperatingSystems =
		new ArrayList<String>() {
			{
				add("Android");
				add("iOS");
				add("Other");
			}
		};
	private static final Map<String, String> _timeZones =
		new LinkedHashMap<String, String>() {
			{
				put("Pacific/Honolulu", "HST");
				put("America/Anchorage", "AST");
				put("America/Los_Angeles", "PST");
				put("America/Denver", "MST");
				put("America/Phoenix", "PNT");
				put("America/Chicago", "CST");
				put("America/New_York", "EST");
				put("America/Indiana/Indianapolis", "IET");
			}
		};

}