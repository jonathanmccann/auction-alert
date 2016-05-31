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

package com.app.test.mail;

import com.app.mail.MailUtil;
import com.app.test.BaseTestCase;

import freemarker.template.Template;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class MailUtilTest extends BaseTestCase {

	@Test
	public void testGetEmailTemplate() throws Exception {
		Class clazz = Class.forName(MailUtil.class.getName());

		Object classInstance = clazz.newInstance();

		Method method = clazz.getDeclaredMethod("getEmailTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(classInstance);

		Assert.assertNotNull(template);
		Assert.assertEquals("email_body.ftl", template.getName());
	}

}