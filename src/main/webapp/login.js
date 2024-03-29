{
	document.getElementById("loginbutton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			clearMessages();
			var email = form.querySelector('input[name="username"]').value;
			var password = form.querySelector('input[name="password"]').value;
			if (password != null && validateEmail(email)) {
				makeCall("POST", 'CheckCredenziali', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									sessionStorage.setItem('username', message);
									window.location.href = "homePage.html";
									break;
								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									break;
								case 500: // server error
									document.getElementById("errormessage").textContent = message;
									break;
							}
						}
					}
				);
			} else {
				document.getElementById("errormessage").textContent = "campi non presenti o non validi";
			}
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("registerbutton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		var password = form.querySelector('input[name="password"]').value;
		var repeatpassword = form.querySelector('input[name="repeatpassword"]').value;
		var email = form.querySelector('input[name="username"]').value;
		var name = form.querySelector('input[name="name"]').value;
		var surname = form.querySelector('input[name="surname"]').value;
		if (form.checkValidity()) {
			clearMessages();
			if (password == repeatpassword && validateEmail(email) && password != null && name != null && surname != null) {
				makeCall("POST", 'RegistraUtente', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									document.getElementById("successmessage").textContent = message;
									break;
								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									break;
								case 500: // server error
									document.getElementById("errormessage").textContent = message;
									break;
							}
						}
					});
			} else {
				document.getElementById("errormessage").textContent = "campi errati";
			}
		} else {
			form.reportValidity();
		}
	});
};