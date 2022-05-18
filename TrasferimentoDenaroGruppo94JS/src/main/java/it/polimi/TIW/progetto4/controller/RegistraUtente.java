package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.TIW.progetto4.DAO.DAO_Utente;
import it.polimi.TIW.progetto4.beans.Utente;
import it.polimi.TIW.progetto4.util.ConnectionHandler;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@WebServlet("/RegistraUtente")
@MultipartConfig

public class RegistraUtente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;       
	
    public RegistraUtente() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = null;
        String surname = null;
        String username = null;
        String password = null;
        String repeatpassword = null;
        String percorso;
	
		name = request.getParameter("name");
		surname = request.getParameter("surname");
		username = request.getParameter("username");
		password = request.getParameter("password");
		repeatpassword = request.getParameter("repeatpassword");
		
		if(name == null || surname == null || username == null || password == null || repeatpassword == null || name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || repeatpassword.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("I campi username e password devono essere riempiti");
			return;
	    } 
	
		if(password.length()>20) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("La password non può essere più lunga di 20 caratteri");
			return;
		}

	    if(!password.equals(repeatpassword)) { 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("I campi password e repeatpassword non corrispondono");
			return;
		}
		
		Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = p.matcher(username);
    
	    if(!matcher.find()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Il campo username deve essere un indirizzo email");
			return;
		}

	    DAO_Utente DaoUtente = new DAO_Utente(connection);
	    Utente utente = null;
	    String usernameEsistente = null;
	    try{
	    	usernameEsistente = DaoUtente.checkUsername(name, surname, username, password);
	    } catch (Exception e) {
	    	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Non è stato possibile accedere alla base di dati");
	    }
	    if(usernameEsistente == null) {
	    	try{
	    		utente = DaoUtente.registraUtente(username, password, name, surname);
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Non è stato possibile accedere alla base di dati");
			};	
	    } else {
	    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Username già in uso");
			return;
	      }
	
	
	    if(utente!= null) {
	    	response.setStatus(HttpServletResponse.SC_OK);
	    	response.getWriter().println("Registrazione avvenuta con successo");
			
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Non è stato possibile effettuare la registrazione");
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
