package com.studieux.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MatiereActivity extends Activity {

	AlertDialog.Builder builder;
	AlertDialog alertDialog;
	
	//DB Stuff
	private Cursor cursor;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matiere);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_matiere, menu);
		return true;
	}
	
	public void selectionnerPeriode(View v)
	{
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
        //String[] from = { PeriodeDao.Properties.Nom.columnName, ddColumn, PeriodeDao.Properties.Date_fin.columnName };
        String[] from = {"title", "date_debut", "date_fin"};
        int[] to = { R.id.periodeListItemNomPeriode , R.id.periodeListItemDateDebut, R.id.periodeListItemDateFin };
        //SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.periode_list_item, cursor, from, to, 0);
        
        //On parse la liste pour convertir les long en Date, avant affichage
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        cursor.moveToFirst();
        while (!cursor.isBeforeFirst() && !cursor.isLast())
        {
        	//Contient le détail d'une période
        	Map<String, String> datum = new HashMap<String, String>(3);
        	datum.put("id", "" + cursor.getLong(PeriodeDao.Properties.Id.ordinal) );
        	datum.put("title", cursor.getString(PeriodeDao.Properties.Nom.ordinal));
        	SimpleDateFormat dateFormater = new SimpleDateFormat("dd MM yyyy");
        	Date d = new Date(cursor.getLong(PeriodeDao.Properties.Date_debut.ordinal));
        	datum.put("date_debut", "Date de début : " + dateFormater.format(d));
        	d = new Date(cursor.getLong(PeriodeDao.Properties.Date_fin.ordinal));
        	datum.put("date_fin", "Date de fin : " + dateFormater.format(d));
        	
        	data.add(datum);
        	cursor.moveToNext();
        }
        
        //Adapter pour notre listView
        SimpleAdapter adapter = new SimpleAdapter(this, 
        		data,
        		R.layout.periode_list_item,
                from,
                to);
        
		ListFragment newFragment = new PeriodeSellectionDialogFragment(adapter);
		String tag = "dez";
		Toast.makeText(MatiereActivity.this, "start frag", Toast.LENGTH_SHORT).show();
		getFragmentManager().beginTransaction().replace(R.id.matiereMainLayoutContainer , newFragment, tag).commit();
		
	}

}
