<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>View Search Queries</title>
</head>
<body>
	<div align="center">
		<form:form action="delete_search_query" commandName="searchQueryCheckboxes" method="post">
			<table border="0">
				<tr>
					<td align="center" colspan="2"><h2>Current Search Queries</h2></td>
				</tr>
				<c:choose>
					<c:when test="${empty searchQueryModels}">
						<td>There are currently no search queries<td>
					</c:when>
					<c:otherwise>
						<c:forEach items="${searchQueryModels}" var="searchQueryModel">
							<tr>
								<td><input id="checkboxes" name="searchQueryIds" type="checkbox" value="${searchQueryModel.searchQueryId}" /><label>${searchQueryModel.searchQuery}</label></td>
							</tr>
						</c:forEach>
						<tr>
							<td align="center" colspan="2"><input type="submit" value="Delete Search Query" /></td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</form:form>
		</br>
		<a href="add_search_query">Add a Search Query</a> | View Search Queries | <a href="view_search_query_results">View Search Query Results</a>
	</div>
</body>
</html>