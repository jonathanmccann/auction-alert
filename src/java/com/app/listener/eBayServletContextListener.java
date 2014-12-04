package com.app.listener;

import com.app.util.PropertiesUtil;
import com.app.util.eBayAPIUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class eBayServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		System.out.println("ServletContextListener destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
		throws RuntimeException {

		System.out.println("ServletContextListener started");

		try {
			PropertiesUtil.loadConfigurationProperties();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		eBayAPIUtil.loadeBayServiceClient();
	}
}