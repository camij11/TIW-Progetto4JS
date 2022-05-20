package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.TIW.progetto4.DAO.DAO_Conto;
import it.polimi.TIW.progetto4.DAO.DAO_Trasferimento;
import it.polimi.TIW.progetto4.beans.Conto;
import it.polimi.TIW.progetto4.beans.Trasferimento;
import it.polimi.TIW.progetto4.util.ConnectionHandler;

import java.util.ArrayList;

/**
 * Servlet implementation class SelezionaConto
 */
@WebServlet("/SelezionaConto")
@MultipartConfig
public class SelezionaConto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public SelezionaConto() {
        super();
    }

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int IDConto = 0;
		DAO_Conto DaoConto = new DAO_Conto(connection);
		DAO_Trasferimento DaoTrasferimento = new DAO_Trasferimento(connection);
		Conto conto;
		try{
			IDConto = Integer.parseInt(request.getParameter("conto"));
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Nessun conto selezionato");
			return;
		}
		
		try{
			conto = DaoConto.getContoByID(IDConto);
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile selezionare conto desiderato");
			return;
		}
		
		ArrayList<Trasferimento> listaTrasferimenti = new ArrayList<>();
		
		try {
			listaTrasferimenti = DaoTrasferimento.trovaTrasferimentiByIDConto(IDConto);
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile accedere ai dati del conto");
			return;
		}
		
		if(conto != null) {
			Collection risultato = new ArrayList();
			risultato.add(conto);
			risultato.add(listaTrasferimenti);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    String json = new Gson().toJson(risultato);
		    response.getWriter().write(json);
		    
		} 
		else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Conto inesistente");
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
