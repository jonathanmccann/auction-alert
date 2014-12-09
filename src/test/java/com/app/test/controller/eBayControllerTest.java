package com.app.test.controller;

import com.app.controller.eBayController;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author Jonathan McCann
 */
public class eBayControllerTest {

	@Before
	public void setUp() {
		_controller = new eBayController();
	}

	@Test
	public void testDefaultView() throws Exception {
		ModelAndView modelAndView = _controller.defaultLandingPage();

		Assert.assertEquals("default", modelAndView.getViewName());
	}

	@Test
	public void testHelloUserView() throws Exception {
		ModelAndView modelAndView = _controller.helloUser();

		Assert.assertEquals("hello_ebay_user", modelAndView.getViewName());
	}

	@Test
	public void testHelloUserViewMessage() throws Exception {
		ModelAndView modelAndView = _controller.helloUser();

		Map<String, Object> modelMap = modelAndView.getModel();

		Assert.assertEquals("Welcome, eBay User!", modelMap.get("message"));
	}

	private static eBayController _controller;

}