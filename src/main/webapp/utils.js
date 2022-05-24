{
	function makeCall(method, url, formElement, cback, reset = true) {
		var req = new XMLHttpRequest();
		req.onreadystatechange = function() {
			cback(req)
		};
		req.open(method, url, reset);
		if (formElement == null) {
			req.send();
		} else if (formElement instanceof FormData) {
			req.send(formElement);
		} else {
			req.send(new FormData(formElement));
		}
		if (formElement !== null && !(formElement instanceof FormData) && reset === true) {
			formElement.reset();
		}
	}

	function clearMessages() {
		document.getElementById("errormessage").innerHTML = "";
		document.getElementById("successmessage").innerHTML = "";
	}

	function validateEmail(email) {
		var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		console.log(re.test(email));
		return re.test(email);
	}
};