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
		<title>Home</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
	</head>
	<body>
		<div>
			<h1>eBay Searcher</h1>
			<shiro:guest>
				<c:if test="${not empty authenticationError}">
					${authenticationError}</br>
				</c:if>
				<form:form action="log_in" commandName="logIn" method="post">
					<div>
						<b>Email Address: </b><input id="emailAddress" name="emailAddress" /></br>
						<b>Password: </b><input id="password" name="password" type="password" />
					</div>
					<div>
						<input type="submit" value="Log In" />
					</div>
				</form:form>
			</shiro:guest>
			<shiro:user>
				<h3>View and update search queries and results</h3>

				<form:form action="log_out" commandName="logOut" method="post">
					<div>
						<input type="submit" value="Log Out" />
					</div>
				</form:form>

				<div align="center">
					<a href="add_search_query">Add a Search Query</a> | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a>
				</div>
			</shiro:user>
		</div>
	</body>
</html>