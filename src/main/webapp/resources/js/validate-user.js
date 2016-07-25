$(window).load(function() {
	$("#currentPassword, #emailAddress, #newPassword, #password").tooltipster({
		trigger: 'custom',
		onlyOne: false,
		position: 'bottom'
	});

	$('#resetPasswordForm, #updateUserForm').validate({
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
			currentPassword: {
				minlength: 6
			},
			emailAddress: {
				minlength: 3,
				maxlength: 255,
				required: true,
				email: true
			},
			newPassword: {
				minlength: 6
			},
			password: {
				required: true,
				minlength: 6
			}
		}
	});

	$('#resetPasswordSubmit').click(function() {
		$('#resetPasswordForm').valid();
	});

	$('#updateUserSubmit').click(function() {
		$('#updateUserForm').valid();
	});
});