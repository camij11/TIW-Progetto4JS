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

import it.polimi.TIW.progetto4.beans.*;
import it.polimi.TIW.progetto4.DAO.*;
import it.polimi.TIW.progetto4.util.*;
/**
 * Servlet implementation class CheckConto
 */
@WebServlet("/CheckConto")
@MultipartConfig
public class CheckConto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckConto() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("chiamata CheckConto");
		String usernameDestinatario;
		int IDContoDestinazione;
		String causale;
		int importo;
		int IDContoOrigine;
		
		try {
			usernameDestinatario = request.getParameter("usernameDestinatario");
			IDContoDestinazione = Integer.parseInt(request.getParameter("IDContoDestinazione"));
			causale = request.getParameter("causale");
			importo = Integer.parseInt(request.getParameter("importo"));
			IDContoOrigine = Integer.parseInt(request.getParameter("IDContoOrigine"));
			
			if (usernameDestinatario == null || usernameDestinatario.isEmpty()) {
				throw new Exception("Username nullo o vuoto");
			}
			
			if (causale == null || causale.isEmpty()) {
				throw new Exception("Causale nullo o vuoto");
			}
			
			if (importo <= 0) {
				throw new Exception("Importo nullo o negativo");
			}
			
			if (IDContoOrigine == IDContoDestinazione) {
				throw new Exception("Conto di origine uguale a conto di destinazione");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Qualcosa è andato storto nel trasferimento");
			return;
			
		}
		
		DAO_Conto DAOConto = new DAO_Conto(connection);
		Conto contoDestinazione = null;
		Conto contoOrigine = null;
		try {
			contoDestinazione = DAOConto.checkProprietà(IDContoDestinazione, usernameDestinatario);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare la proprietà del conto");
			return;
		}
		try {
			contoOrigine = DAOConto.checkSaldo(IDContoOrigine);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare il saldo del conto");
			return;
		}
		
		String percorso;
		if(contoOrigine!= null && contoDestinazione !=null && contoOrigine.getSaldo()>=importo) {
			percorso = "/EseguiTransazione";
			getServletContext().getRequestDispatcher(percorso).forward(request, response);
		} 
		else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
