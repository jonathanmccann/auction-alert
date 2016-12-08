$(window).load(function() {
	var contentDiv = document.getElementById('content');

	var currencyMap = {1: "USD", 7: "CAD", 15: "GBP", 3: "EUR", 4: "AUD", 5: "EUR", 14: "CHF", 13: "EUR", 10: "EUR", 2: "EUR", 12: "EUR", 16: "EUR"};

	var currencySymbolMap = {"USD": "$", "CAD": "C $", "GBP": "£", "EUR": "€", "AUD": "AU $", "CHF": "CHF "};

	var fromConversionRate;

	var imageRegex = new RegExp("<img src='([^']+)");

	var intervalId;

	var itemIds = [];

	var itemUrl = $("#preferredDomain").val();

	var isConvertCurrency = false;

	var globalId;

	var preferredCurrency = $("#preferredCurrency").val();

	var resultsPhrase = "Results will begin populating here as soon as they are found.";

	var rssUrl = "http://rest.ebay.com/epn/v1/find/item.rss?toolid=10039&lgeo=1&feedType=rss&sortOrder=StartTimeNewest&hideDuplicateItems=true&keyword=";

	var search = $("#search");

	var searchQuery = $("#searchQuery");

	var startMonitoring = $('#startMonitoring');

	var stopMonitoring = $("#stopMonitoring");

	var toConversionRate;

	if (typeof Notification !== 'function') {
		$('#notificationOptions').hide();
	}

	function fetchRss(url) {
		$.ajax({
			url: "https://query.yahooapis.com/v1/public/yql?q=select%20title%2C%20description%2C%20guid%2C%20e%3ACurrentPrice%2C%20e%3AListingType%2C%20e%3ABuyItNowPrice%20from%20rss(0%2C10)%20where%20url%20%3D%20%22" + url + "%22&format=json",
			success: function (data) {
				if (data.query && data.query.results && data.query.results.item) {
					if (data.query.count == 1) {
						var item = data.query.results.item;

						var itemId = item.guid;
						var imageUrl = imageRegex.exec(item.description)[1];
						var title = item.title;
						var listingType = item.ListingType;
						var currentPrice = item.CurrentPrice;
						var fixedPrice = item.BuyItNowPrice;

						populateResults(itemId, imageUrl, title, listingType, currentPrice, fixedPrice);
					}
					else {
						$.each(data.query.results.item, function (i, e) {
							var itemId = e.guid;
							var imageUrl = imageRegex.exec(e.description)[1];
							var title = e.title;
							var listingType = e.ListingType;
							var currentPrice = e.CurrentPrice;
							var fixedPrice = e.BuyItNowPrice;

							populateResults(itemId, imageUrl, title, listingType, currentPrice, fixedPrice);
						});
					}
				}
			}
		});
	}

	function populateResults(itemId, imageUrl, title, listingType, currentPrice, fixedPrice) {
		if (itemIds.indexOf(itemId) < 0) {
			if (contentDiv.innerHTML.indexOf(resultsPhrase) != -1) {
				contentDiv.innerHTML = "";
			}

			var html = '<div align="left" id="' + itemId + '"> <div class="monitor-result-image"> <img src=' + imageUrl + '> </div> <div class="monitor-result-information"> <a href="' + itemUrl + itemId + '" target="_blank">' + title + '</a> <br>';

			var currencySymbol = currencySymbolMap[preferredCurrency];

			if (listingType == "Auction") {
				if (isConvertCurrency) {
					html += 'Auction Price: ' + currencySymbol + (Number(currentPrice) * fromConversionRate).toFixed(2);
				}
				else {
					html += 'Auction Price: ' + currencySymbol + Number(currentPrice).toFixed(2);
				}
			}
			else if (listingType == "AuctionWithBIN") {
				if (isConvertCurrency) {
					html += 'Auction Price: ' + currencySymbol + (Number(currentPrice) * fromConversionRate).toFixed(2) + '<br>';
					html += 'Fixed Price: ' + currencySymbol + (Number(fixedPrice) * fromConversionRate).toFixed(2);
				}
				else {
					html += 'Auction Price: ' + currencySymbol + Number(currentPrice).toFixed(2) + '<br>';
					html += 'Fixed Price: ' + currencySymbol + Number(fixedPrice).toFixed(2);
				}
			}
			else if ((listingType == "FixedPrice") || (listingType == "StoreInventory")) {
				if (isConvertCurrency) {
					html += 'Fixed Price: ' + currencySymbol + (Number(currentPrice) * fromConversionRate).toFixed(2);
				}
				else {
					html += 'Fixed Price: ' + currencySymbol + Number(currentPrice).toFixed(2);
				}
			}

			contentDiv.innerHTML = html + '</div> </div>' + contentDiv.innerHTML;

			sendNotification(title, itemUrl + itemId, imageUrl);

			itemIds.unshift(itemId);

			if (itemIds.length > 30) {
				var lastItemId = "#" + itemIds[itemIds.length - 1];

				$(lastItemId).remove();

				itemIds.splice(itemIds.length - 1, 1);
			}
		}
	}

	function sendNotification(title, itemUrl, imageUrl) {
		if (!$("#desktopNotifications").is(":checked")) {
			return;
		}

		if (typeof Notification === 'function') {
			Notification.requestPermission().then(function (result) {
				if ((result === 'denied') || (result === 'default')) {
					return;
				}

				var options = {
					icon: imageUrl
				};

				var notification = new Notification(title, options);

				notification.onclick = function (event) {
					event.preventDefault();

					window.open(itemUrl);

					notification.close();
				};
			});
		}
	}

	function collapseSearchQuery() {
		searchQuery.slideToggle(500);

		search.toggleClass("fa-angle-down fa-angle-right")
	}

	function setConversionRate() {
		var itemCurrency = currencyMap[globalId];

		if (preferredCurrency == itemCurrency) {
			isConvertCurrency = false;
		}
		else {
			isConvertCurrency = true;

			var request = new XMLHttpRequest();

			request.open("GET", "http://api.fixer.io/latest?base=" + preferredCurrency + "&symbols=" + itemCurrency, false);

			request.send(null);

			toConversionRate = JSON.parse(request.responseText).rates[itemCurrency];

			fromConversionRate = 1 / toConversionRate;
		}
	}

	search.click(function() {
		collapseSearchQuery();
	});

	$("#clearResults").click(function() {
		contentDiv.innerHTML = "<h5>Please start monitoring in order to display results.</h5>";
	});

	startMonitoring.click(function() {
		var valid = $("#searchQueryForm").valid();

		if (!valid) {
			$('html, body').animate({
				scrollTop: $('#search').offset().top - $('#header').height()
			}, 500);

			return;
		}

		if (intervalId) {
			clearInterval(intervalId);
		}

		if (searchQuery.is(':visible')) {
			collapseSearchQuery();
		}

		startMonitoring.hide();
		stopMonitoring.show();

		contentDiv.innerHTML = "<h5>" + resultsPhrase + "</h5>";

		globalId = $("#globalId").val();

		setConversionRate();

		var url = rssUrl + $("#keywords").val().replace(/ /g, '%20').replace(/"/g, '%22');

		var subcategoryId = $('#subcategoryId').val();
		var categoryId = $('#categoryId').val();

		if (subcategoryId && (subcategoryId != "All Subcategories")) {
			url += "&categoryId1=" + subcategoryId
		}
		else if (categoryId && (categoryId != "All Categories")) {
			url += "&categoryId1=" + categoryId
		}

		url += "&programid=" + globalId;

		if ($("#searchDescription").is(":checked")) {
			url += "&descriptionSearch=true";
		}

		if ($("#freeShippingOnly").is(":checked")) {
			url += "&freeShipping=true";
		}

		if ($("#newCondition").is(":checked")) {
			url += "&condition1=New";
		}

		if ($("#usedCondition").is(":checked")) {
			url += "&condition1=Used";
		}

		if ($("#unspecifiedCondition").is(":checked")) {
			url += "&condition1=Unspecified";
		}

		if ($("#auctionListing").is(":checked")) {
			url += "&listingType1=AuctionWithBIN&listingType2=Auction";
		}

		if ($("#fixedPriceListing").is(":checked")) {
			url += "&listingType1=AuctionWithBIN&listingType2=FixedPrice";
		}

		var minPrice = $("#minPrice").val();
		var maxPrice = $("#maxPrice").val();

		if (minPrice && (minPrice > 0)) {
			if (isConvertCurrency) {
				minPrice = minPrice * toConversionRate;
			}

			url += "&minPrice=" + minPrice;
		}

		if (maxPrice && (maxPrice > 0)) {
			if (isConvertCurrency) {
				maxPrice = maxPrice * toConversionRate;
			}

			url += "&maxPrice=" + maxPrice;
		}

		fetchRss(encodeURIComponent(url + "&" + new Date().getTime()));

		intervalId = setInterval(function() {
			fetchRss(encodeURIComponent(url + "&" + new Date().getTime()));
		}, 5000);
	});

	stopMonitoring.click(function() {
		if (intervalId) {
			clearInterval(intervalId);

			intervalId = null;
		}

		startMonitoring.show();
		stopMonitoring.hide();
	});
});