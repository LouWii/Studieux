package com.studieux.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MatiereAddActivity extends Activity {

	//La période à laquelle la matière sera liée
	private Periode periode;
	
	//La matière
	private Matiere matiere;
	
	private TextView matiereName;
	private TextView matiereCoef;
	
	private TextView periodeName;
	private TextView periodeDates;
	
	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
    private MatiereDao matiereDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matiere_add);
		
		//back arrow on action bar
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		
		matiereName = (TextView) findViewById(R.id.matiere_add_matiereNameEdit);
		matiereCoef = (TextView) findViewById(R.id.matiere_add_matiereCoefEdit);
		matiereCoef.setText("1.0");
		periodeName = (TextView) findViewById(R.id.matiere_add_periodeName);
		periodeDates = (TextView) findViewById(R.id.matiere_add_periodeDates);
		
		//Database stuff
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        periodeDao = daoSession.getPeriodeDao();
        matiereDao = daoSession.getMatiereDao();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_matiere_add, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		Bundle donnees = getIntent().getExtras();
		matiere = new Matiere();
		if (donnees != null && donnees.containsKey("periodeId"))
		{
			periode = periodeDao.load(donnees.getLong("periodeId"));
			periodeName.setText(periode.getNom());
			SimpleDateFormat dateFormater = new SimpleDateFormat("dd MM yyyy");
			periodeDates.setText("Du " + dateFormater.format(periode.getDate_debut()) + " au " + dateFormater.format(periode.getDate_fin()));
		}
		else
		{
			Toast.makeText(MatiereAddActivity.this, "Aucune période renseignée", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	/**
	 * Enregistrer la matière
	 * @param vue
	 */
	public void enregistrer(View vue)
	{
		//Si le nom est vide, on alerte et on stoppe l'enregistrement
		if (matiereName.getText().toString().equals("") )
		{
			new AlertDialog.Builder(this).setTitle("Nom vide")
    		.setMessage("Veuillez entrer un nom pour la matière")
    		.setPositiveButton("OK", null)
    		.show();
			return;
		}
		
		//verification coef
		Float coef;
		if (matiereCoef.getText().toString().contains("."))
		{
			try {
				coef = Float.parseFloat(matiereCoef.getText().toString());
			}
			catch (Exception e) {
				Toast.makeText(MatiereAddActivity.this, "Coefficient incorrect.", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		else
		{
			coef = (float) Integer.parseInt(matiereCoef.getText().toString());
		}
		
		
		//Enregistrement en BDD
		
		matiere.setNom(matiereName.getText().toString());
		matiere.setCoef(coef);
		matiere.setPeriode(periode);
		
		if (matiereDao.insert(matiere) != 0 )
		{
			Toast.makeText(MatiereAddActivity.this, "Matière enregistrée", Toast.LENGTH_SHORT).show();	
		}
		else
		{
			Toast.makeText(MatiereAddActivity.this, "Problème lors de l'enregistrement", Toast.LENGTH_SHORT).show();	
		}
		
		daoSession.update(matiere);
		
		//Toast.makeText(PeriodeAddActivity.this, "pId:" + periode.getId(), Toast.LENGTH_SHORT).show();
		
		db.close();
		
		finish();
		
	}
	
	
	 @Override
	 public void onBackPressed() {
		 // TODO Auto-generated method stub
		 super.onBackPressed();
		 overridePendingTransition(R.anim.animation_back_enter_up,
				 R.anim.animation_back_leave_up);
	 }

}
