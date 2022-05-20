var rubrica = null;
var usernameInRubrica = [];

window.addEventListener("load", () => {
	if (sessionStorage.getItem("username") == null) {
		window.location.href = "index.html";
	} else {
		getContiUtente();
		getRubrica();
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

function getRubrica() {
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
	var IDConto = form.querySelector('select[name="conto"]').value
	if (form.checkValidity()) {
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
	console.log(usernameDestinatario);
	console.log(IDContoOrigine);
	if (form.checkValidity()){
		clearMessages();
		if (validateEmail(usernameDestinatario) && IDContoOrigine != IDContoDestinazione && !isNaN(IDContoDestinazione) && IDContoOrigine != null && IDContoDestinazione != null && !isNaN(IDContoOrigine) && causale != null && importo > 0 && !isNaN(importo)) {
			makeCall("POST", 'CheckConto', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								var content = JSON.parse(message);
								document.getElementById("successo").className = "show";
								document.getElementById("contoorigineprima").innerHTML = '<p>' + content[0].IDConto + '</p><p>' + content[0].saldo + '</p><p id = "usernameproprietario" >' + content[0].proprietario + '</p>';
								document.getElementById("contodestinazioneprima").innerHTML = '<p id = "contoassociato" >' + content[2].IDConto + '</p><p>' + content[2].saldo + '</p><p id = "usernameassociato">' + content[2].proprietario + '</p>';
								document.getElementById("contooriginedopo").innerHTML = "<p>" + content[1].IDConto + "</p><p>" + content[1].saldo + "</p><p>" + content[1].proprietario + "</p>";
								document.getElementById("contodestinazionedopo").innerHTML = "<p>" + content[3].IDConto + "</p><p>" + content[3].saldo + "</p><p>" + content[3].proprietario + "</p>";
								break;
							case 400: // bad request
								document.getElementById("fallimento").className = "show";
								document.getElementById("errormessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("fallimento").className = "show";
								document.getElementById("errormessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("fallimento").className = "show";
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

document.getElementById("aggiungiinrubrica").addEventListener('click', (e) => {
	var form = new FormData();
	form.append('IDContoAssociato',document.getElementById("contoassociato").textContent);
	form.append('UsernameProprietario',document.getElementById("usernameproprietario").textContent);
	form.append('UsernameAssociato',document.getElementById("usernameassociato").textContent);
	console.log(document.getElementById("usernameassociato").textContent);
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

document.getElementById("inputUsername").addEventListener('input', (e)=>{
	var input = document.getElementById("inputUsername").value;
	document.getElementById("autocomplete").innerHTML = "";
	for(let i=0; i<usernameInRubrica.length; i++) {
		console.log(input);
		if(usernameInRubrica[i].startsWith(input) && input!=null){
			console.log(usernameInRubrica[i]);
			document.getElementById("autocomplete").innerHTML += '<option value="'+usernameInRubrica[i]+'" id="'+usernameInRubrica[i]+'">'+usernameInRubrica[i]+'</option>';
			document.getElementById(""+usernameInRubrica[i]+"").addEventListener('click',(e)=>{
				document.getElementById("inputUsername").value = usernameInRubrica[i];
			})
		}
	}
});

function autocomplete(inp, arr) {
  /*the autocomplete function takes two arguments,
  the text field element and an array of possible autocompleted values:*/
  var currentFocus;
  /*execute a function when someone writes in the text field:*/
  inp.addEventListener("input", function(e) {
      var a, b, i, val = this.value;
      /*close any already open lists of autocompleted values*/
      closeAllLists();
      if (!val) { return false;}
      currentFocus = -1;
      /*create a DIV element that will contain the items (values):*/
      a = document.createElement("DIV");
      a.setAttribute("id", this.id + "autocomplete-list");
      a.setAttribute("class", "autocomplete-items");
      /*append the DIV element as a child of the autocomplete container:*/
      this.parentNode.appendChild(a);
      /*for each item in the array...*/
      for (i = 0; i < arr.length; i++) {
        /*check if the item starts with the same letters as the text field value:*/
        if (arr[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
          /*create a DIV element for each matching element:*/
          b = document.createElement("DIV");
          /*make the matching letters bold:*/
          b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
          b.innerHTML += arr[i].substr(val.length);
          /*insert a input field that will hold the current array item's value:*/
          b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
          /*execute a function when someone clicks on the item value (DIV element):*/
              b.addEventListener("click", function(e) {
              /*insert the value for the autocomplete text field:*/
              inp.value = this.getElementsByTagName("input")[0].value;
              /*close the list of autocompleted values,
              (or any other open lists of autocompleted values:*/
              closeAllLists();
          });
          a.appendChild(b);
        }
      }
  });
  /*execute a function presses a key on the keyboard:*/
  inp.addEventListener("keydown", function(e) {
      var x = document.getElementById(this.id + "autocomplete-list");
      if (x) x = x.getElementsByTagName("div");
      if (e.keyCode == 40) {
        /*If the arrow DOWN key is pressed,
        increase the currentFocus variable:*/
        currentFocus++;
        /*and and make the current item more visible:*/
        addActive(x);
      } else if (e.keyCode == 38) { //up
        /*If the arrow UP key is pressed,
        decrease the currentFocus variable:*/
        currentFocus--;
        /*and and make the current item more visible:*/
        addActive(x);
      } else if (e.keyCode == 13) {
        /*If the ENTER key is pressed, prevent the form from being submitted,*/
        e.preventDefault();
        if (currentFocus > -1) {
          /*and simulate a click on the "active" item:*/
          if (x) x[currentFocus].click();
        }
      }
  });
  function addActive(x) {
    /*a function to classify an item as "active":*/
    if (!x) return false;
    /*start by removing the "active" class on all items:*/
    removeActive(x);
    if (currentFocus >= x.length) currentFocus = 0;
    if (currentFocus < 0) currentFocus = (x.length - 1);
    /*add class "autocomplete-active":*/
    x[currentFocus].classList.add("autocomplete-active");
  }
  function removeActive(x) {
    /*a function to remove the "active" class from all autocomplete items:*/
    for (var i = 0; i < x.length; i++) {
      x[i].classList.remove("autocomplete-active");
    }
  }
  function closeAllLists(elmnt) {
    /*close all autocomplete lists in the document,
    except the one passed as an argument:*/
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
      if (elmnt != x[i] && elmnt != inp) {
      x[i].parentNode.removeChild(x[i]);
    }
  }
}
/*execute a function when someone clicks in the document:*/
document.addEventListener("click", function (e) {
    closeAllLists(e.target);
});
}