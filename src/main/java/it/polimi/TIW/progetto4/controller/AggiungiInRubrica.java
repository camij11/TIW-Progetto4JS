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

import it.polimi.TIW.progetto4.DAO.DAO_Rubrica;
import it.polimi.TIW.progetto4.beans.Utente;
import it.polimi.TIW.progetto4.util.ConnectionHandler;


@WebServlet("/AggiungiInRubrica")
@MultipartConfig
public class AggiungiInRubrica extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	
    public AggiungiInRubrica() {
        super();
    }

    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext()); 
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utente utenteCorr = (Utente) request.getSession().getAttribute("user");
		String UsernameProprietario = request.getParameter("UsernameProprietario");
		int IDContoAssociato = Integer.parseInt(request.getParameter("IDContoAssociato"));
		String UsernameAssociato = request.getParameter("UsernameAssociato");
		
		if(UsernameProprietario == null || UsernameAssociato == null || UsernameProprietario.isEmpty() || UsernameAssociato.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("I campi usernameProprietario e usernameAssociato devono essere riempiti");
			return;
		}
		
		if(IDContoAssociato<=0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Inserire un IDConto valido");
			return;
		}
		if(!utenteCorr.getUsername().equals(UsernameProprietario)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("L'usernameProprietario non coincide con quello in sessione");
			return;
		}
		
		DAO_Rubrica DaoRubrica = new DAO_Rubrica(connection);
		int esito = 0;
		try {
			esito = DaoRubrica.addToRubrica(UsernameProprietario, IDContoAssociato, UsernameAssociato);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile aggiungere in rubrica");
			return;
		}
		
		if(esito==1) {
			response.setStatus(HttpServletResponse.SC_OK);
	    	response.getWriter().println("Conto registrato in rubrica con successo");
		}else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    	response.getWriter().println("Non è stato possibile aggiungere il conto in rubrica");
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
