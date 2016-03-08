$(window).load(function() {
	$(".header").click(function() {
		$header = $(this);

		$content = $header.next();

		$content.slideToggle(500);
	});

	$('#keywords, #minPrice, #maxPrice').tooltipster({
		trigger: 'custom',
		onlyOne: false,
		position: 'bottom'
	});

	$.validator.addMethod("decimalPlaces", function (value) {
		var pattern = new RegExp("^\\d+(?:\\.\\d{2})?$");

		return pattern.test(value);
	}, "Price must have two decimal places");

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

	$('#addSearchQueryForm').validate({
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
			}
		}
	});

	$('#basedOnTime').click(function() {
		if( $(this).is(':checked')) {
			$("#basedOnTimeOptions").show();
		}
		else {
			$("#basedOnTimeOptions").hide();
		}
	});

	$('#submit').click(function() {
		$('#addSearchQueryForm').valid();
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