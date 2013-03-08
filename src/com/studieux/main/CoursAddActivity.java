package com.studieux.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import de.greenrobot.dao.QueryBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
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
	
	private String [] noms;
	private HashMap<String, Integer> correctionJour;
	private int jourSelectionne = -1;
	
	
	SimpleDateFormat dateFormatter;
	SimpleDateFormat heureFormatter;
	
	EditText dateDebutET;
	EditText dateFinET;
	EditText heureDebutET;
	EditText heureFinET;
	TextView type;
	
	String [] typesArray = {"Amphi", "Cours", "TD", "TP"};
	
	Calendar heureDebut;
	Calendar heureFin;
	
	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
    private MatiereDao matiereDao;
    private CoursDao coursDao;
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
		    coursDao = daoSession.getCoursDao();
		}
		
		Bundle donnees = getIntent().getExtras();
		//Si une période est définie
		if (matiere == null && donnees != null && donnees.containsKey("MatiereId") )
		{
			matiere = matiereDao.load(donnees.getLong("MatiereId"));
			periode = matiere.getPeriode();
			dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
			dateDebutET = (EditText)findViewById(R.id.coursadd_coursdatedebut);
			dateFinET = (EditText)findViewById(R.id.coursadd_coursdatefin);
			dateDebutET.setText(dateFormatter.format(periode.getDate_debut()));
			dateFinET.setText(dateFormatter.format(periode.getDate_fin()));
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
			heureFormatter = new SimpleDateFormat("HH:mm");
			heureDebut = Calendar.getInstance();
			heureDebut.clear();
			heureDebut.set(1, 1, 1, 9, 0, 0);
			heureDebutET.setText(heureFormatter.format(heureDebut.getTime()));
			heureFin = Calendar.getInstance();
			heureFin.clear();
			heureFin.set(1, 1, 1, 10, 0, 0);
			heureFinET.setText(heureFormatter.format(heureFin.getTime()));
			//TextView nomMatiere = (TextView) findViewById(R.id.cours_matierenom);
			//nomMatiere.setText(matiere.getNom());
		}
		else if (matiere == null)//sinon, on cherche la période courante si pas de période
		{
			Toast.makeText(CoursAddActivity.this, "Pas de matière liée...", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		type = (EditText) findViewById(R.id.coursadd_courstype);
		type.setClickable(true);
		type.setFocusableInTouchMode(false);
		type.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CoursAddActivity.this.type.setInputType(InputType.TYPE_NULL);
				AlertDialog.Builder builder = new AlertDialog.Builder(CoursAddActivity.this);
				builder.setTitle("Quotient");
			
				builder.setItems(typesArray, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CoursAddActivity.this.type.setText(CoursAddActivity.this.typesArray[which]);
						
					}

				});
				builder.create();         
				builder.show();
			}
			
		});
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
			heureDebut.clear();
			heureDebut.set(1, 1, 1, hourOfDay, minute, 0);
			heureDebutET.setText(heureFormatter.format(heureDebut.getTime()));
			Toast.makeText(CoursAddActivity.this, "" + heureDebut.getTimeInMillis(), Toast.LENGTH_SHORT).show();
		}
	};
	
	/**
	 * Listener Date picker pour l'heure de fin
	 */
	private TimePickerDialog.OnTimeSetListener mHeureFinSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			heureFin = Calendar.getInstance();
			heureFin.clear();
			heureFin.set(1, 1, 1, hourOfDay, minute, 0);
			heureFinET.setText(heureFormatter.format(heureFin.getTime()));
			Toast.makeText(CoursAddActivity.this, "" + heureFin.getTimeInMillis(), Toast.LENGTH_SHORT).show();
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
			
			correctionJour = new HashMap<String, Integer>();
			correctionJour.put("Dimanche", 1);
			correctionJour.put("Lundi", 2);
			correctionJour.put("Mardi", 3);
			correctionJour.put("Mercredi", 4);
			correctionJour.put("Jeudi", 5);
			correctionJour.put("Vendredi", 6);
			correctionJour.put("Samedi", 7);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Matieres");
		
		builder.setItems(noms, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//NoteAddActivity.this.loadMatiere(Long.parseLong(NoteAddActivity.this.data.get(which).get("id")));
				String jr = noms[which];
				jourSelectionne = correctionJour.get(jr);
				TextView jour = (TextView)findViewById(R.id.coursadd_jour);
				jour.setText(jr);
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
		
		if (type.getText().toString().equals("") )
		{
			new AlertDialog.Builder(this).setTitle("Type de cours vide")
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
		Date dateFinDate = null;
		Date dateDebutDate = null;

		try {
			dateDebutDate = dateFormatter.parse(this.dateDebutET.getText().toString());

			dateFinDate = dateFormatter.parse(this.dateFinET.getText().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Erreur CAST", "Erreur du cast de String en Date. " + e.getMessage());
		}

		//controle des dates
		if (dateDebutDate!= null && dateFinDate != null)
		{
			if (dateDebutDate.after(dateFinDate) )
			{
				new AlertDialog.Builder(this).setTitle("Dates incorrectes")
				.setMessage("Veuillez entrer une date de début supérieure à la date de fin")
				.setPositiveButton("OK", null)
				.show();
				return;
			}
		}
		else
		{
			return;
		}
		
		//Vérification si les dates du cours
		if (dateDebutDate.before(this.periode.getDate_debut()))
		{
			new AlertDialog.Builder(this).setTitle("Date de début incorrecte")
			.setMessage("Veuillez entrer une date de début de cours comprise entre le " + dateFormatter.format(this.periode.getDate_debut()) + " et le " + dateFormatter.format(this.periode.getDate_fin()) + " (dates de la période dans laquelle se trouvera le cours)." )
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		if (dateFinDate.after(this.periode.getDate_fin()))
		{
			new AlertDialog.Builder(this).setTitle("Date de fin incorrecte")
			.setMessage("Veuillez entrer une date de fin de cours comprise entre le " + dateFormatter.format(this.periode.getDate_debut()) + " et le " + dateFormatter.format(this.periode.getDate_fin()) + " (dates de la période dans laquelle se trouvera le cours)." )
			.setPositiveButton("OK", null)
			.show();
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
		
		//vérification si le cours n'en chevauche pas un autre
		//obligation de passer par une requête
		QueryBuilder<Cours> qb = coursDao.queryBuilder();
		qb.where(
				com.studieux.bdd.CoursDao.Properties.Jour.eq(jourSelectionne), 
				com.studieux.bdd.CoursDao.Properties.Heure_debut.between(heureDebut.getTimeInMillis(), heureFin.getTimeInMillis())
		);
		qb.or(
				com.studieux.bdd.CoursDao.Properties.Jour.eq(jourSelectionne), 
				com.studieux.bdd.CoursDao.Properties.Heure_fin.between(heureDebut.getTimeInMillis(), heureFin.getTimeInMillis())
		);
		qb.or(
				com.studieux.bdd.CoursDao.Properties.Jour.eq(jourSelectionne),
				com.studieux.bdd.CoursDao.Properties.Heure_debut.eq(heureDebut.getTimeInMillis()),
				com.studieux.bdd.CoursDao.Properties.Heure_fin.eq(heureFin.getTimeInMillis())
		);
		List<Cours> lesCours = qb.list(); //cours chevauchant le nouveau cours
		
		Toast.makeText(CoursAddActivity.this, "list chevauch " + lesCours.size(), Toast.LENGTH_SHORT).show();
		
		System.out.println(dateDebutDate);
		//on va vérifier si les cours ne se chevauchent  pas car dates décalées
		for (Cours cours2 : lesCours) {
			System.out.println(cours2.getDate_debut());
			if ( 
					( dateDebutDate.after(cours2.getDate_debut()) && dateDebutDate.before(cours2.getDate_fin()) )
					|| ( dateFinDate.after(cours2.getDate_debut()) && dateFinDate.before(cours2.getDate_fin()) )
					|| ( dateDebutDate.equals(cours2.getDate_debut()) && dateFinDate.equals(cours2.getDate_fin()) )
			)
			{
				Date dtdebut = new Date(cours2.getHeure_debut());
				Date dtfin = new Date(cours2.getHeure_fin());
				
				//On récupère le nom du jour correct
				String jourErr = "";
				for (Map.Entry<String, Integer> jr : correctionJour.entrySet()) {
					if (jr.getValue() == cours2.getJour())
					{
						jourErr = jr.getKey();
					}
				}
				
				//On averti l'utilisateur
				new AlertDialog.Builder(this).setTitle("Cours chevauchant un autre")
				.setMessage(Html.fromHtml( "Le cous que vous désirez enregistrer en chevauche un autre :<br/>" 
						+ "<b>Matière :</b> " + cours2.getMatiere().getNom() 
						+ "<br/> "
						+ "<b>Cours</b> de type <i>"
						+ cours2.getType()
						+ "</i> ayant lieu le "
						+ jourErr
						+ "<br/> de <b>"
						+ heureFormatter.format(dtdebut)
						+ "</b> à <b>"
						+ heureFormatter.format(dtfin)
						+ "</b><br/> du "
						+ dateFormatter.format(cours2.getDate_debut())
						+ " au "
						+ dateFormatter.format(cours2.getDate_fin())
					)	
				)
				.setPositiveButton("OK", null)
				.show();
				return;
			}
		}
		
		Cours c = new Cours();
		c.setMatiere(matiere);
		c.setType(type.getText().toString());
		c.setSalle(lieu.getText().toString());
		c.setJour(jourSelectionne);
		c.setDate_debut(dateDebutDate);
		c.setDate_fin(dateFinDate);
		c.setSemaine(0);
		c.setHeure_debut(heureDebut.getTimeInMillis()); //les heures sont stockées en LONG.
		c.setHeure_fin(heureFin.getTimeInMillis());
		
		if (coursDao.insert(c) != 0 )
		{
			Toast.makeText(CoursAddActivity.this, "Cours enregistré", Toast.LENGTH_SHORT).show();	
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
