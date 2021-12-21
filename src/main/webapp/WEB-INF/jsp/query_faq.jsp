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
		<title>Search Query</title>

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

		<noscript>
			<link rel="stylesheet" href="/resources/css/skel.css" />
			<link rel="stylesheet" href="/resources/css/style.css" />
			<link rel="stylesheet" href="/resources/css/style-xlarge.css" />
		</noscript>
	</head>
	<body>
		<header class="skel-layers-fixed" id="header">
			<h1><a href="/home">Auction Alert</a></h1>
			<nav id="nav">
				<ul>
					<shiro:guest>
						<li><a href="log_in" id="loginLink">Log In</a></li>
						<li><a href="create_account" class="button special">Sign Up</a></li>
					</shiro:guest>
					<shiro:user>
						<c:if test="${isActive}">
							<li><a href="add_search_query">Add Search Query</a></li>
							<li><a href="view_search_queries">Search Queries and Results</a></li>
						</c:if>

						<li><a href="my_account">My Account</a></li>
						<li><a href="log_out" class="button special">Log Out</a></li>
					</shiro:user>
				</ul>
			</nav>
		</header>

		<section class="minor">
			<h1>Search Query</h1>

			<div class="sub-heading-indent">
				<h5><a href="/faq">FAQ Home</a></h5>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container small">
				<div>
					<span class="anchor" id="add"></span>
					<h2>How do I add a search query?</h2>
					<p class="faq-paragraph">
						You are able to add a new search query from the <a href="/add_search_query" target="_blank">add search query</a> page. There are many different pieces that make up a query:<br><br>
						<b>Keywords</b>
					</p>

					<p class="faq-paragraph-indent">
						This is what you want to search for on eBay. This field is required since without we won't know what to search!
					</p>

					<p class="faq-paragraph">
						<b>Category</b>
					</p>

					<p class="faq-paragraph-indent">
						This selection box allows you to choose a category in which to search such as Books or Video Games. Only items listed under the category you choose will be returned.
					</p>

					<p class="faq-paragraph">
						<b>Subcategory</b>
					</p>

					<p class="faq-paragraph-indent">
						After selecting a category, you are then able to choose a subcategory. For example if you chose the category Books you could then narrow it down further to Cookbooks or Nonfiction.
					</p>

					<p class="faq-paragraph">
						<b>Search Description</b>
					</p>

					<p class="faq-paragraph-indent">
						If this option is checked, your keywords will not only be searched against the item's title but also the item's description.
						This will allow for a broader search, but it might return unrelated items as some sellers include words in their descriptions even though that is not the item they are selling.
					</p>

					<p class="faq-paragraph">
						<b>Free Shipping</b>
					</p>

					<p class="faq-paragraph-indent">
						With this option selected, only items with free shipping will be returned.
					</p>

					<p class="faq-paragraph">
						<b>Listing Type</b>
					</p>

					<p class="faq-paragraph-indent">
						Auctions are listings that start at a specified price and then users can bid the amount they wish to pay. When the auction ends, the highest bidder wins the item.<br><br>
						With a Buy it Now, instead of placing bids on an item, you are able to purchase the item immediately for the specified price.<br><br>
						Choosing either of these options will only return results that offer the specified type of listing. By default, both types are searched.
						For more information please see <a href="http://pages.ebay.com/help/buy/formats.html">http://pages.ebay.com/help/buy/formats.html</a>.
					</p>

					<p class="faq-paragraph">
						<b>Condition</b>
					</p>

					<p class="faq-paragraph-indent">
						Your options for condition are New, Used, and Unspecified. Based on the options selected, only items matching the specified condition(s) will be returned. By default, all conditions are searched.
					</p>

					<p class="faq-paragraph">
						<b>Minimum and Maximum Price</b>
					</p>

					<p class="faq-paragraph-indent">
						Setting these values all you to specify a minimum, maximum, or range of prices you are looking for.<br><br>
						If you do not want to pay more than $100 for an item, you can set 100 as the maximum price.
						If you are looking to pay between $30 and $50 for an item, you can $30 and $50 for the minimum and maximum prices respectively.<br><br>
						These currency for these values is determined by the marketplace set in your account. If your marketplace is <b>United Kingdom (.co.uk)</b>, these values will be in <b>GBP</b>.
						Likewise, if your marketplace is <b>United States (.com)</b>, then the values will be in <b>USD</b>.<br><br>
					</p>

					<span class="anchor" id="edit"></span>
					<h2>How do I edit a search query?</h2>
					<p class="faq-paragraph">
						On the <a href="/view_search_queries" target="_blank">view search queries</a> page, all of your saved queries will be displayed.
						Clicking on a specific query will bring up its results as well as some options for it. One of the options is <b>Edit</b>.
						Upon clicking this button, you will be taken to the <b>Update Search Query</b> page and the query's information will be pre-populated in the form.<br><br>
						From here, you are free to edit the query as necessary and save it. Then, the next time the query is searched, it will use the new parameters and any new results will be delivered to you.
					</p>

					<span class="anchor" id="boolean"></span>
					<h2>Can I use boolean logic?</h2>
					<p class="faq-paragraph">
						Yes! Here are the following search operators that are supported by eBay and how they work. In order to show how they work, let's say there are the following items for sale on eBay:<br><br>
						1. DVD<br>
						2. Action DVD<br>
						3. Funny Action DVD<br><br>

						<b>Quotation marks</b>

					<p class="faq-paragraph-indent">
						Surrounding a word or phrase with quotation marks returns listings that contains those exact words or phrases.
						For example, using our listings above, if we search for <b>"Action DVD"</b>, we will only retrieve one result.
						If we remove the quotation marks, then we will retrieve two results, the ones containing both the words <b>Action</b> and <b>DVD</b>.
					</p>

					<p class="faq-paragraph">
						<b>Parentheses</b>
					</p>

					<p class="faq-paragraph-indent">
						Using parentheses allow you to set up an OR operation. Either this word or this word.
						If we search for <b>(Action, Funny)</b>, then two results would be returned to us since we are searching for items that contain <b>Action</b> or <b>Funny</b>.
					</p>

					<p class="faq-paragraph">
						<b>Minus sign (-)</b>
					</p>

					<p class="faq-paragraph-indent">
						Using the minus sign acts as a NOT operator. Placing this symbol before a word will make sure that the item does not contain that word.
						If we search for <b>DVD -funny</b>, then two results will be returned. Additionally, this can be used in conjunction with parentheses. Searching for <b>DVD -(action, funny)</b> will only retrieve a single result.
					</p>

					<p class="faq-paragraph">
						For more information please see <a href="http://pages.ebay.com/help/search/advanced-search.html#using">http://pages.ebay.com/help/search/advanced-search.html#using</a>.
					</p>

					<span class="anchor" id="after"></span>
					<h2>What happens after I save a search query?</h2>
					<p class="faq-paragraph">
						Once a query has been saved you will be directed to the <a href="/view_search_queries" target="_blank">view search queries</a> page where you can see the list of all your currently saved queries as well as the results for each query.<br><br>
						Within one minute, the newly saved query will be searched and new results will be delivered via email. From then on, it will be search every minute and you will be notified of any new results that are found.
					</p>

					<span class="anchor" id="often"></span>
					<h2>How often are queries searched?</h2>
					<p class="faq-paragraph">
						Every search query is searched every minute.
					</p>

					<span class="anchor" id="active"></span>
					<h2>What's the difference between active and inactive queries?</h2>
					<p class="faq-paragraph">
						You have the ability to mute a search query if you wish to stop searching that query but still want to save it for potential future use.<br><br>
						An active query means that the query is being searched and you will receive any new results for that query via email.
						Inactive queries will not be searched and new results will not be retrieved.<br><br>
						You are able to set whether or not a query is active or inactive by clicking on the query's keywords and then clicking either the <b>Activate</b> or <b>Deactivate</b> buttons.
					</p>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>