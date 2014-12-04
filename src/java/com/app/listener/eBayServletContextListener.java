package com.app.listener;

import com.app.util.PropertiesUtil;
import com.app.util.eBayAPIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class eBayServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		_log.info("Destroying servlet context");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
		throws RuntimeException {

		System.out.println("ServletContextListener started");

		_log.info("Initializing servlet context");

		try {
			_log.info("Loading configuration properties");

			PropertiesUtil.loadConfigurationProperties();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		_log.info("Loading eBay service client");

		eBayAPIUtil.loadeBayServiceClient();
	}

	private static Logger _log = LoggerFactory.getLogger(
		eBayServletContextListener.class);
}