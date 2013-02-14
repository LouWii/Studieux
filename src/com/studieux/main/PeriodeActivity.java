package com.studieux.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoMaster.DevOpenHelper;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.PeriodeDao;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PeriodeActivity extends MenuActivity {

	//DB Stuff
	private Cursor cursor;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_periode);
		initMenu();
		View v1 = findViewById(R.id.viewRed1);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 1;
		this.updateList();
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//update la liste lorsqu'on revient sur l'activit�
		this.updateList();
	}
	
	public void updateList()
	{
		//Db init
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        periodeDao = daoSession.getPeriodeDao();
        
        //Recup�ration des periodes en BD
        String ddColumn = PeriodeDao.Properties.Date_debut.columnName;
        String orderBy = ddColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(periodeDao.getTablename(), periodeDao.getAllColumns(), null, null, null, null, orderBy);
        //String[] from = { PeriodeDao.Properties.Nom.columnName, ddColumn, PeriodeDao.Properties.Date_fin.columnName };
        String[] from = {"title", "date_debut", "date_fin"};
        int[] to = { R.id.periodeListItemNomPeriode , R.id.periodeListItemDateDebut, R.id.periodeListItemDateFin };
        //SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.periode_list_item, cursor, from, to, 0);
        
        //On parse la liste pour convertir les long en Date, avant affichage
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        cursor.moveToFirst();
        while (!cursor.isBeforeFirst() && !cursor.isLast())
        {
        	//Contient le d�tail d'une p�riode
        	Map<String, String> datum = new HashMap<String, String>(3);
        	datum.put("title", cursor.getString(PeriodeDao.Properties.Nom.ordinal));
        	SimpleDateFormat dateFormater = new SimpleDateFormat("dd MM yyyy");
        	Date d = new Date(cursor.getLong(PeriodeDao.Properties.Date_debut.ordinal));
        	datum.put("date_debut", dateFormater.format(d));
        	d = new Date(cursor.getLong(PeriodeDao.Properties.Date_fin.ordinal));
        	datum.put("date_fin", dateFormater.format(d));
        	
        	data.add(datum);
        	cursor.moveToNext();
        }
        
        //Adapter pour notre listView
        SimpleAdapter adapter = new SimpleAdapter(this, 
        		data,
        		R.layout.periode_list_item,
                from,
                to);
        
        //on r�cup�re la liste on lui affecte l'adapter
    	ListView listview = (ListView) findViewById(R.id.periodeListView);
    	
    	listview.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_periode, menu);
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
		Intent intention = new Intent(PeriodeActivity.this, PeriodeAddActivity.class);
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter_up,
		        R.anim.animation_leave_up);
		
	}

}
