package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.TIW.progetto4.util.ConnectionHandler;
import it.polimi.TIW.progetto4.DAO.DAO_Trasferimento;
import it.polimi.TIW.progetto4.DAO.DAO_Conto;
import it.polimi.TIW.progetto4.beans.Conto;

/**
 * Servlet implementation class EseguiTransazione
 */
@WebServlet("/EseguiTransazione")
public class EseguiTransazione extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EseguiTransazione() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath()).append("by the get");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("chiamata EseguiTransazione");
		DAO_Trasferimento DAOTrasferimento = new DAO_Trasferimento(connection);
		DAO_Conto DAOConto = new DAO_Conto(connection);
		Conto contoOriginePrima, contoOrigineDopo, contoDestinazionePrima, contoDestinazioneDopo;
		int risultato = 0;
		int IDContoOrigine = Integer.parseInt(request.getParameter("IDContoOrigine"));
		int IDContoDestinazione = Integer.parseInt(request.getParameter("IDContoDestinazione"));
		try {
			contoOriginePrima = DAOConto.getContoByID(IDContoOrigine);
			contoDestinazionePrima = DAOConto.getContoByID(IDContoDestinazione);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile estrarre i conti prima della transazione");
			return;
		}
		int importo = Integer.parseInt(request.getParameter("importo"));
		String causale = request.getParameter("causale");
		try {
			risultato = DAOTrasferimento.eseguiTransazione(IDContoOrigine, IDContoDestinazione, importo, causale); 
			if (risultato == 1) {
				System.out.println("Transazione andata a buon fine");
			} else {
				System.out.println("Impossibile eseguire la transazione");
			}
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare la proprietà del conto");
			return;
		}
		
		try {
			contoOrigineDopo = DAOConto.getContoByID(IDContoOrigine);
			contoDestinazioneDopo = DAOConto.getContoByID(IDContoDestinazione);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile estrarre i conti dopo la transazione");
			return;
		}
		
		if(risultato == 1) {
			ServletContext servletContext = getServletContext();
			Collection valori = new ArrayList();
			valori.add(contoOriginePrima);
			valori.add(contoOrigineDopo);
			valori.add(contoDestinazionePrima);
			valori.add(contoDestinazioneDopo);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    String json = new Gson().toJson(valori);
		    System.out.println(json);
		    response.getWriter().write(json);
		} 
		else {
			//ctx.setVariable("errorMsg", "Nope");
		}
	}

}
