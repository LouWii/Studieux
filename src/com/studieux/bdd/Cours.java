package com.studieux.bdd;

public class Cours {

	private String nom;
	private String type;
	private int jour;
	private long dateDebut;
	private long dateFin;
	private long heureDebut;
	private long heureFin;
	
	
	
	public Cours(String nom, String type, int jour, long dateDebut,
			long dateFin, long heureDebut, long heureFin) {

		this.nom = nom;
		this.type = type;
		this.jour = jour;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.heureDebut = heureDebut;
		this.heureFin = heureFin;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getJour() {
		return jour;
	}
	public void setJour(int jour) {
		this.jour = jour;
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
	public long getHeureDebut() {
		return heureDebut;
	}
	public void setHeureDebut(long heureDebut) {
		this.heureDebut = heureDebut;
	}
	public long getHeureFin() {
		return heureFin;
	}
	public void setHeureFin(long heureFin) {
		this.heureFin = heureFin;
	}
	
	
}
