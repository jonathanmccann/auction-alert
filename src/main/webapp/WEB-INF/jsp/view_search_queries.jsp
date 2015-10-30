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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>View Search Queries</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
	</head>
	<body>
		<div>
			<form:form action="delete_search_query" commandName="searchQueryCheckboxes" method="post">
				<div>
					<h2>Current Search Queries</h2>
					<c:choose>
						<c:when test="${empty searchQueries}">
							<div>
								There are currently no search queries.
							</div>
						</c:when>
						<c:otherwise>
							<c:forEach items="${searchQueries}" var="searchQuery">
								<div>
									<input id="checkboxes" name="searchQueryIds" type="checkbox" value="${searchQuery.searchQueryId}" /><label><a href="edit_search_query?searchQueryId=${searchQuery.searchQueryId}">${searchQuery.keywords}</a></label>
								</div>
							</c:forEach>
							<div>
								<div>
									<input type="submit" value="Delete Search Query" />
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</form:form>
			</br>
			<div align="center">
				<a href="add_search_query">Add a Search Query</a> | View Search Queries | <a href="view_search_query_results">View Search Query Results</a>
			</div>
		</div>
	</body>
</html>