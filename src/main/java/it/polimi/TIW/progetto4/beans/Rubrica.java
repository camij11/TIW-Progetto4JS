package it.polimi.TIW.progetto4.beans;

public class Rubrica {
	private String usernameProprietario;
	private int IDContoAssociato;
	private String usernameAssociato;
	
	public String getUsernameProprietario() {
		return usernameProprietario;
	}
	public void setUsernameProprietario(String usernameProprietario) {
		this.usernameProprietario = usernameProprietario;
	}
	public int getIDContoAssociato() {
		return IDContoAssociato;
	}
	public void setIDContoAssociato(int iDContoAssociato) {
		IDContoAssociato = iDContoAssociato;
	}
	public String getUsernameAssociato() {
		return usernameAssociato;
	}
	public void setUsernameAssociato(String usernameAssociato) {
		this.usernameAssociato = usernameAssociato;
	}
}
