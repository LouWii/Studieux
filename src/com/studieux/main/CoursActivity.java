package com.studieux.main;

import com.slidingmenu.lib.SlidingMenu;
import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.Matiere;
import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CoursActivity extends MenuActivity {

	private Matiere matiere;
	
	//DB Stuff
	private Cursor cursor;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MatiereDao matiereDao;
	private Long currentMatiereId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cours);
		initMenu();	
		currentButtonIndex = 1;
		//findViewById(R.id.viewRed2).setBackgroundColor(getResources().getColor(R.color.redButtons));
		//menuButtons[1].drawRedLine();
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
		}
		
		Bundle donnees = getIntent().getExtras();
		//Si une période est définie
		if (matiere == null && donnees != null && donnees.containsKey("matiereId") )
		{
			matiere = matiereDao.load(donnees.getLong("matiereId"));
			TextView nomMatiere = (TextView) findViewById(R.id.cours_matierenom);
			nomMatiere.setText(matiere.getNom());
		}
		else if (matiere == null)//sinon, on cherche la période courante si pas de période
		{
			Toast.makeText(CoursActivity.this, "Pas de matière sélectionnée...", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cours, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed
	            // in the Action Bar.
	            finish();
	            return true;
	        case R.id.menu_add:
	        	this.ajouter(findViewById(R.id.menu_add));
	        	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void ajouter(View v)
	{
		Intent intention = new Intent(CoursActivity.this, CoursAddActivity.class);
		intention.putExtra("MatiereId", matiere.getId());
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter_up,
		        R.anim.animation_leave_up);
		
	}

}
