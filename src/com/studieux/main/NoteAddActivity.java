package com.studieux.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.Devoir;
import com.studieux.bdd.DevoirDao;
import com.studieux.bdd.Matiere;
import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.Note;
import com.studieux.bdd.NoteDao;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;
import com.studieux.main.MatiereActivity.MyDialogListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NoteAddActivity extends Activity {

	private Matiere matiere;
	private Devoir devoir;
	private Note note;

	private TextView matiereName;
	private EditText noteValeur;
	private EditText noteCoeff;
	private EditText noteQuotient;
	private EditText noteDescription;
	
	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private MatiereDao matiereDao;
	private DevoirDao devoirDao;
	private NoteDao noteDao;

	private Cursor cursor;

	public List<Map<String, String>> data;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_add);

		//back arrow on action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);


		this.matiereName = (TextView) findViewById(R.id.matiereNom);
		this.noteValeur = (EditText) findViewById(R.id.noteET);
		this.noteCoeff = (EditText) findViewById(R.id.coeffET);
		this.noteCoeff.setText("1");
		this.noteDescription = (EditText) findViewById(R.id.descriptionET);
		this.noteQuotient = (EditText) findViewById(R.id.quotientET);
		this.noteQuotient.setText("20");
		this.noteQuotient.setClickable(true);
		this.noteQuotient.setFocusableInTouchMode(false);
		noteQuotient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String [] quotients = {"10", "20"};
				AlertDialog.Builder builder = new AlertDialog.Builder(NoteAddActivity.this);
				builder.setTitle("Quotient");
				builder.setItems(quotients, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						NoteAddActivity.this.noteQuotient.setText(10+(which*10)+"");

					}

				});
				builder.create();         
				builder.show();
			}
		});



		//Database stuff
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		devoirDao = daoSession.getDevoirDao();
		matiereDao = daoSession.getMatiereDao();
		noteDao = daoSession.getNoteDao();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_add, menu);
		return true;
	}

	public void chooseMatiere(View v)
	{
		matiereDao = daoSession.getMatiereDao();

		//Recupération des periodes en BD
		String nomColumn = MatiereDao.Properties.Nom.columnName;
		String orderBy = nomColumn + " COLLATE LOCALIZED ASC";
		cursor = db.query(matiereDao.getTablename(), matiereDao.getAllColumns(), null, null, null, null, orderBy);

		if(cursor.getCount() != 0)
		{
			//On parse la liste pour convertir les long en Date, avant affichage
			data = new ArrayList<Map<String, String>>();
			String [] noms = new String [cursor.getCount()];
			cursor.moveToFirst();
			do
			{
				System.out.println("ehheheh");
				noms[cursor.getPosition()] =  cursor.getString(PeriodeDao.Properties.Nom.ordinal);
				//Contient le détail d'une période
				Map<String, String> datum = new HashMap<String, String>(3);
				datum.put("id", "" + cursor.getLong(PeriodeDao.Properties.Id.ordinal) );
				datum.put("nom", cursor.getString(PeriodeDao.Properties.Nom.ordinal));
				datum.put("coef", "Coef. : " + cursor.getString(MatiereDao.Properties.Coef.ordinal));
				data.add(datum);
			} while (cursor.moveToNext());            


			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Matieres");
			builder.setItems(noms, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					NoteAddActivity.this.loadMatiere(Long.parseLong(NoteAddActivity.this.data.get(which).get("id")));

				}

			});
			builder.create();         
			builder.show();
		}
	}

	public void chooseDevoirs(View v)
	{

	}

	public void loadMatiere(long id)
	{
		this.matiere = this.matiereDao.load(id);
		this.matiereName.setText(this.matiere.getNom());
	}

	public void enregistrer(View v)
	{
		int note;
		int coeff;
		try  
		{  
			note = Integer.parseInt(this.noteValeur.getText().toString());  
			if(note > Integer.parseInt(this.noteQuotient.getText().toString()) || note < 0)
			{
				Toast.makeText(NoteAddActivity.this, "Veuillez saisir une note comprise entre 0 et " + this.noteQuotient.getText(), Toast.LENGTH_SHORT).show();
				return;
			}
		}  
		catch( Exception e)  
		{  
			Toast.makeText(NoteAddActivity.this, "Veuillez saisir une note comprise entre 0 et " + this.noteQuotient.getText(), Toast.LENGTH_SHORT).show();
			return;
		}
		
		try  
		{  
			coeff = Integer.parseInt(this.noteCoeff.getText().toString());  
			if(coeff <  0)
			{
				Toast.makeText(NoteAddActivity.this, "Veuillez saisir un coefficient positif " + this.noteQuotient.getText(), Toast.LENGTH_SHORT).show();
				return;
			}
		}  
		catch( Exception e)  
		{  
			Toast.makeText(NoteAddActivity.this, "Veuillez saisir une valeur numérique comme coefficient " + this.noteQuotient.getText(), Toast.LENGTH_SHORT).show();
			return;
		}
		if(this.matiere == null)
		{
			Toast.makeText(NoteAddActivity.this, "Veuillez sélectionner une matière", Toast.LENGTH_SHORT).show();
			return;
		}
		if(this.noteDescription.getText().toString().equals(""))
		{
			Toast.makeText(NoteAddActivity.this, "Veuillez entrer une description", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Note newNote = new Note();
		if(Integer.parseInt(this.noteQuotient.getText().toString()) == 10)
		{
			newNote.setValue(note*2);
		}
		else
		{
			newNote.setValue(note);
		}
		
		newNote.setCoef((float)coeff);
		newNote.setQuotient(Integer.parseInt(this.noteQuotient.getText().toString()));
		newNote.setDescription(this.noteDescription.getText().toString());
		newNote.setMatiereId(this.matiere.getId());
		
		if (noteDao.insert(newNote) != 0 )
		{
			Toast.makeText(NoteAddActivity.this, "Note enregistrée", Toast.LENGTH_SHORT).show();	
		}
		else
		{
			Toast.makeText(NoteAddActivity.this, "Problème lors de l'enregistrement", Toast.LENGTH_SHORT).show();	
		}
		
		daoSession.update(newNote);
		
		db.close();
		
		finish();
		
	}

}
