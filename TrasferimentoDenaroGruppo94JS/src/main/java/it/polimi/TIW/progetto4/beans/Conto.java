package it.polimi.TIW.progetto4.beans;

public class Conto {
	private int IDConto;
	private int saldo;
	private String proprietario;
	
	public int getIDConto() {
		return IDConto;
	}
	public void setIDConto(int iDConto) {
		IDConto = iDConto;
	}
	public int getSaldo() {
		return saldo;
	}
	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}
	public String getProprietario() {
		return proprietario;
	}
	public void setProprietario(String proprietario) {
		this.proprietario = proprietario;
	}	
}
