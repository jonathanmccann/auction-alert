$(window).load(function() {
	$(".header").click(function() {
		$header = $(this);

		$content = $header.next();

		$content.slideToggle(500);
	});

	$("#emailAddress, #user\\.emailAddress, #user\\.phoneNumber, #keywords, #maxPrice, #minPrice, #password").tooltipster({
		trigger: 'custom',
		onlyOne: false,
		position: 'bottom'
	});

	$("#notificationPreferences\\.startOfDay").tooltipster({
		trigger: 'custom',
		onlyOne: false,
		position: 'right'
	});

	$.validator.addMethod("decimalPlaces", function(value) {
		var pattern = new RegExp("^\\d+(?:\\.\\d{2})?$");

		return pattern.test(value);
	}, "Price must have two decimal places");

	$.validator.addMethod("lessThan", function(value, element, param) {
		var $min = $(param);

		if (this.settings.onfocusout) {
			$min.off(".validate-startOfDay").on("blur.validate-startOfDay", function() {
				$(element).valid();
			});
		}

		if ((value == 0) && ($min.val() == 0)) {
			return true;
		}

		return parseFloat(value) < parseFloat($min.val());
	}, "Max price must be greater than min price");

	$.validator.addMethod("phoneNumber", function(value) {
		if (!value && !$('#textNotification').is(':checked')) {
			return true;
		}

		value = value.replace(/\D/g, "");

		var pattern = new RegExp("[0-9]{10,10}");

		return pattern.test(value);
	}, "Phone number must contain 10 digits");

	$.validator.addMethod("startOfDay", function(value, element, param) {
		var $endOfDay = $(param);

		if (this.settings.onfocusout) {
			$endOfDay.off(".validate-startOfDay").on("blur.validate-startOfDay", function() {
				$(element).valid();
			});
		}

		return parseFloat(value) < parseFloat($endOfDay.val());
	}, "Start of day must be before end of day");

	$('#addSearchQueryForm, #createAccountForm, #updateUserForm').validate({
		errorPlacement: function(error, element) {
			var lastError = $(element).data('lastError');
			var newError = $(error).text();

			$(element).data('lastError', newError);

			if (newError !== '' && newError !== lastError) {
				$(element).tooltipster('content', newError);
				$(element).tooltipster('show');
			}
		},
		success: function(label, element) {
			$(element).tooltipster('hide');
		},
		rules: {
			emailAddress: {
				minlength: 3,
				maxlength: 255,
				required: true,
				email: true
			},
			keywords: {
				required: true
			},
			minPrice: {
				decimalPlaces: true,
				lessThan: '#maxPrice'
			},
			maxPrice: {
				decimalPlaces: true
			},
			password: {
				required: true
			},
			'user.emailAddress': {
				minlength: 3,
				maxlength: 255,
				required: true,
				email: true
			},
			'user.phoneNumber': {
				phoneNumber: true
			},
			'notificationPreferences.startOfDay': {
				startOfDay: '#notificationPreferences\\.endOfDay'
			}
		}
	});

	$('#basedOnTime').click(function() {
		if ($(this).is(':checked')) {
			$("#notificationOptions").hide();

			$("#basedOnTimeOptions").show();
		}
		else {
			$("#notificationOptions").show();

			$('#notificationPreferences\\.startOfDay').tooltipster('hide');

			$("#basedOnTimeOptions").hide();
		}
	});

	$('#createAccountSubmit').click(function() {
		$('#createAccountForm').valid();
	});

	$('#updateSearchQuerySubmit').click(function() {
		$('#addSearchQueryForm').valid();
	});

	$('#updateUserSubmit').click(function() {
		$('#updateUserForm').valid();
	});

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
		var basedOnTime = $('#basedOnTime').is(':checked');

		if (basedOnTime) {
			$("#notificationOptions").hide();
		}
		else {
			$("#basedOnTimeOptions").hide();
		}

		loadSubcategories(true);
	});
});