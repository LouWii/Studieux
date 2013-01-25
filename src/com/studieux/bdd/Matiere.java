package com.studieux.bdd;

public class Matiere {

	private int id;
	private String nom;
	private float moyenne;
	private float coef;
	private int id_Periode;
	
	
	public Matiere(String nom, float moyenne, float coef) {
		super();
		this.nom = nom;
		this.moyenne = moyenne;
		this.coef = coef;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public float getMoyenne() {
		return moyenne;
	}
	public void setMoyenne(float moyenne) {
		this.moyenne = moyenne;
	}
	public float getCoef() {
		return coef;
	}
	public void setCoef(float coef) {
		this.coef = coef;
	}

	public int getId_Periode() {
		return id_Periode;
	}

	public void setId_Periode(int id_Periode) {
		this.id_Periode = id_Periode;
	}
	
	
}
