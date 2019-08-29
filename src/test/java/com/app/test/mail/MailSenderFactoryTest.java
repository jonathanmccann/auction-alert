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

import com.app.mail.DefaultMailSender;
import com.app.mail.MailSender;
import com.app.mail.MailSenderFactory;
import com.app.mail.SendGridMailSender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-active-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class MailSenderFactoryTest {

	@Test
	public void testSetMailSender() throws Exception {
		MailSenderFactory mailSenderFactory = new MailSenderFactory();

		MailSender mailSender = mailSenderFactory.getInstance();

		Assert.assertTrue(mailSender instanceof DefaultMailSender);

		mailSenderFactory.setMailSender(new SendGridMailSender());

		mailSender = mailSenderFactory.getInstance();

		Assert.assertTrue(mailSender instanceof SendGridMailSender);
	}

}