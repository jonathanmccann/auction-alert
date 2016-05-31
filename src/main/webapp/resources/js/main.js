$(window).load(function() {
	$(".header").click(function() {
		$header = $(this);

		$content = $header.next();

		$content.slideToggle(500);
	});

	$("#emailAddress, #user\\.emailAddress, #keywords, #maxPrice, #minPrice, #password").tooltipster({
		trigger: 'custom',
		onlyOne: false,
		position: 'bottom'
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
			}
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
});