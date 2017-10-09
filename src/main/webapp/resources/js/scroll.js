$(document).ready(function() {
	$("#learn-more").click(function(){
		$('html, body').animate({
			scrollTop: $($(this).attr('href')).offset().top - 32
		}, 500);
		return false;
	});
});