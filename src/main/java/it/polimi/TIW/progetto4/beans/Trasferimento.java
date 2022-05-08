package it.polimi.TIW.progetto4.beans;

import java.util.Date;

public class Trasferimento {
	private int IDTrasferimento;
	private Date data;
	private int importo;
	private int IDContoOrigine;
	private int IDContoDestinazione;
	private String causale;
	public String getCausale() {
		return causale;
	}
	public void setCausale(String causale) {
		this.causale = causale;
	}
	public int getIDTrasferimento() {
		return IDTrasferimento;
	}
	public void setIDTrasferimento(int IDTrasferimento) {
		IDTrasferimento = IDTrasferimento;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public int getImporto() {
		return importo;
	}
	public void setImporto(int importo) {
		this.importo = importo;
	}
	public int getIDContoOrigine() {
		return IDContoOrigine;
	}
	public void setIDContoOrigine(int iDContoOrigine) {
		IDContoOrigine = iDContoOrigine;
	}
	public int getIDContoDestinazione() {
		return IDContoDestinazione;
	}
	public void setIDContoDestinazione(int iDContoDestinazione) {
		IDContoDestinazione = iDContoDestinazione;
	}

	
}
