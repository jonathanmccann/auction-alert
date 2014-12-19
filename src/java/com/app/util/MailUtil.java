package com.app.util;

import com.app.model.SearchResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

			Message message = populateMessage(
				searchResultModels, session.getProperty("username"), properties,
				session);

			Transport.send(message);
		}
		catch (Exception e) {
			_log.error(
				"Unable to send search result to recipients", e.getCause());
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

		List<String> recipientEmailAddressesList =
			Arrays.asList(recipientEmailAddresses.split(","));

		validateEmailAddresses(recipientEmailAddressesList);

		return recipientEmailAddressesList;
	}

	private static List<String> getRecipientPhoneNumbers(
		Properties properties) {

		String recipientPhoneNumbers =
			properties.getProperty(PropertiesUtil.RECIPIENT_PHONE_NUMBERS);

		List<String> recipientPhoneNumbersList =
			Arrays.asList(recipientPhoneNumbers.split(","));

		validatePhoneNumbers(recipientPhoneNumbersList);

		convertPhoneNumbersToEmailAddresses(
			recipientPhoneNumbersList, properties);

		return recipientPhoneNumbersList;
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

	private static Message populateMessage(
			List<SearchResultModel> searchResultModels, String emailFrom,
			Properties properties, Session session)
		throws Exception {

		List<String> recipientEmailAddress = getRecipientEmailAddresses(
			properties);
		List<String> recipientPhoneNumbers = getRecipientPhoneNumbers(
			properties);

		return new MimeMessage(session);
	}

	private static Pattern _emailAddressPattern = Pattern.compile(
		"[a-zA-Z0-9]*@[a-zA-Z0-9]*\\.[a-zA-Z]{1,6}");

	private static Pattern _phoneNumberPattern = Pattern.compile(
		"[0-9]{10,10}");

	private static final Logger _log = LoggerFactory.getLogger(
		MailUtil.class);

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
