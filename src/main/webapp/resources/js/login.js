$(document).ready(function() {
	$('a.login').click(function() {
		//Fade in the Popup
		$("#login").fadeIn(300);

		$('body').append('<div id="mask"></div>');
		$('#mask').fadeIn(300);

		return false;
	});

	// When clicking on the button close or the mask layer the popup closed
	$('a.close, #mask').live('click', function() {
		$('#mask, .login-popup').fadeOut(300, function() {
			$('#mask').remove();
		});

		return false;
	});
});