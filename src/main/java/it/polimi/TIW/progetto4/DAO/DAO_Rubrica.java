package it.polimi.TIW.progetto4.DAO;

import it.polimi.TIW.progetto4.beans.Rubrica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO_Rubrica {
	private Connection connessione;
	
	public DAO_Rubrica(Connection connessione) {
		this.connessione = connessione;
	}
	
	public ArrayList<Rubrica> getRubricaByUsername(String Username) throws SQLException {
		ArrayList<Rubrica> risultato = new ArrayList<>();
		
		String query = "SELECT * FROM Rubrica WHERE UsernameProprietario = ?";
		ResultSet result = null;
		PreparedStatement statement = null;
		try {
			statement = connessione.prepareStatement(query);
			statement.setString(1, Username);
			result = statement.executeQuery();
			
			while(result.next()) {
				Rubrica r = new Rubrica();
				r.setUsernameProprietario(Username);
				r.setIDContoAssociato(result.getInt("IDContoAssociato"));
				r.setUsernameAssociato(result.getString("UsernameAssociato"));
				
				risultato.add(r);
			}
		} catch (SQLException e) {
			throw e;
		}
		if(risultato.size()>0) {
			return risultato;
		}else {
			return null;
		}
		
	}
	
	public int addToRubrica(String UsernameProprietario, int IDContoAssociato, String UsernameAssociato) throws SQLException {
		String query ="INSERT INTO Rubrica VALUES(?,?,?)";
		int result = 0;
		PreparedStatement statement = null;
		try {
			statement = connessione.prepareStatement(query);
			statement.setString(1, UsernameProprietario);
			statement.setInt(2, IDContoAssociato);
			statement.setString(3, UsernameAssociato);
			result = statement.executeUpdate();	
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}
	
	public Rubrica checkRubrica(String UsernameAssociato, int IDContoAssociato) throws SQLException{
		String query = "SELECT * FROM Rubrica WHERE IDContoAssociato = ? AND UsernameAssociato = ?";
		try (PreparedStatement statement = connessione.prepareStatement(query);) {
			statement.setString(1, UsernameAssociato);
			statement.setInt(2, IDContoAssociato);
			try (ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					Rubrica rubrica = new Rubrica();
					rubrica.setUsernameProprietario(result.getString("UsernameProprietario"));
					rubrica.setIDContoAssociato(result.getInt("IDContoAssociato"));
					rubrica.setUsernameAssociato(result.getString("UsernameAssociato"));
					return rubrica;
				}
				else {
					return null;
				}
			}
		}
	}
}
