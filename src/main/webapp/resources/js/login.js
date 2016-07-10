$(document).ready(function() {
	var login = document.getElementById('login');
	var close = document.getElementById("close");

	$("#loginLink").click(function(e) {
		e.preventDefault();

		login.style.display = "block";
	});

	close.onclick = function() {
		login.style.display = "none";
	};

	window.onclick = function(event) {
		if (event.target == login) {
			login.style.display = "none";
		}
	}
});