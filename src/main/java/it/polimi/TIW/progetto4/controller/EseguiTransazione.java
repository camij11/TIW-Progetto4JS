package it.polimi.TIW.progetto4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.TIW.progetto4.util.ConnectionHandler;
import it.polimi.TIW.progetto4.DAO.DAO_Trasferimento;

/**
 * Servlet implementation class EseguiTransazione
 */
@WebServlet("/EseguiTransazione")
public class EseguiTransazione extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
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
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
		DAO_Trasferimento DAOTrasferimento = new DAO_Trasferimento(connection);
		int risultato = 0;
		int IDContoOrigine = Integer.parseInt(request.getParameter("IDContoOrigine"));
		int IDContoDestinazione = Integer.parseInt(request.getParameter("IDContoDestinazione"));
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
		
		String percorso;
		if(risultato == 1) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Tutto ok");
			percorso = "/WEB-INF/successo.html";
			templateEngine.process(percorso, ctx, response.getWriter());
		} 
		else {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Nope");
			percorso = "/WEB-INF/fallimento.html";
			templateEngine.process(percorso, ctx, response.getWriter());
		}
	}

}
