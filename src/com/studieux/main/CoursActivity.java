package com.studieux.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slidingmenu.lib.SlidingMenu;
import com.studieux.bdd.Cours;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CoursActivity extends MenuActivity {

	private Matiere matiere;
	
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat heureFormatter;
	
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
		    dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		    heureFormatter = new SimpleDateFormat("HH:mm");
		}
		
		Bundle donnees = getIntent().getExtras();
		//Si une période est définie
		if (matiere == null && donnees != null && donnees.containsKey("matiereId") )
		{
			matiere = matiereDao.load(donnees.getLong("matiereId"));
			TextView nomMatiere = (TextView) findViewById(R.id.cours_matierenom);
			nomMatiere.setText("Cours de : "  + matiere.getNom());
			
			//updateList();
		}
		else if (matiere == null)//sinon, on cherche la période courante si pas de période
		{
			Toast.makeText(CoursActivity.this, "Pas de matière sélectionnée...", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		if (matiere != null)
		{
			updateList();
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
	
	public void updateList()
	{	
		if (matiere!=null)
		{
			matiere.resetCoursList();
			List<Cours> cList = matiere.getCoursList();
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			String[] from = {"heure_debut", "heure_fin", "date_debut", "date_fin", "type", "salle"};
			int[] to = { R.id.coursHeureDebut , R.id.coursHeureFin, R.id.coursDateDebut, R.id.coursDateFin, R.id.coursType, R.id.coursSalle };
			
			Toast.makeText(CoursActivity.this, "list:" + cList.size(), Toast.LENGTH_SHORT).show();
			
			for (Cours c : cList) {
				Map<String, String> datum = new HashMap<String, String>(3);
				datum.put("date_debut", dateFormatter.format(c.getDate_debut()));
				datum.put("date_fin", dateFormatter.format(c.getDate_fin()));
				datum.put("heure_debut", heureFormatter.format(c.getHeure_debut()));
				datum.put("heure_fin", heureFormatter.format(c.getHeure_fin()));
				datum.put("type", c.getType());
				datum.put("salle", c.getSalle());
				data.add(datum);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(this, 
					data,
					R.layout.cours_complete_list_item,
					from,
					to);

			//on récupère la liste on lui affecte l'adapter
			ListView listview = (ListView) findViewById(R.id.cours_listviewcours);

			listview.setAdapter(adapter);
		}
	}

}
