$(window).load(function() {
	$(".header").click(function() {
		$header = $(this);

		$content = $header.next();

		$content.slideToggle(500);
	});

	$("#user\\.emailAddress, #user\\.phoneNumber, #notificationPreferences\\.endOfDay, #notificationPreferences\\.startOfDay, #keywords, #minPrice, #maxPrice").tooltipster({
		trigger: 'custom',
		onlyOne: false,
		position: 'bottom'
	});

	$.validator.addMethod("decimalPlaces", function (value) {
		var pattern = new RegExp("^\\d+(?:\\.\\d{2})?$");

		return pattern.test(value);
	}, "Price must have two decimal places");

	$.validator.addMethod("endOfDay", function (value, element, param) {
		var $startOfDay = $(param);

		return parseFloat(value) > parseFloat($startOfDay.val());
	}, "End of day must be after start of day");

	$.validator.addMethod("greaterThan", function (value, element, param) {
		var $min = $(param);

		if ((value == 0) && ($min.val() == 0)) {
			return true;
		}

		return parseFloat(value) > parseFloat($min.val());
	}, "Max price must be greater than min price");

	$.validator.addMethod("lessThan", function (value, element, param) {
		var $min = $(param);

		if ((value == 0) && ($min.val() == 0)) {
			return true;
		}

		return parseFloat(value) < parseFloat($min.val());
	}, "Max price must be greater than min price");

	$.validator.addMethod("phoneNumber", function (value) {
		value = value.replace(/\D/g, "");

		var pattern = new RegExp("[0-9]{10,10}");

		return pattern.test(value);
	}, "Phone number must contain 10 digits");

	$.validator.addMethod("startOfDay", function (value, element, param) {
		var $endOfDay = $(param);

		return parseFloat(value) < parseFloat($endOfDay.val());
	}, "Start of day must be before end of day");

	$('#addSearchQueryForm, #updateUserForm').validate({
		errorPlacement: function (error, element) {
			var lastError = $(element).data('lastError');
			var newError = $(error).text();

			$(element).data('lastError', newError);

			if(newError !== '' && newError !== lastError){
				$(element).tooltipster('content', newError);
				$(element).tooltipster('show');
			}
		},
		success: function (label, element) {
			$(element).tooltipster('hide');
		},
		rules: {
			keywords: {
				required: true
			},
			minPrice: {
				decimalPlaces: true,
				lessThan: '#maxPrice'
			},
			maxPrice: {
				decimalPlaces: true,
				greaterThan: '#minPrice'
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
			'notificationPreferences.endOfDay': {
				endOfDay: '#notificationPreferences\\.startOfDay'
			},
			'notificationPreferences.startOfDay': {
				startOfDay: '#notificationPreferences\\.endOfDay'
			}
		}
	});

	$('#basedOnTime').click(function() {
		if( $(this).is(':checked')) {
			$("#basedOnTimeOptions").show();
		}
		else {
			$('#notificationPreferences\\.startOfDay').tooltipster('hide');
			$('#notificationPreferences\\.endOfDay').tooltipster('hide');

			$("#basedOnTimeOptions").hide();
		}
	});

	$('#updateSearchQuerySubmit').click(function() {
		$('#addSearchQueryForm').valid();
	});

	$('#updateUserSubmit').click(function() {
		$('#updateUserForm').valid();
	});

	$(document).ready(function () {
		var basedOnTime = $('#basedOnTime').is(':checked');

		if (basedOnTime) {
			$("#basedOnTimeOptions").show();
		}
		else {
			$("#basedOnTimeOptions").hide();
		}
	});
});