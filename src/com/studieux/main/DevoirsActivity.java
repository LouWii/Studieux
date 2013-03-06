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
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemLongClickListener;

public class DevoirsActivity extends MenuActivity {

	//DB Stuff
	private Cursor cursor;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private DevoirDao devoirDao;
    private MatiereDao matiereDao;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devoirs);
		
		initMenu();
		View v1 = findViewById(R.id.viewRed1);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 3;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_devoirs, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//update la liste lorsqu'on revient sur l'activité
		this.updateList();
	}
	
	/**
	 * Sélection d'une option dans le menu en haut ou dans la barre de boutons (en bas)
	 */
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
		Intent intention = new Intent(DevoirsActivity.this, DevoirsAddActivity.class);
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter_up,
		        R.anim.animation_leave_up);
	}
	
	public void updateList()
	{
		
		//Db init
		if (devoirDao == null)
		{
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
	        db = helper.getWritableDatabase();
	        daoMaster = new DaoMaster(db);
	        daoSession = daoMaster.newSession();
	        devoirDao = daoSession.getDevoirDao();
	        matiereDao = daoSession.getMatiereDao();
		}
        //Recupération des periodes en BD
        String ddColumn = DevoirDao.Properties.Deadline.columnName;
        String orderBy = ddColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(devoirDao.getTablename(), devoirDao.getAllColumns(), null, null, null, null, orderBy);
        //String[] from = { PeriodeDao.Properties.Nom.columnName, ddColumn, PeriodeDao.Properties.Date_fin.columnName };
        
        if (cursor.getCount() != 0) //si on a des résultats (sinon c'est inutile)
        {
	        String[] from = {"title", "date_rendu", "matiere"};
	        int[] to = { R.id.devoirListItemNomDevoir , R.id.devoirListItemDeadlineDevoir, R.id.devoirListItemMatiereDevoir };
	        
	        //On parse la liste pour convertir les long en Date, avant affichage
	        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	        Matiere m;
	        cursor.moveToFirst();
	        do     
	        {
	        	//Contient le détail d'une période
	        	Map<String, String> datum = new HashMap<String, String>(3);
	        	datum.put("id", "" + cursor.getLong(DevoirDao.Properties.Id.ordinal) );
	        	datum.put("title", cursor.getString(DevoirDao.Properties.Nom.ordinal));
	        	SimpleDateFormat dateFormater = new SimpleDateFormat("dd MM yyyy");
	        	Date d = new Date(cursor.getLong(DevoirDao.Properties.Deadline.ordinal));
	        	datum.put("date_rendu", "Date de rendu : " + dateFormater.format(d));
	        	m = matiereDao.load(cursor.getLong(DevoirDao.Properties.MatiereId.ordinal));
	        	datum.put("matiere", m.getNom());
	        	data.add(datum);
	        } while (cursor.moveToNext());
	        //Toast.makeText(PeriodeActivity.this, "t: " + data.size() + ";" + cursor.getCount(), Toast.LENGTH_SHORT).show();
	        
	        //Adapter pour notre listView
	        SimpleAdapter adapter = new SimpleAdapter(this, 
	        		data,
	        		R.layout.devoir_list_item,
	                from,
	                to);
	        
	        //on récupère la liste on lui affecte l'adapter
	    	ListView listview = (ListView) findViewById(R.id.devoirs_listview);
	    	
	    	listview.setAdapter(adapter);
	    	
	    	listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					HashMap<String, String> data = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
					final Devoir d = devoirDao.load(Long.parseLong(data.get("id")));
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
					String details = "<b>" + d.getNom() + "</b><br>"
							+ "en " + d.getMatiere().getNom() + "<br>"
							+ " pour le <b>" + dateFormatter.format(d.getDeadline()) + "</b><br/>";
					if (d.getDescription() != null)
					{
							details += "<b>Détails :</b><br>"
							+ d.getDescription();
					}
					
					new AlertDialog.Builder(DevoirsActivity.this).setTitle(d.getNom())
							.setMessage(Html.fromHtml(details))
							.setPositiveButton("OK", null)
							.setNegativeButton("Je l'ai fait, à supprimer de la liste !", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									devoirDao.delete(d);
									updateList();
								}
							})
							.show();
					
				}
			});
	    	
	    	listview.setOnItemLongClickListener( new OnItemLongClickListener () {
	
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// On récupère l'item clické = HashMap<String, String>
					HashMap<String, String> data = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
					
					Intent intention = new Intent(DevoirsActivity.this, DevoirsAddActivity.class);
					intention.putExtra( "id", Long.parseLong(data.get("id")) );
					startActivity(intention);
					
					//Toast.makeText(PeriodeActivity.this, "id: " + data.get("id"), Toast.LENGTH_SHORT).show();
					
					return false;
				}
	    		
	    	});
        }
	}

}
