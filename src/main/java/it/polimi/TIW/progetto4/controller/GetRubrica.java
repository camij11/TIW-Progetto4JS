package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.TIW.progetto4.DAO.DAO_Rubrica;
import it.polimi.TIW.progetto4.beans.Rubrica;
import it.polimi.TIW.progetto4.beans.Utente;
import it.polimi.TIW.progetto4.util.ConnectionHandler;


@WebServlet("/GetRubrica")
@MultipartConfig
public class GetRubrica extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetRubrica() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utente utenteCorr = (Utente)request.getSession().getAttribute("user");
		if(utenteCorr != null) {
			String usernameCorr = utenteCorr.getUsername();
			DAO_Rubrica DaoRubrica = new DAO_Rubrica(connection);
			ArrayList<Rubrica> rubriche = new ArrayList<>();
			try{
				rubriche = DaoRubrica.getRubricaByUsername(usernameCorr);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossibile accedere alla base di dati");
				return;
			}
			response.setStatus(HttpServletResponse.SC_OK);
			if(rubriche != null) {
				response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    String json = new Gson().toJson(rubriche);
			    response.getWriter().write(json);
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
