<%--
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
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>My Account</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
	</head>
	<body>
		<div>
			<form:form commandName="userDetails" id="updateUserForm" method="post">
				<form:input path="user.userId" type="hidden" value="${user.userId}" />
				<form:input path="notificationPreferences.userId" type="hidden" value="${user.userId}" />

				<h2>My Account</h2>
				<div>
					<h3>My Details:</h3>

					Email Address <form:input path="user.emailAddress" value="${user.emailAddress}" />

					<c:if test="${not empty duplicateEmailAddressException}">
						${duplicateEmailAddressException}
					</c:if>

					<c:if test="${not empty invalidEmailAddressException}">
						${invalidEmailAddressException}
					</c:if>
				</div>
				<hr>
				<div>
					<h3>Mobile Details:</h3>

					<div style="display: table">
						Phone Number <form:input path="user.phoneNumber" value="${user.phoneNumber}" />

						<c:if test="${not empty invalidPhoneNumberException}">
							${invalidPhoneNumberException}</br>
						</c:if> <br> <br>

						Mobile Operating System
						<form:select path="user.mobileOperatingSystem" value="${user.mobileOperatingSystem}">
							<form:options items="${mobileOperatingSystems}" />
						</form:select> <br>
						Mobile Carrier
						<form:select path="user.mobileCarrierSuffix" value="${user.mobileCarrierSuffix}">
							<form:options items="${mobileCarrierSuffixes}" />
						</form:select>
					</div>
				</div>
				<hr>
				<div>
					<h3>Notification Details:</h3>

					<b>Customize notifications based on time:</b> <form:checkbox id="basedOnTime" path="notificationPreferences.basedOnTime" value="${notificationPreferences.basedOnTime}" /> <br>

					<div id="notificationOptions">
						<b>Receive notifications via:</b> <br>
						<form:checkbox label="Email" path="notificationPreferences.emailNotification" value="${notificationPreferences.emailNotification}" /> <br>
						<form:checkbox label="Text" path="notificationPreferences.textNotification" value="${notificationPreferences.textNotification}" />
					</div>

					<div id="basedOnTimeOptions">
						<b>Set your current time zone:</b> <br>
						Time Zone
						<form:select path="notificationPreferences.timeZone" value="${notificationPreferences.timeZone}">
							<form:options items="${timeZones}" />
						</form:select>
						<br>
						<br>

						<b>Set times to specify day and night time frames:</b> <br>
						Start of Day
						<form:select path="notificationPreferences.startOfDay" value="${notificationPreferences.startOfDay}">
							<form:options items="${hours}" />
						</form:select> <br>
						End of Day
						<form:select path="notificationPreferences.endOfDay" value="${notificationPreferences.endOfDay}">
							<form:options items="${hours}" />
						</form:select>
						<br>
						<br>

						<b>Delivery method during time frames:</b>
						<table>
							<tr>
								<td></td>
								<td>Email</td>
								<td>Text</td>
							</tr>
							<tr>
								<td>Weekday Day Notification</td>
								<td><form:checkbox path="notificationPreferences.weekdayDayEmailNotification" value="${notificationPreferences.weekdayDayEmailNotification}" /></td>
								<td><form:checkbox path="notificationPreferences.weekdayDayTextNotification" value="${notificationPreferences.weekdayDayTextNotification}" /></td>
							</tr>
							<tr>
								<td>Weekday Night Notification</td>
								<td><form:checkbox path="notificationPreferences.weekdayNightEmailNotification" value="${notificationPreferences.weekdayNightEmailNotification}" /></td>
								<td><form:checkbox path="notificationPreferences.weekdayNightTextNotification" value="${notificationPreferences.weekdayNightTextNotification}" /></td>
							</tr>
							<tr>
								<td>Weekend Day Notification</td>
								<td><form:checkbox path="notificationPreferences.weekendDayEmailNotification" value="${notificationPreferences.weekendDayEmailNotification}" /></td>
								<td><form:checkbox path="notificationPreferences.weekendDayTextNotification" value="${notificationPreferences.weekendDayTextNotification}" /></td>
							</tr>
							<tr>
								<td>Weekend Night Notification</td>
								<td><form:checkbox path="notificationPreferences.weekendNightEmailNotification" value="${notificationPreferences.weekendNightEmailNotification}" /></td>
								<td><form:checkbox path="notificationPreferences.weekendNightTextNotification" value="${notificationPreferences.weekendNightTextNotification}" /></td>
							</tr>
						</table>
					</div>
				</div>
				<div>
					<input id="updateUserSubmit" type="submit" value="Update User" />
				</div>
			</form:form>
			<div align="center">
				<a href="add_search_query">Add a Search Query</a> | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a> <br> <br>
				My Account | <a href="log_out">Log Out</a>
			</div>
		</div>
	</body>
</html>