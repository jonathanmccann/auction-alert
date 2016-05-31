$(window).load(function() {
	function loadSubcategories(initial) {
		$.ajax({
			url: '/subcategories',
			data: ({categoryParentId : $('#categoryId').val()}),
			success: function(data) {
				var keys = [];

				for (var key in data) {
					keys.push(key);
				}

				keys.sort();

				var html = '<option value="">All Subcategories</option>'

				if (keys.length == 0) {
					$('#subcategoryId').prop('disabled', true);
				}
				else {
					$('#subcategoryId').prop('disabled', false);
				}

				for (var i = 0; i < keys.length; i++) {
					key = keys[i];

					html += '<option value="' + data[key] + '">' + key + '</option>';
				}

				$('#subcategoryId').html(html);

				if (initial == true) {
					document.getElementById('subcategoryId').value = document.getElementById('initialSubcategoryId').value;
				}
			}
		});
	}

	$('#categoryId').change(
		loadSubcategories
	);

	$(document).ready(function () {
		loadSubcategories(true);
	});
});