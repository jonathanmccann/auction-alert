$(document).ready(function() {
	var popup = document.getElementById('popup');
	var close = document.getElementById("close");

	$("#deleteAccount").click(function(e) {
		e.preventDefault();

		popup.style.display = "block";
	});

	$("#loginLink").click(function(e) {
		e.preventDefault();

		popup.style.display = "block";

		$("#emailAddress").focus();
	});

	close.onclick = function() {
		popup.style.display = "none";
	};

	window.onclick = function(event) {
		if (event.target == popup) {
			popup.style.display = "none";
		}
	}
});