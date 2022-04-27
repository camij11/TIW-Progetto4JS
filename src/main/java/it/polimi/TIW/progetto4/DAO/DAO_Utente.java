package it.polimi.TIW.progetto4.DAO;
import it.polimi.TIW.progetto4.beans.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO_Utente {
	private Connection connessione;
	
	public DAO_Utente(Connection connessione) {
		this.connessione = connessione;
	}
	
	public Utente checkCredenziali(String username, String password) throws SQLException {
		String query = "SELECT username, nome, cognome FROM Utente WHERE username = ? AND password = ?";
		try (PreparedStatement statement = connessione.prepareStatement(query);) {
			statement.setString(1, username);
			statement.setString(2, password);
			try (ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					Utente utente = new Utente();
					utente.setUsername(result.getString("username"));
					utente.setNome(result.getString("nome"));
					utente.setCognome(result.getString("cognome"));
					return utente;
				}
				else {
					return null;
				}
			}
		}
	}
	
}
