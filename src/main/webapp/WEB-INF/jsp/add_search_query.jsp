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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Add Search Query</title>
</head>
<body>
	<div align="center">
		<form:form action="add_search_query" commandName="searchQueryModel" method="post">
			<table border="0">
				<tr>
					<td align="center" colspan="2"><h2>Add Search Query</h2></td>
				</tr>
				<tr>
					<td>Search Query:</td>
					<td><form:input path="searchQuery" /></td>
				</tr>
				<tr>
					<td align="center" colspan="2"><input type="submit" value="Add Search Query" /></td>
				</tr>
			</table>
		</form:form>
		</br>
		Add Search Query | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a>
	</div>
</body>
</html>