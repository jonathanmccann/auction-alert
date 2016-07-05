$(window).load(function() {
	var itemIds = [];

	var regex = new RegExp('-/([0-9]*)?');

	setInterval(function () {
		$.ajax({
			url: document.location.protocol + '//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=5&callback=?&q=' + encodeURIComponent('http://www.ebay.com/sch/i.html?_from=R40&_sacat=0&_nkw=nes&_sop=10&_rss=1' + new Date().getTime()),
			dataType: 'json',
			success: function (data) {
				console.log("Starting");
				var div = document.getElementById('content');

				if (data.responseData.feed && data.responseData.feed.entries) {
					$.each(data.responseData.feed.entries, function (i, e) {
						var itemId = e.link.match(regex)[1];

						if (itemIds.indexOf(itemId) < 0) {
							div.innerHTML = '<div id="' + itemId + '"> <a href="' + e.link + '" target="_blank">' + e.title + '</a> </div>' + div.innerHTML;

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
	}, 3000)
});