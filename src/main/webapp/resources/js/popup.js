$(document).ready(function() {
	var popup = document.getElementById('popup');
	var close = document.getElementById("close");

	$("#loginLink").click(function(e) {
		if (($(window).width() > 980) && ($(window).height() > 400)) {
			e.preventDefault();

			popup.style.display = "block";

			$("#emailAddress").focus();

			close.onclick = function() {
				popup.style.display = "none";
			};

			window.onclick = function(event) {
				if (event.target == popup) {
					popup.style.display = "none";
				}
			}
		}
	});
});