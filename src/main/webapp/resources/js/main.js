$(window).load(function() {
	$("#search").click(function() {
		$("#searchQuery").slideToggle(500);

		$(this).toggleClass("fa-angle-down fa-angle-right")
	});

	$("#user\\.emailAddress, #keywords, #maxPrice, #minPrice").tooltipster({
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

	$('#addSearchQueryForm, #monitorForm, #updateUserForm').validate({
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
			'user.emailAddress': {
				minlength: 3,
				maxlength: 255,
				required: true,
				email: true
			}
		}
	});

	$('#updateSearchQuerySubmit').click(function() {
		$('#addSearchQueryForm').valid();
	});

	$('#updateUserSubmit').click(function() {
		$('#updateUserForm').valid();
	});
});