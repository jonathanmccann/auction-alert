package com.app.test.util;

import com.app.util.MailUtil;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.app.util.PropertiesUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class MailUtilTest extends MailUtil {

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = getClass();

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());

		_properties = PropertiesUtil.getConfigurationProperties();

		_clazz = Class.forName(MailUtil.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@Test
	public void testConvertPhoneNumbersToEmailAddresses() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"convertPhoneNumbersToEmailAddresses",
			List.class, Properties.class);

		method.setAccessible(true);

		List<String> recipientPhoneNumbers = new ArrayList<>();

		recipientPhoneNumbers.add("1234567890");
		recipientPhoneNumbers.add("2345678901");

		method.invoke(_classInstance, recipientPhoneNumbers, _properties);

		Assert.assertEquals(
			"1234567890@txt.att.net", recipientPhoneNumbers.get(0));
		Assert.assertEquals(
			"2345678901@txt.att.net", recipientPhoneNumbers.get(1));
	}

	@Test
	public void testGetRecipientEmailAddresses() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"getRecipientEmailAddresses", Properties.class);

		method.setAccessible(true);

		List<String> recipientEmailAddresses =
			(List<String>)method.invoke(_classInstance,  _properties);

		Assert.assertEquals(
			"test@test.com", recipientEmailAddresses.get(0));
		Assert.assertEquals(
			"test2@test2.com", recipientEmailAddresses.get(1));
	}

	@Test
	public void testGetRecipientPhoneNumbers() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"getRecipientPhoneNumbers", Properties.class);

		method.setAccessible(true);

		List<String> recipientEmailAddresses =
			(List<String>)method.invoke(_classInstance,  _properties);

		Assert.assertEquals(
			"1234567890@txt.att.net", recipientEmailAddresses.get(0));
		Assert.assertEquals(
			"2345678901@txt.att.net", recipientEmailAddresses.get(1));
	}

	@Test
	public void testValidateEmailAddresses() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"validateEmailAddresses", List.class);

		method.setAccessible(true);

		List<String> recipientEmailAddresses = new ArrayList<>();

		recipientEmailAddresses.add("test@test.com");
		recipientEmailAddresses.add("invalidEmailAddress");
		recipientEmailAddresses.add("test2@test2.com");

		method.invoke(_classInstance,  recipientEmailAddresses);

		Assert.assertEquals(2, recipientEmailAddresses.size());
		Assert.assertEquals(
			"test@test.com", recipientEmailAddresses.get(0));
		Assert.assertEquals(
			"test2@test2.com", recipientEmailAddresses.get(1));
	}

	@Test
	public void testValidatePhoneNumbers() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"validatePhoneNumbers", List.class);

		method.setAccessible(true);

		List<String> recipientPhoneNumbers = new ArrayList<>();

		recipientPhoneNumbers.add("1234567890");
		recipientPhoneNumbers.add("1234");
		recipientPhoneNumbers.add("2345678901");
		recipientPhoneNumbers.add("test");

		method.invoke(_classInstance, recipientPhoneNumbers);

		Assert.assertEquals(2, recipientPhoneNumbers.size());
		Assert.assertEquals(
			"1234567890", recipientPhoneNumbers.get(0));
		Assert.assertEquals(
			"2345678901", recipientPhoneNumbers.get(1));
	}

	private static Properties _properties;

	private static Class _clazz;

	private static Object _classInstance;

}