$(window).load(function(){
	$(".header").click(function () {
		$header = $(this);

		$content = $header.next();

		$content.slideToggle(500);
	});
});