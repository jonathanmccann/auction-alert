package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Jonathan McCann
 */
@Controller
public class eBayController {

	@RequestMapping("/default")
	public ModelAndView defaultLandingPage() {
		return new ModelAndView("default");
	}

	@RequestMapping("/hello_ebay_user")
	public ModelAndView helloUser() {
		String message = "Welcome, eBay User!";

		return new ModelAndView("hello_ebay_user", "message", message);
	}

}