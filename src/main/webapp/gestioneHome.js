var rubrica = null;
var usernameInRubrica = [];
var stati = [];

window.addEventListener("load", () => {
	if (sessionStorage.getItem("username") == null) {
		window.location.href = "index.html";
	} else {
		getContiUtente();
		getRubrica();
	}
});

function getContiUtente() {
	clearMessages();
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
								text: 'IDConto: '+conti,
							}).appendTo(select);
						}
						break;
					case 400: // bad request
						document.getElementById("errormessage").textContent = message;
						break;
					case 401: // unauthorized
						document.getElementById("errormessage").textContent = message;
						break;
					case 440:
						logout(message);
						break;
					case 500: // server error
						document.getElementById("errormessage").textContent = message;
						break;
				}
			}
		}
	);
}

document.getElementById("logout").addEventListener("click", (e) => {
	var message = "Logout effettuato";
	logout(message);
});

function logout(message) {
	sessionStorage.clear();
	window.location.href = "index.html";
	clearMessages();
	document.getElementById("successmessage").innerHTML = message;
}

function getRubrica() {
	clearMessages();
	makeCall("GET", 'GetRubrica', null,
		function(x) {
			if (x.readyState == XMLHttpRequest.DONE) {
				var message = x.responseText;
				switch (x.status) {
					case 200:
						rubrica = JSON.parse(message);
						for(let i=0; i<rubrica.length;i++){
							usernameInRubrica.push(rubrica[i].usernameAssociato);
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
	var IDConto = form.querySelector('select[name="conto"]').value;
	if (form.checkValidity()) {
		addToHistory();
		clearMessages();
		if(!isNaN(IDConto) && IDConto > 0) {
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
							document.getElementById("contoorigineform").value = IDConto;
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
							document.getElementById("infoconto").className = "show";
							document.getElementById("elencoconti").className = "hide elencoConti";
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
		)
		} else {
			document.getElementById("errormessage").textContent = "IDConto non valido";
		}
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
	if (form.checkValidity()){
		addToHistory();
		clearMessages();
		if (validateEmail(usernameDestinatario) && IDContoOrigine != IDContoDestinazione && !isNaN(IDContoDestinazione) && IDContoOrigine != null && IDContoDestinazione != null && !isNaN(IDContoOrigine) && causale != null && importo > 0 && !isNaN(importo)) {
			makeCall("POST", 'CheckConto', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								var content = JSON.parse(message);
								document.getElementById("infoconto").className = "hide";
								document.getElementById("successo").className = "esito show";
								document.getElementById("contoorigineprima").innerHTML = '<p>' + content[0].IDConto + '</p><p>' + content[0].saldo + '</p><p id = "usernameproprietario" >' + content[0].proprietario + '</p>';
								document.getElementById("contodestinazioneprima").innerHTML = '<p id = "contoassociato" >' + content[2].IDConto + '</p><p>' + content[2].saldo + '</p><p id = "usernameassociato">' + content[2].proprietario + '</p>';
								document.getElementById("contooriginedopo").innerHTML = "<p>" + content[1].IDConto + "</p><p>" + content[1].saldo + "</p><p>" + content[1].proprietario + "</p>";
								document.getElementById("contodestinazionedopo").innerHTML = "<p>" + content[3].IDConto + "</p><p>" + content[3].saldo + "</p><p>" + content[3].proprietario + "</p>";
								break;
							case 400: // bad request
								window.alert(message);
								break;
							case 401: // unauthorized
								window.alert(message);
								break;
							case 500: // server error
								window.alert(message);
								break;
						}
					}
				});
		} else {
			window.alert("campi errati");
		}
	} else {
		form.reportValidity();
	}
});

document.getElementById("logout").addEventListener('click', (e) => {
	clearMessages();
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

document.getElementById("aggiungiinrubrica").addEventListener('click', (e) => {
	var form = new FormData();
	form.append('IDContoAssociato',document.getElementById("contoassociato").textContent);
	form.append('UsernameProprietario',document.getElementById("usernameproprietario").textContent);
	form.append('UsernameAssociato',document.getElementById("usernameassociato").textContent);
		makeCall("POST", 'AggiungiInRubrica', form, 
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
		}
	);	
});

document.getElementById("goBack").addEventListener('click', (e) =>{
	if(stati.length == 0){
		window.history.back();
	} else {
		stati.pop();
	    document.location.reload();
	  }
});

function addToHistory() {
	stati.push(document.querySelector("html").innerHTML);
	console.log(stati);
}

function autocompleteMatch(input) {
  if (input == '') {
    return [];
  }
  var reg = new RegExp(input)
  return usernameInRubrica.filter(function(term) {
	  if (term.match(reg)) {
  	  return term;
	  }
  });
}
 
function showResult(val) {
  res = document.getElementById("result");
  res.innerHTML = '';
  let list = '';
  let terms = autocompleteMatch(val);
  for (i=0; i<terms.length; i++) {
    list += '<option id="complete">' + terms[i] + '</option>';
  }
  res.size = terms.length;
  res.innerHTML = list;
}

document.getElementById("result").addEventListener('change', (e)=>{
	document.getElementById("inputUsername").value = e.target.value; 
});

document.getElementById("result").addEventListener('focus', (e)=>{
	document.getElementById("inputUsername").value = e.target.value; 
});
