const togglePasswordButton = document.getElementById("togglePasswordBtn");

togglePasswordButton.addEventListener("click", function (e) {
	const passwordInput = document.getElementById("password");
	const isPreviousTypePassword =
	  passwordInput.getAttribute("type") === "password";
	// toggle the type attribute
	const type = isPreviousTypePassword ? "text" : "password";
	passwordInput.setAttribute("type", type);
	// toggle the button text
	togglePasswordButton.textContent = isPreviousTypePassword ? "Hide" : "Show";
});
