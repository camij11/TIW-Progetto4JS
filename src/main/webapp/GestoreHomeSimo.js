
document.getElementById("selectionbutton").addEventListener('click', (e) => {
	var prova = document.getElementById("prova");
	prova.textContent = "ciao";
});

document.getElementById("showinfoconto").addEventListener('click', (e) => {
	var IDConto = 1;
	var data = new FormData();
	data.append("conto", IDConto);
	makeCall("Post", 'SelezionaConto', data,
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
		});

	if (document.getElementById("showinfoconto").textContent === "Tasto2") {
		document.getElementById("infoconto").className = "show";
		document.getElementById("showinfoconto").textContent = "AntiTasto2";
	} else {
		document.getElementById("infoconto").className = "hide";
		document.getElementById("showinfoconto").textContent = "Tasto2";
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
								document.getElementById("contoorigineprima").innerHTML ="<p>"+content[0].IDConto+"</p><p>"+content[0].saldo+"</p><p>"+content[0].proprietario+"</p>";
								document.getElementById("contodestinazioneprima").innerHTML ="<p>"+content[2].IDConto+"</p><p>"+content[2].saldo+"</p><p>"+content[1].proprietario+"</p>";
								document.getElementById("contooriginedopo").innerHTML ="<p>"+content[1].IDConto+"</p><p>"+content[1].saldo+"</p><p>"+content[2].proprietario+"</p>";
								document.getElementById("contodestinazionedopo").innerHTML ="<p>"+content[3].IDConto+"</p><p>"+content[3].saldo+"</p><p>"+content[3].proprietario+"</p>";
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

document.getElementById("showsuccesso").addEventListener('click', (e) => {
	if (document.getElementById("showsuccesso").textContent === "Tasto3") {
		document.getElementById("successo").className = "show";
		document.getElementById("showsuccesso").textContent = "AntiTasto3";
	} else {
		document.getElementById("successo").className = "hide";
		document.getElementById("showsuccesso").textContent = "Tasto3";
	}
});

document.getElementById("showfallimento").addEventListener('click', (e) => {
	if (document.getElementById("showfallimento").textContent === "Tasto4") {
		document.getElementById("fallimento").className = "show";
		document.getElementById("showfallimento").textContent = "AntiTasto4";
	} else {
		document.getElementById("fallimento").className = "hide";
		document.getElementById("showfallimento").textContent = "Tasto4";
	}

});

document.getElementById("showhomepage").addEventListener('click', (e) => {
	if (document.getElementById("showhomepage").textContent === "Tasto1") {
		document.getElementById("homepage").className = "show";
		document.getElementById("showhomepage").textContent = "AntiTasto1";
	} else {
		document.getElementById("homepage").className = "hide";
		document.getElementById("showhomepage").textContent = "Tasto1";
	}
});

function clearMessages() {
	document.getElementById("errormessage").innerHTML = "";
	document.getElementById("successmessage").innerHTML = "";
}

function validateEmail(email) {
	var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	console.log(re.test(email));
	return re.test(email);
}