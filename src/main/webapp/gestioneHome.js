
window.addEventListener("load", () => {
	if (sessionStorage.getItem("username") == null) {
		window.location.href = "index.html";
	} else {
		getContiUtente(sessionStorage.getItem("username"));
	}
});

document.getElementById("selectionbutton").addEventListener('click', (e) => {
	var form = e.target.closest("form");
	if (form.checkValidity()) {
		clearMessages();
		IDConto = form.querySelector('select[name="conto"]').value;
	} else {
		form.reportValidity();
	}
});

function getContiUtente() {
	makeCall("GET", 'GetConti', null,
		function(x) {
			if (x.readyState == XMLHttpRequest.DONE) {
				var message = x.responseText;
				switch (x.status) {
					case 200:
						var elencoconti = JSON.parse(x.responseText);
						var select = $("select#conto");
						var elenco = document.getElementById("conto");
						elenco.size = elencoconti.length;
						for (var key in elencoconti) {
							var conti = elencoconti[key];
							$('<option>', {
								value: conti,
								text: conti,
							}).appendTo(select);
						}
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
}

document.getElementById("selectionbutton").addEventListener('click', (e) => {
	var form = e.target.closest("form");
	if (form.checkValidity()) {
		makeCall("Post", 'SelezionaConto', form,
			function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200:
							var content = JSON.parse(x.responseText);
							document.getElementById("DettaglioContoIDConto").textContent = content[0].IDConto;
							document.getElementById("DettaglioContoProprietario").textContent = content[0].proprietario;
							document.getElementById("DettaglioContoSaldo").textContent = content[0].saldo;
							const tBody = document.getElementById("trasferimenti");
							tBody.innerHTML = "";
							for (let i = 0; i < content[1].length; i++) {
								var IDT = content[1][i].IDTrasferimento;
								var Data = content[1][i].data;
								var Importo = content[1][i].importo;
								var Causale = content[1][i].causale;
								var IDContoOrigine = content[1][i].IDContoOrigine;
								var IDContoDestinazione = content[1][i].IDContoDestinazione;
								tBody.innerHTML += " <tr><td>" + IDT + "</td><td>" + Data + "</td><td>" + Importo + "</td><td>" + Causale + "</td><td>" + IDContoOrigine + "</td><td>" + IDContoDestinazione + "</td></tr>";
							}
							break;
						case 400: // bad request
							document.getElementById("errormessage").textContent = message;
							break;
						case 401: // unauthorized
							document.getElementById("errormessage").textContent = message;
							break;
						case 404: // not found
							document.getElementById("errormessage").textContent = message;
							break;
						case 500: // server error
							document.getElementById("errormessage").textContent = message;
							break;
					}
				}
			}
		)
	} else {
		form.reportValidity();
	}
});

document.getElementById("inviatrasferimento").addEventListener('click', (e) => {
	var form = e.target.closest("form");
	var usernameDestinatario = form.querySelector('input[name="usernameDestinatario"]').value;
	var IDContoDestinazione = form.querySelector('input[name="IDContoDestinazione"]').value;
	var causale = form.querySelector('input[name="causale"]').value;
	var importo = form.querySelector('input[name="importo"]').value;
	var IDContoOrigine = form.querySelector('input[name="IDContoOrigine"]').value;
	console.log(usernameDestinatario);
	console.log(IDContoOrigine);
	if (form.checkValidity()) {
		clearMessages();
		if (validateEmail(usernameDestinatario)) {
			makeCall("POST", 'CheckConto', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								var content = JSON.parse(message);
								document.getElementById("successo").className = "show";
								document.getElementById("showsuccesso").textContent = "AntiTasto3";
								document.getElementById("contoorigineprima").innerHTML = "<p>" + content[0].IDConto + "</p><p>" + content[0].saldo + "</p><p>" + content[0].proprietario + "</p>";
								document.getElementById("contodestinazioneprima").innerHTML = "<p>" + content[2].IDConto + "</p><p>" + content[2].saldo + "</p><p>" + content[1].proprietario + "</p>";
								document.getElementById("contooriginedopo").innerHTML = "<p>" + content[1].IDConto + "</p><p>" + content[1].saldo + "</p><p>" + content[2].proprietario + "</p>";
								document.getElementById("contodestinazionedopo").innerHTML = "<p>" + content[3].IDConto + "</p><p>" + content[3].saldo + "</p><p>" + content[3].proprietario + "</p>";
								break;
							case 400: // bad request
								document.getElementById("fallimento").className = "show";
								document.getElementById("showfallimento").textContent = "AntiTasto4";
								document.getElementById("errormessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("fallimento").className = "show";
								document.getElementById("showfallimento").textContent = "AntiTasto4";
								document.getElementById("errormessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("fallimento").className = "show";
								document.getElementById("showfallimento").textContent = "AntiTasto4";
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

document.getElementById("logout").addEventListener('click', (e) => {
	makeCall("GET", 'Logout', null, function(x) {
		if (x.readyState == XMLHttpRequest.DONE) {
			switch (x.status) {
				case 200:
					sessionStorage.clear;
					window.location.href = "index.html";
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
});