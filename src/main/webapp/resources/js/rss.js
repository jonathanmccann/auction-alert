$(window).load(function() {
	var itemIds = [];

	var itemIdRegex = new RegExp('-/([0-9]*)?');

	var contentDiv = document.getElementById('content');

	var intervalId;

	function fetchRss(url) {
		$.ajax({
			url: document.location.protocol + '//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=5&callback=?&q=' + url,
			dataType: 'json',
			cache: false,
			success: function (data) {
				if (data.responseData.feed && data.responseData.feed.entries) {
					$.each(data.responseData.feed.entries, function (i, e) {
						var itemId = e.link.match(itemIdRegex)[1];

						if (itemIds.indexOf(itemId) < 0) {
							contentDiv.innerHTML = '<div id="' + itemId + '"> <a href="' + e.link + '" target="_blank">' + e.title + '</a> </div>' + contentDiv.innerHTML;

							sendNotification(e.title);

							itemIds.unshift(itemId);
						}
						else if (itemIds.length > 30) {
							var lastItemId = "#" + itemIds[itemIds.length - 1];

							$(lastItemId).remove();

							itemIds.splice(itemIds.length - 1, 1);
						}
					});
				}
			}
		});
	}

	function sendNotification(title) {
		if (!$("#desktopNotifications").is(":checked")) {
			return;
		}

		Notification.requestPermission().then(function(result) {
			if ((result === 'denied') || (result === 'default')) {
				return;
			}

			new Notification(title);
		});
	}

	$("#clearResults").click(function() {
		contentDiv.innerHTML = "";
	});

	$('#startMonitoring').click(function() {
		var valid = $("#monitorForm").valid();

		if (!valid) {
			return;
		}

		if (intervalId) {
			clearInterval(intervalId);
		}

		var url = "http://www.ebay.com/sch/i.html?&_sop=10&_nkw=" + $("#keywords").val();

		var subcategoryId = $('#subcategoryId').val();
		var categoryId = $('#categoryId').val();

		if (subcategoryId && (subcategoryId != "All Subcategories")) {
			url += "&_sacat=" + subcategoryId
		}
		else if (categoryId && (categoryId != "All Categories")) {
			url += "&_sacat=" + categoryId
		}

		if ($("#searchDescription").is(":checked")) {
			url += "&LH_TitleDesc=1";
		}

		if ($("#freeShippingOnly").is(":checked")) {
			url += "&LH_FS=1";
		}

		if ($("#newCondition").is(":checked")) {
			url += "&LH_ItemCondition=11";
		}

		if ($("#usedCondition").is(":checked")) {
			url += "&LH_ItemCondition=12";
		}

		if ($("#unspecifiedCondition").is(":checked")) {
			url += "&LH_ItemCondition=10";
		}

		if ($("#auctionListing").is(":checked")) {
			url += "&LH_Auction=1";
		}

		if ($("#fixedPriceListing").is(":checked")) {
			url += "&LH_BIN=1";
		}

		var minPrice = $("#minPrice").val();
		var maxPrice = $("#maxPrice").val();

		if (minPrice && (minPrice > 0)) {
			url += "&_udlo=" + minPrice;
		}

		if (maxPrice && (maxPrice > 0)) {
			url += "&_udhi=" + maxPrice;
		}

		url = encodeURIComponent(url + "&_rss=1");

		intervalId = setInterval(function() {
			fetchRss(url + new Date().getTime());
		}, 3000);
	});

	$("#stopMonitoring").click(function() {
		if (intervalId) {
			clearInterval(intervalId);

			intervalId = null;
		}
	});
});