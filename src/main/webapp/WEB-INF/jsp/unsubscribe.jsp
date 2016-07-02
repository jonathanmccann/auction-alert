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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>Error</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
	</head>
	<body>
		<div>
			<h2>Unsubscribe</h2>
			</br>
			${unsubscribeMessage}
			</br>
			<shiro:user>
				<div align="center">
					<a href="add_search_query">Add a Search Query</a> | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a> <br> <br>
					<a href="my_account">My Account</a> | <a href="log_out">Log Out</a>
				</div>
			</shiro:user>
		</div>
	</body>
</html>