package com.app.test.util;

import com.app.model.SearchResultModel;
import com.app.util.MailUtil;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.app.util.PropertiesUtil;
import freemarker.template.Template;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

/**
 * @author Jonathan McCann
 */
public class MailUtilTest {

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
	public void testGetEmailTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod("getEmailTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance);

		Assert.assertNotNull(template);
	}

	@Test
	public void testGetTextTemplate() throws Exception {
		Method method = _clazz.getDeclaredMethod("getTextTemplate");

		method.setAccessible(true);

		Template template = (Template)method.invoke(_classInstance);

		Assert.assertNotNull(template);
	}

	@Test
	public void testPopulateEmailMessage() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"populateEmailMessage", List.class, List.class, String.class,
			Session.class);

		method.setAccessible(true);

		List<SearchResultModel> searchResultModels = new ArrayList<>();

		Date endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg", endingTime, "Buy It Now");

		searchResultModels.add(searchResultModel);

		List<String> emailAddresses = new ArrayList<>();

		emailAddresses.add("test@test.com");
		emailAddresses.add("test2@test2.com");

		Session session = Session.getInstance(
			_properties,
			new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(
						_properties.getProperty(
							PropertiesUtil.OUTBOUND_EMAIL_ADDRESS),
						_properties.getProperty(
							PropertiesUtil.OUTBOUND_EMAIL_ADDRESS_PASSWORD));
				}
			});

		Message message = (Message)method.invoke(
			_classInstance, searchResultModels, emailAddresses, "test@test.com",
			session);

		Assert.assertEquals("test@test.com", message.getFrom()[0].toString());
		Assert.assertThat(
			message.getSubject(),
			CoreMatchers.containsString("New Search Results - "));
		Assert.assertEquals(
			"Item: itemTitle\nAuction Price: $14.99\nFixed Price: $29.99\n" +
				"URL: http://www.ebay.com/itm/1234\n",
			message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[2];

		internetAddresses[0] = new InternetAddress("test@test.com");
		internetAddresses[1] = new InternetAddress("test2@test2.com");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.CC));
	}

	@Test
	public void testPopulateTextMessage() throws Exception {
		Method method = _clazz.getDeclaredMethod(
			"populateTextMessage", List.class, List.class, Session.class);

		method.setAccessible(true);

		List<SearchResultModel> searchResultModels = new ArrayList<>();

		Date endingTime = new Date();

		SearchResultModel searchResultModel = new SearchResultModel(
			1, "1234", "itemTitle", 14.99, 29.99,"http://www.ebay.com/itm/1234",
			"http://www.ebay.com/123.jpg", endingTime, "Buy It Now");

		searchResultModels.add(searchResultModel);

		List<String> phoneNumberEmailAddresses = new ArrayList<>();

		phoneNumberEmailAddresses.add("1234567890@txt.att.net");
		phoneNumberEmailAddresses.add("2345678901@txt.att.net");

		Session session = Session.getInstance(
			_properties,
			new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(
						_properties.getProperty(
							PropertiesUtil.OUTBOUND_EMAIL_ADDRESS),
						_properties.getProperty(
							PropertiesUtil.OUTBOUND_EMAIL_ADDRESS_PASSWORD));
				}
			});

		Message message = (Message)method.invoke(
			_classInstance, searchResultModels, phoneNumberEmailAddresses,
			session);

		Assert.assertEquals(
			"itemTitle\nhttp://www.ebay.com/itm/1234\n",
			message.getContent());

		InternetAddress[] internetAddresses = new InternetAddress[2];

		internetAddresses[0] = new InternetAddress("1234567890@txt.att.net");
		internetAddresses[1] = new InternetAddress("2345678901@txt.att.net");

		Assert.assertArrayEquals(
			internetAddresses, message.getRecipients(Message.RecipientType.CC));
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

		method.invoke(_classInstance, recipientEmailAddresses);

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