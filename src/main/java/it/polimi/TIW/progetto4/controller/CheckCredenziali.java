package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.TIW.progetto4.beans.*;
import it.polimi.TIW.progetto4.DAO.*;
import it.polimi.TIW.progetto4.util.ConnectionHandler;

@WebServlet("/CheckCredenziali")
@MultipartConfig

public class CheckCredenziali extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public CheckCredenziali() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = null;
		String password = null;
		
		username = request.getParameter("username");
		password = request.getParameter("password");
			
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("I campi username e password devono essere riempiti");
			return;
		 }
		
		if(password.length()>20) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("La password inserita è troppo lunga (max 20 caratteri)");
			return;
		}
		
		DAO_Utente DaoUtente = new DAO_Utente(connection);
		Utente utente = null;
		try {
			utente = DaoUtente.checkCredenziali(username, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Non è stato possible verificare le credenziali");
			return;
		}
		
		if(utente!= null) {
			request.getSession().setAttribute("user", utente);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
			response.getWriter().write(username);
		} 
		else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Username o password errati");
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
