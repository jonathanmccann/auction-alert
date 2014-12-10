<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>View Search Queries</title>
</head>
<body>
	<div align="center">
		<table border="0">
			<tr>
				<td colspan="2" align="center"><h2>Add Search Query</h2></td>
			</tr>
			<tr>
				<td>Search Query:</td>
				<td>${searchQueryModel.searchQuery}</td>
			</tr>
		</table>

		<a href="/eBay-webapp/add_search_query">Add a Search Query</a>
	</div>
</body>
</html>