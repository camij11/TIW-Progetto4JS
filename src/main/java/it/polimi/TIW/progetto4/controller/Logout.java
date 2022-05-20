package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;

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

import it.polimi.TIW.progetto4.util.ConnectionHandler;

@WebServlet("/Logout")
@MultipartConfig

public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connessione;
	
    public Logout() {
        super();
    }
    
    public void init() throws ServletException {
		connessione = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
