package com.studieux.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoMaster.DevOpenHelper;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.Periode;
import com.studieux.bdd.PeriodeDao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class PeriodeAddActivity extends Activity {

	private EditText nom, dateDebut, dateFin;
	
	static final int DATEDEBUT_DIALOG_ID = 0;
	static final int DATEFIN_DIALOG_ID = 1;
	
	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);
        switch (id) {
        case DATEDEBUT_DIALOG_ID:
            return new DatePickerDialog(this,  mDateDebutSetListener,  cyear, cmonth, cday);
        case DATEFIN_DIALOG_ID:
        	return new DatePickerDialog(this,  mDateFinSetListener,  cyear, cmonth, cday);
    	}
        return null;
    }
    
    /**
     * Date picker pour la date de début
     */
    private DatePickerDialog.OnDateSetListener mDateDebutSetListener = new DatePickerDialog.OnDateSetListener() {
        // onDateSet method
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        	//On récupère les valeurs de la date et on les met dans le champ texte
        	String date_selected = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1) + "/"+String.valueOf(year);
            dateDebut.setText(date_selected);
        }
    };
    
    /**
     * Date picker pour la date de fin
     */
    private DatePickerDialog.OnDateSetListener mDateFinSetListener = new DatePickerDialog.OnDateSetListener() {
        // onDateSet method
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date_selected = String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
            dateFin.setText(date_selected);
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_periode_add);
		
		//Database stuff
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db", null);
		db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        periodeDao = daoSession.getPeriodeDao();
		
		nom = (EditText) findViewById(R.id.periodeNameEdit);
		dateDebut = (EditText) findViewById(R.id.dateDebutEdit);
		dateFin = (EditText) findViewById(R.id.dateFinEdit);
		
		dateDebut.setOnTouchListener(new OnTouchListener(){ 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v == dateDebut)
				{
                    showDialog(DATEDEBUT_DIALOG_ID);
				}
                return false;  
			}
        });
		dateFin.setOnTouchListener(new OnTouchListener(){ 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v == dateFin)
				{
                    showDialog(DATEFIN_DIALOG_ID);
				}
                return false;  
			}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_periode_add, menu);
		return true;
	}
	
	/**
	 * Enregistrer la période
	 * @param vue
	 */
	public void enregistrer(View vue)
	{
		//Si le nom est vide, on alerte et on stoppe l'enregistrement
		if (nom.getText().toString().equals("") )
		{
			new AlertDialog.Builder(this).setTitle("Nom vide")
    		.setMessage("Veuillez entrer un nom pour la période")
    		.setPositiveButton("OK", null)
    		.show();
			return;
		}
		//Création des dates avec le format donné
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");		
		Date dateFinDate = null;
		Date dateDebutDate = null;
		
		try {
			dateDebutDate = formatter.parse(this.dateDebut.getText().toString());
			
			dateFinDate = formatter.parse(this.dateFin.getText().toString());
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
		
		//Enregistrement en BDD
		Periode p = new Periode(null, nom.getText().toString(), dateDebutDate, dateFinDate);
		
		if (periodeDao.insert(p) != 0 )
		{
			Toast.makeText(PeriodeAddActivity.this, "Periode enregistrée", Toast.LENGTH_SHORT).show();	
		}
		finish();
		
	}

}
