package com.app.test;

import com.app.controller.eBayController;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import org.junit.Assert;

import java.util.Map;

public class eBayControllerTest {

	@Test
    public void testDefaultView() throws Exception{
        eBayController controller = new eBayController();

        ModelAndView modelAndView = controller.defaultLandingPage();

        Assert.assertEquals("default", modelAndView.getViewName());
    }

	@Test
	public void testHelloUserView() throws Exception{
        eBayController controller = new eBayController();

        ModelAndView modelAndView = controller.helloUser();

        Assert.assertEquals("hello_ebay_user", modelAndView.getViewName());
    }

	@Test
	public void testHelloUserViewMessage() throws Exception{
        eBayController controller = new eBayController();

        ModelAndView modelAndView = controller.helloUser();

		Map<String, Object> modelMap = modelAndView.getModel();

        Assert.assertEquals("Welcome, eBay User!", modelMap.get("message"));
    }
}