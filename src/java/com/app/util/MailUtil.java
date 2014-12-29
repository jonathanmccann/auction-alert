package com.app.util;

import com.app.model.SearchResultModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailUtil {

	public static void sendSearchResultsToRecipients(
		List<SearchResultModel> searchResultModels) {

		try {
			_log.info("Sending {} search results", searchResultModels.size());

			Properties properties = PropertiesUtil.getConfigurationProperties();

			Session session = authenticateOutboundEmailAddress(properties);

			List<String> recipientEmailAddresses = getRecipientEmailAddresses(
				properties);

			if (recipientEmailAddresses.size() > 0) {
				Message emailMessage = populateEmailMessage(
					searchResultModels, recipientEmailAddresses,
					session.getProperty(PropertiesUtil.OUTBOUND_EMAIL_ADDRESS),
					session);

				Transport.send(emailMessage);
			}

			List<String> recipientPhoneNumbers = getRecipientPhoneNumbers(
				properties);

			if (recipientPhoneNumbers.size() > 0) {
				Message textMessage = populateTextMessage(
					searchResultModels, recipientPhoneNumbers, session);

				Transport.send(textMessage);
			}
		}
		catch (Exception e) {
			_log.error(
				"Unable to send search result to recipients: " + e.getMessage());
		}
	}

	private static Session authenticateOutboundEmailAddress(
		final Properties properties) {

		return Session.getInstance(
			properties,
			new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(
						properties.getProperty(
							PropertiesUtil.OUTBOUND_EMAIL_ADDRESS),
						properties.getProperty(
							PropertiesUtil.OUTBOUND_EMAIL_ADDRESS_PASSWORD));
				}
			});
	}

	private static void convertPhoneNumbersToEmailAddresses(
		List<String> recipientPhoneNumbers, Properties properties) {

		String phoneCarrier =
			properties.getProperty(PropertiesUtil.RECIPIENT_PHONE_CARRIER);

		String phoneCarrierEmailSuffix = _CARRIER_SUFFIX_MAP.get(phoneCarrier);

		for (int i = 0; i < recipientPhoneNumbers.size(); i++) {
			recipientPhoneNumbers.set(
				i, recipientPhoneNumbers.get(i) + phoneCarrierEmailSuffix);
		}
	}

	private static List<String> getRecipientEmailAddresses(
		Properties properties) {

		String recipientEmailAddresses =
			properties.getProperty(PropertiesUtil.RECIPIENT_EMAIL_ADDRESSES);

		if ((recipientEmailAddresses != null) &&
			(!recipientEmailAddresses.equals(""))) {

			List<String> recipientEmailAddressesList =
				Arrays.asList(recipientEmailAddresses.split(","));

			validateEmailAddresses(recipientEmailAddressesList);

			return recipientEmailAddressesList;
		}
		else {
			return new ArrayList<>();
		}
	}

	private static List<String> getRecipientPhoneNumbers(
		Properties properties) {

		String recipientPhoneNumbers =
			properties.getProperty(PropertiesUtil.RECIPIENT_PHONE_NUMBERS);

		if ((recipientPhoneNumbers != null) &&
			(!recipientPhoneNumbers.equals(""))) {

			List<String> recipientPhoneNumbersList =
				Arrays.asList(recipientPhoneNumbers.split(","));

			validatePhoneNumbers(recipientPhoneNumbersList);

			convertPhoneNumbersToEmailAddresses(
				recipientPhoneNumbersList, properties);

			return recipientPhoneNumbersList;
		}
		else {
			return new ArrayList<>();
		}
	}

	private static void validateEmailAddresses(
		List<String> recipientEmailAddressesArray) {

		for (int i = 0; i < recipientEmailAddressesArray.size(); i++) {
			Matcher matcher = _emailAddressPattern.matcher(
				recipientEmailAddressesArray.get(i));

			if (!matcher.matches()) {
				_log.debug(
					"{} is not a valid email address",
					recipientEmailAddressesArray.get(i));

				recipientEmailAddressesArray.remove(i);
			}
		}
	}

	private static void validatePhoneNumbers(
		List<String> recipientPhoneNumbersArray) {

		for (int i = 0; i < recipientPhoneNumbersArray.size(); i++) {
			Matcher matcher = _phoneNumberPattern.matcher(
				recipientPhoneNumbersArray.get(i));

			if (!matcher.matches()) {
				_log.debug(
					"{} is not a valid phone number",
					recipientPhoneNumbersArray.get(i));

				recipientPhoneNumbersArray.remove(i);
			}
		}
	}

	private static Template getEmailTemplate() throws IOException {
		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		return _configuration.getTemplate("/email_body.ftl");
	}

	private static Template getTextTemplate() throws IOException {
		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		return _configuration.getTemplate("/text_body.ftl");
	}

	private static Message populateEmailMessage(
			List<SearchResultModel> searchResultModels,
			List<String> recipientEmailAddresses, String emailFrom,
			Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(emailFrom));

		for (String recipientEmailAddress : recipientEmailAddresses) {
			message.addRecipient(
				Message.RecipientType.CC,
				new InternetAddress(recipientEmailAddress));
		}

		message.setSubject(
			"New Search Results - " +
				_DATE_FORMAT.format(new Date()));

		populateMessage(searchResultModels, message, getEmailTemplate());

		return message;
	}

	private static Message populateTextMessage(
			List<SearchResultModel> searchResultModels,
			List<String> recipientPhoneNumbers, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		for (String recipientPhoneNumber : recipientPhoneNumbers) {
			message.addRecipient(
				Message.RecipientType.CC,
				new InternetAddress(recipientPhoneNumber));
		}

		populateMessage(searchResultModels, message, getTextTemplate());

		return message;
	}

	private static void populateMessage(
			List<SearchResultModel> searchResultModels, Message message,
			Template template)
		throws Exception {

		Map<String, Object> rootMap = new HashMap<String, Object>();

		rootMap.put("searchResultModels", searchResultModels);

		StringWriter stringWriter = new StringWriter();

		template.process(rootMap, stringWriter);

		message.setText(stringWriter.toString());
	}

	private static Configuration _configuration = new Configuration(
		Configuration.VERSION_2_3_21);

	private static Pattern _emailAddressPattern = Pattern.compile(
		"[a-zA-Z0-9]*@[a-zA-Z0-9]*\\.[a-zA-Z]{1,6}");

	private static Pattern _phoneNumberPattern = Pattern.compile(
		"[0-9]{10,10}");

	private static final Logger _log = LoggerFactory.getLogger(
		MailUtil.class);

	private static final DateFormat _DATE_FORMAT =
		new SimpleDateFormat("MM/dd/yyyy");

	private static final Map<String, String> _CARRIER_SUFFIX_MAP =
		new HashMap<>();

	static {
		_CARRIER_SUFFIX_MAP.put("AT&T", "@txt.att.net");
		_CARRIER_SUFFIX_MAP.put("T-Mobile", "@tmomail.net");
		_CARRIER_SUFFIX_MAP.put("Verizon", "@vtext.com");
		_CARRIER_SUFFIX_MAP.put("Sprint", "@messaging.sprintpcs.com");
		_CARRIER_SUFFIX_MAP.put("Virgin Mobile", "@vmobl.com");
		_CARRIER_SUFFIX_MAP.put("Tracfone", "@mmst5.tracfone.com");
		_CARRIER_SUFFIX_MAP.put("Metro PCS", "@mymetropcs.com");
		_CARRIER_SUFFIX_MAP.put("Boost Mobile", "@myboostmobile.com");
		_CARRIER_SUFFIX_MAP.put("Cricket", "@sms.mycricket.com");
		_CARRIER_SUFFIX_MAP.put("Alltel", "@message.alltel.com");
		_CARRIER_SUFFIX_MAP.put("Ptel", "@ptel.com");
		_CARRIER_SUFFIX_MAP.put("U.S. Cellular", "@email.uscc.net");
	}
}
