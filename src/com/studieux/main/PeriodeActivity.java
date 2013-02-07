package com.studieux.main;

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
import android.view.View;
import android.widget.ListView;

public class PeriodeActivity extends Activity {

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
		
		//Db init
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        periodeDao = daoSession.getPeriodeDao();
        
        //Recupération des periodes en BD
        String ddColumn = PeriodeDao.Properties.Date_debut.columnName;
        String orderBy = ddColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(periodeDao.getTablename(), periodeDao.getAllColumns(), null, null, null, null, orderBy);
        String[] from = { PeriodeDao.Properties.Nom.columnName, ddColumn, PeriodeDao.Properties.Date_fin.columnName };
        int[] to = { R.id.periodeListItemNomPeriode , R.id.periodeListItemDateDebut, R.id.periodeListItemDateFin };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.periode_list_item, cursor, from, to, 0);
        
        //on récupère la liste on lui affecte l'adapter
    	ListView listview = (ListView) findViewById(R.id.periodeListView);
    	listview.setAdapter(adapter);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_periode, menu);
		return true;
	}
	
	public void ajouter(View v)
	{
		Intent intention = new Intent(PeriodeActivity.this, PeriodeAddActivity.class);
		startActivity(intention);
	}

}
