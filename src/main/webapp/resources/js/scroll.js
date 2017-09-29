$(document).ready(function() {
	$("#learn-more").click(function(){
		$('html, body').animate({
			scrollTop: 500-$( $(this).attr('href') ).offset().top
		}, 500);
		return false;
	});
});