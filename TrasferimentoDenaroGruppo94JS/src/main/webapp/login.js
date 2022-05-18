
document.getElementById("loginbutton").addEventListener('click', (e) => {
	var form = e.target.closest("form");
    if (form.checkValidity()) {
      utils.makeCall("POST",'CheckCredenziali', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
            	sessionStorage.setItem('username', message);
                window.location.href = "HomePage.html";
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
    	 form.reportValidity();
    }
});

document.getElementById("registerbutton").addEventListener('click', (e) => {
	var form = e.target.closest("form");
	var password = form.querySelector('input[name="password"]').value;
	var repeatpassword = form.querySelector('input[name="repeatpassword"]').value;
	var email = form.querySelector('input[name="username"]').value;
	console.log(email);
    if (form.checkValidity()) {
	console.log(password==repeatpassword);
	if(password == repeatpassword && validateEmail(email) ) {
		utils.makeCall("POST", 'RegistraUtente',e.target.closest("form"),
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
 
 function validateEmail(email) {
    var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    console.log(re.test(email));
    return re.test(email);
 }
 