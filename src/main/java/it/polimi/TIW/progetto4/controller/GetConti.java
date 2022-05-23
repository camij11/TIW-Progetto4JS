
package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.TIW.progetto4.DAO.DAO_Conto;
import it.polimi.TIW.progetto4.beans.Utente;
import it.polimi.TIW.progetto4.util.ConnectionHandler;

@WebServlet("/GetConti")
@MultipartConfig

public class GetConti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetConti() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utente utenteCorr = (Utente)request.getSession().getAttribute("user");
		DAO_Conto DaoConto = new DAO_Conto(connection);
		List<Integer> ElencoConti;
		if(utenteCorr!=null) {
			try { 
				ElencoConti = DaoConto.getContiUtente(utenteCorr.getUsername());
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Impossibile ottenere i conti associati all'utente loggato");
					return;
				}
			if(ElencoConti.size()== 0) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Non è presente nessun conto associato a questo account");
				return;
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    String json = new Gson().toJson(ElencoConti);
				response.getWriter().write(json);
				return;
			  }			
		}
		else {
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