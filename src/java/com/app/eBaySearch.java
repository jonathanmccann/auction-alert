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
			_log.error("Unable to get all of the search queries");
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		eBaySearch.class);

}