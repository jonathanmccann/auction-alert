$(window).load(function() {
	var updateBillingForm = $('#updateBillingForm');

	var handler = StripeCheckout.configure({
		key: $('#stripePublishableKey').val(),
		image: "images/marketplace.png",
		name: "eBay Search",
		label: "Update Card Details",
		panelLabel: "Update Card Details",
		allowRememberMe: false,
		zipCode: true,
		token: function(token) {
			updateBillingForm.append($('<input id="stripeToken" name="stripeToken" type="hidden">').val(token.id));

			updateBillingForm.submit();
		}
	});

	$('#updateBillingSubmit').click(function(e) {
		handler.open({
			email: $('#emailAddress').val()
		});

		e.preventDefault();
	});
});