package com.studieux.bdd;

import java.util.Date;

public class Periode {

	private int id;
	private long dateDebut;
	private long dateFin;
	private String nom;
	
	public Periode(String nom, Date dateDebut, Date dateFin)
	{
		this.nom = nom;
		this.dateDebut = dateDebut.getTime();
		this.dateFin = dateFin.getTime();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public long getDateDebut() {
		return dateDebut;
	}
	
	public void setDateDebut(long dateDebut) {
		this.dateDebut = dateDebut;
	}
	
	public long getDateFin() {
		return dateFin;
	}
	
	public void setDateFin(long dateFin) {
		this.dateFin = dateFin;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}	
	
}
