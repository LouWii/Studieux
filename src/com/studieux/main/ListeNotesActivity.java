package com.studieux.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.Matiere;
import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.Note;
import com.studieux.bdd.NoteDao;
import com.studieux.bdd.Periode;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import de.greenrobot.dao.QueryBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ListeNotesActivity extends Activity {

	//DB Stuff
	private Cursor cursor;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao notesDao;
	private MatiereDao matiereDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_liste_notes);
		
		//back arrow on action bar
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.liste_notes, menu);
		return true;
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		//Db init
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		notesDao = daoSession.getNoteDao();
		matiereDao = daoSession.getMatiereDao();
		
		this.updateList();
	}

	public void updateList()
	{
		cursor = db.query(notesDao.getTablename(), notesDao.getAllColumns(), null, null, null, null, null);
		if(cursor.getCount() != 0)
		{
			//Recupération des periodes en BD
			String[] from = {"value", "description","coeff"};
			int[] to = { R.id.noteListItemValeur , R.id.noteListItemDescription, R.id.noteListItemCoeff };
			//SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.periode_list_item, cursor, from, to, 0);
			
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();

			//On parse la liste pour convertir les long en Date, avant affichage

			cursor.moveToFirst();
			do
			{

				Map<String, String> datum = new HashMap<String, String>(3);
				datum.put("id", "" + cursor.getLong(NoteDao.Properties.Id.ordinal) );
				datum.put("value", cursor.getString(NoteDao.Properties.Value.ordinal));
				datum.put("description", cursor.getString(NoteDao.Properties.Description.ordinal));
				datum.put("coeff", cursor.getString(NoteDao.Properties.Coef.ordinal));
				data.add(datum);
			} while (cursor.moveToNext());         

			//On parse la liste pour convertir les long en Date, avant affichage

			//Adapter pour notre listView
			SimpleAdapter adapter = new SimpleAdapter(this, 
					data,
					R.layout.note_list_item,
					from,
					to);

			//on récupère la liste on lui affecte l'adapter
			ListView listview = (ListView) findViewById(R.id.noteListView);

			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					// On récupère l'item clické = HashMap<String, String>
					HashMap<String, String> data = (HashMap<String, String>) arg0.getItemAtPosition(arg2);

					Intent intention = new Intent(ListeNotesActivity.this, NoteAddActivity.class);
					intention.putExtra( "noteId", Long.parseLong(data.get("id")) );
					startActivity(intention);
					ListeNotesActivity.this.overridePendingTransition(R.anim.animation_enter_up,
							R.anim.animation_leave_up);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			this.onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	 public void onBackPressed() {
		 // TODO Auto-generated method stub
		 super.onBackPressed();
		 overridePendingTransition(R.anim.animation_back_enter_up,
				 R.anim.animation_back_leave_up);
	 }
	
}
