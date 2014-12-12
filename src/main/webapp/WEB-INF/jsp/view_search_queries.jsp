<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>View Search Queries</title>
</head>
<body>
	<div align="center">
		<table border="0">
			<tr>
				<td colspan="2" align="center"><h2>Current Search Queries</h2></td>
			</tr>
			<c:forEach items="${searchQueryModels}" var="searchQueryModel">
				<tr>
					<td><c:out value="${searchQueryModel.searchQuery}"/><td>
				</tr>
			</c:forEach>
		</table>

		<a href="/eBay-webapp/add_search_query">Add a Search Query</a>
		</br>
		<a href="/eBay-webapp/view_search_query_results">View Search Query Results</a>
	</div>
</body>
</html>