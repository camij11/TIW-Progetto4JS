package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.TIW.progetto4.beans.*;
import it.polimi.TIW.progetto4.DAO.*;
import it.polimi.TIW.progetto4.util.*;

@WebServlet("/CheckConto")
@MultipartConfig
public class CheckConto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    
    public CheckConto() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String usernameDestinatario;
		int IDContoDestinazione;
		String causale;
		int importo;
		int IDContoOrigine;
		
		Utente utente = (Utente)request.getSession().getAttribute("user");
		if(utente!=null) {
			try {
				IDContoOrigine = Integer.parseInt(request.getParameter("IDContoOrigine"));
				usernameDestinatario = request.getParameter("usernameDestinatario");
				IDContoDestinazione = Integer.parseInt(request.getParameter("IDContoDestinazione"));
				causale = request.getParameter("causale");
				importo = Integer.parseInt(request.getParameter("importo"));				
				Pattern p = Pattern.compile("^[a-zA-Z0-9.!#$%&’+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)$", Pattern.CASE_INSENSITIVE);
		        Matcher matcher = p.matcher(usernameDestinatario);
				if(!matcher.find()) {
					throw new Exception("Indirizzo email non valido");
				}
				
				if (usernameDestinatario == null || usernameDestinatario.isEmpty()) {
					throw new Exception("Username nullo o vuoto");
				}
				
				if (causale == null || causale.isEmpty()) {
					throw new Exception("Causale nulla o vuota");
				}
				
				if (importo <= 0) {
					throw new Exception("Importo nullo o negativo");
				}
				
				if (IDContoOrigine == IDContoDestinazione) {
					throw new Exception("Conto di origine uguale a conto di destinazione");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(e.getMessage());
				return;		
			}
			
			DAO_Conto DAOConto = new DAO_Conto(connection);
			Conto contoDestinazione = null;
			Conto contoOrigine = null;
			try {
				contoDestinazione = DAOConto.checkProprietà(IDContoDestinazione, usernameDestinatario);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossibile controllare la proprietà del conto");
				return;
			}
			try {
				contoOrigine = DAOConto.checkProprietà(IDContoOrigine, utente.getUsername());
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossibile controllare la proprietà del conto");
				return;
			}
			String percorso;
			if(contoOrigine!= null && contoDestinazione !=null && contoOrigine.getSaldo()>=importo) {
				percorso = "/EseguiTransazione";
				getServletContext().getRequestDispatcher(percorso).forward(request, response);
				return;
			} 
			else if(contoOrigine==null || contoDestinazione==null){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Conto di origine o di destinazione non esistente");
				return;
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Importo maggiore del saldo disponibile");
				return;
			}
		} else {
			String percorso = "/Logout";
			getServletContext().getRequestDispatcher(percorso).forward(request, response);
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
