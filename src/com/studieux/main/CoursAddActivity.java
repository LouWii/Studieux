package com.studieux.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.studieux.bdd.Cours;
import com.studieux.bdd.CoursDao;
import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.Matiere;
import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.Periode;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CoursAddActivity extends MenuActivity {

	static final int DATEDEBUT_DIALOG_ID = 0;
	static final int DATEFIN_DIALOG_ID = 1;
	static final int HEUREDEBUT_DIALOG_ID = 3;
	static final int HEUREFIN_DIALOG_ID = 4;
	
	String [] noms;
	private int jourSelectionne = -1;
	
	EditText dateDebutET;
	EditText dateFinET;
	EditText heureDebutET;
	EditText heureFinET;
	
	Calendar heureDebut;
	Calendar heureFin;
	
	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
    private MatiereDao matiereDao;
    private CoursDao coursDAO;
    private Matiere matiere;
    private Periode periode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cours_add);
		
		initMenu();
		View v1 = findViewById(R.id.viewRed1);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cours_add, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (matiereDao == null)//Db init
		{
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		    db = helper.getWritableDatabase();
		    daoMaster = new DaoMaster(db);
		    daoSession = daoMaster.newSession();
		    matiereDao = daoSession.getMatiereDao();
		    coursDAO = daoSession.getCoursDao();
		}
		
		Bundle donnees = getIntent().getExtras();
		//Si une période est définie
		if (matiere == null && donnees != null && donnees.containsKey("MatiereId") )
		{
			matiere = matiereDao.load(donnees.getLong("MatiereId"));
			periode = matiere.getPeriode();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dateDebutET = (EditText)findViewById(R.id.coursadd_coursdatedebut);
			dateFinET = (EditText)findViewById(R.id.coursadd_coursdatefin);
			dateDebutET.setText(formatter.format(periode.getDate_debut()));
			dateFinET.setText(formatter.format(periode.getDate_fin()));
			dateDebutET.setOnTouchListener(new OnTouchListener(){ 
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(v == dateDebutET)
					{
						showDialog(DATEDEBUT_DIALOG_ID);
					}
					return false;  
				}
			});
			dateFinET.setOnTouchListener(new OnTouchListener(){ 
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(v == dateFinET)
					{
						showDialog(DATEFIN_DIALOG_ID);
					}
					return false;  
				}
			});
			heureDebutET = (EditText)findViewById(R.id.coursadd_coursheuredebut);
			heureFinET = (EditText)findViewById(R.id.coursadd_coursheurefin);
			heureDebutET.setOnTouchListener(new OnTouchListener(){ 
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(v == heureDebutET)
					{
						showDialog(HEUREDEBUT_DIALOG_ID);
					}
					return false;  
				}
			});
			heureFinET.setOnTouchListener(new OnTouchListener(){ 
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(v == heureFinET)
					{
						showDialog(HEUREFIN_DIALOG_ID);
					}
					return false;  
				}
			});
			formatter = new SimpleDateFormat("HH:mm");
			heureDebut = Calendar.getInstance();
			heureDebut.set(1, 1, 1, 9, 0);
			heureDebutET.setText(formatter.format(heureDebut.getTime()));
			heureFin = Calendar.getInstance();
			heureFin.set(1, 1, 1, 10, 0);
			heureFinET.setText(formatter.format(heureFin.getTime()));
			//TextView nomMatiere = (TextView) findViewById(R.id.cours_matierenom);
			//nomMatiere.setText(matiere.getNom());
		}
		else if (matiere == null)//sinon, on cherche la période courante si pas de période
		{
			Toast.makeText(CoursAddActivity.this, "Pas de matière liée...", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c.setTime(periode.getDate_debut());
		c2.setTime(periode.getDate_fin());
		switch (id) {
		case DATEDEBUT_DIALOG_ID:
			return new DatePickerDialog(this,  mDateDebutSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		case DATEFIN_DIALOG_ID:
			return new DatePickerDialog(this,  mDateFinSetListener,  c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
		case HEUREDEBUT_DIALOG_ID:
			return new TimePickerDialog(this, mHeureDebutSetListener, 9, 0, true);
		case HEUREFIN_DIALOG_ID:
			return new TimePickerDialog(this, mHeureFinSetListener, 10, 0, true);
		}
		return null;
	}
	
	/**
	 * Listener Date picker pour la date de début
	 */
	private DatePickerDialog.OnDateSetListener mDateDebutSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			//On récupère les valeurs de la date et on les met dans le champ texte
			String date_selected = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1) + "/"+String.valueOf(year);
			dateDebutET.setText(date_selected);
		}
	};

	/**
	 * Listener Date picker pour la date de fin
	 */
	private DatePickerDialog.OnDateSetListener mDateFinSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			String date_selected = String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
			dateFinET.setText(date_selected);
		}
	};
	
	/**
	 * Listener Date picker pour le l'heure de début
	 */
	private TimePickerDialog.OnTimeSetListener mHeureDebutSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			heureDebut = Calendar.getInstance();
			heureDebut.set(1, 1, 1, hourOfDay, minute);
			DateFormat formatter = new SimpleDateFormat("HH:mm");
			heureDebutET.setText(formatter.format(heureDebut.getTime()));
		}
	};
	
	/**
	 * Listener Date picker pour l'heure de fin
	 */
	private TimePickerDialog.OnTimeSetListener mHeureFinSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			heureFin = Calendar.getInstance();
			heureFin.set(1, 1, 1, hourOfDay, minute);
			DateFormat formatter = new SimpleDateFormat("HH:mm");
			heureFinET.setText(formatter.format(heureFin.getTime()));
		}
	};
	
	/**
	 * Appui sur le bouton pour sélectionner un jour
	 * Affichage de la popup
	 * @param v
	 */
	public void selectionnerJour(View v)
	{
		if (noms == null)
		{
			noms = new String [7];
			noms[0] = "Lundi";
			noms[1] = "Mardi";
			noms[2] = "Mercredi";
			noms[3] = "Jeudi";
			noms[4] = "Vendredi";
			noms[5] = "Samedi";
			noms[6] = "Dimanche";
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Matieres");
		
		builder.setItems(noms, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//NoteAddActivity.this.loadMatiere(Long.parseLong(NoteAddActivity.this.data.get(which).get("id")));
				jourSelectionne = which;
				TextView jour = (TextView)findViewById(R.id.coursadd_jour);
				jour.setText(noms[which]);
			}
		});
		builder.create();         
		builder.show();	
	}
	
	/**
	 * Enregistrement du cours
	 * @param v
	 */
	public void enregistrer(View v)
	{
		//Si le type est vide, on alerte et on stoppe l'enregistrement
		TextView type = (TextView)findViewById(R.id.coursadd_courstype);
		if (type.getText().toString().equals("") )
		{
			new AlertDialog.Builder(this).setTitle("Type vide")
			.setMessage("Veuillez entrer un type pour le cours (amphi, TP, TD...)")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		
		//Si le type est vide, on alerte et on stoppe l'enregistrement
		TextView lieu = (TextView)findViewById(R.id.coursadd_courssalle);
		if (lieu.getText().toString().equals("") )
		{
			new AlertDialog.Builder(this).setTitle("Lieu vide")
			.setMessage("Veuillez entrer un lieu pour le cours (bât. 3 Salle A5 par exemple)")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		
		//control si un jour est bien sélectionné
		if (jourSelectionne==-1)
		{
			new AlertDialog.Builder(this).setTitle("Pas de jour sélectionné")
			.setMessage("Veuillez sélectionner le jour où a lieu ce cours.")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		
		//Création des dates avec le format donné
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");		
		Date dateFinDate = null;
		Date dateDebutDate = null;

		try {
			dateDebutDate = formatter.parse(this.dateDebutET.getText().toString());

			dateFinDate = formatter.parse(this.dateFinET.getText().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Erreur perso", "Erreur du cast de String en Date. " + e.getMessage());
		}

		//controle des dates
		if (dateDebutDate!= null && dateFinDate != null)
		{
			if (dateDebutDate.after(dateFinDate) )
			{
				new AlertDialog.Builder(this).setTitle("Dates incorrectes")
				.setMessage("Veuillez entrer un date de début supérieure à la date de fin")
				.setPositiveButton("OK", null)
				.show();
				return;
			}
		}
		else
		{

			return;
		}
		
		//controle des heures
		if (heureDebut.after(heureFin))
		{
			new AlertDialog.Builder(this).setTitle("Heures incorrectes")
			.setMessage("Veuillez entrer une heure de début supérieure à l'heure de fin")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		
		Cours c = new Cours();
		c.setMatiere(matiere);
		c.setType(lieu.getText().toString());
		c.setSalle(lieu.getText().toString());
		c.setJour(jourSelectionne);
		c.setDate_debut(dateDebutDate);
		c.setDate_fin(dateFinDate);
		c.setSemaine(0);
		c.setHeure_debut(heureDebut.getTime().getTime()); //les heures sont stockées en LONG.
		c.setHeure_fin(heureFin.getTime().getTime());
		
		if (coursDAO.insert(c) != 0 )
		{
			Toast.makeText(CoursAddActivity.this, "Cours enregistrée", Toast.LENGTH_SHORT).show();	
		}
		else
		{
			Toast.makeText(CoursAddActivity.this, "Problème lors de l'enregistrement", Toast.LENGTH_SHORT).show();	
		}

		daoSession.update(c);

		//Toast.makeText(PeriodeAddActivity.this, "pId:" + periode.getId(), Toast.LENGTH_SHORT).show();

		db.close();

		finish();
	}


}
