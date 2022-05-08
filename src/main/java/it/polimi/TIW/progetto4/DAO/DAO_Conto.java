package it.polimi.TIW.progetto4.DAO;
import it.polimi.TIW.progetto4.beans.Conto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAO_Conto {
	private Connection connessione;
	
	public DAO_Conto(Connection connessione) {
		this.connessione = connessione;
	}
	
	public Conto checkProprietà(int IDConto, String username) throws SQLException {
		String query = "SELECT IDConto, saldo, Intestatario FROM Conto WHERE IDConto = ? AND Intestatario  = ?";
		try (PreparedStatement statement = connessione.prepareStatement(query);) {
			statement.setInt(1, IDConto);
			statement.setString(2, username);
			try (ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					Conto conto = new Conto();
					conto.setIDConto(result.getInt("IDconto"));
					conto.setSaldo(result.getInt("saldo"));
					conto.setProprietario(result.getString("Intestatario"));
					return conto;
				}
				else {
					return null;
				}
			}
		}
	}
	
	public Conto checkSaldo(int IDConto) throws SQLException {
		String query = "SELECT IDConto, saldo, Intestatario FROM Conto WHERE IDConto = ?";
		try (PreparedStatement statement = connessione.prepareStatement(query);) {
			statement.setInt(1, IDConto);
			try (ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					Conto conto = new Conto();
					conto.setIDConto(result.getInt("IDconto"));
					conto.setSaldo(result.getInt("saldo"));
					conto.setProprietario(result.getString("Intestatario"));
					return conto;
				}
				else {
					return null;
				}
			}
		}
	}
	
	public List<Integer> getContiUtente(String username) throws SQLException {
		String query = "SELECT IDConto FROM Conto WHERE Intestatario = ?";
		List<Integer> ElencoConti = new ArrayList<>();
		try(PreparedStatement statement = connessione.prepareStatement(query);) {
			statement.setString(1, username);
			try(ResultSet result = statement.executeQuery();) {
				while (result.next()) {
					  int conto = result.getInt("IDConto");
					  ElencoConti.add(conto);
			    }
		    }
		return ElencoConti;
	    }
	}
}
