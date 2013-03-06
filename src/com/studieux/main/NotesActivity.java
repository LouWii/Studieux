package com.studieux.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.Matiere;
import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.Note;
import com.studieux.bdd.NoteDao;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import android.os.Bundle;
import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Path.FillType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotesActivity extends MenuActivity {

	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao noteDao;
	private MatiereDao matiereDao;
	private Matiere matiere;

	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private XYSeries series;
	private Cursor cursor;
	
	
	private TextView moyenneTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);

		//Database stuff
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		noteDao = daoSession.getNoteDao();

		initMenu();
		View v1 = findViewById(R.id.viewRed4);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 4;

		GraphicalView chartView;
		series = new XYSeries("notes");
		renderer = getBarDemoRenderer();
		dataset = new XYMultipleSeriesDataset();
		fillGraphData();

		dataset.addSeries(series);

		chartView = ChartFactory.getBarChartView(this, dataset,renderer, Type.DEFAULT);
		setChartSettings(renderer);

		LinearLayout layout = (LinearLayout) findViewById(R.id.graphLayout);
		layout.addView(chartView);
		
		calculMoyenne();

	}

	public void fillGraphData()
	{
		noteDao = daoSession.getNoteDao();

		//Recupération des notes en BD
		String idColumn = MatiereDao.Properties.Id.columnName;
		String orderBy = idColumn + " COLLATE LOCALIZED ASC";
		cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);

		if(cursor.getCount() != 0)
		{
			//On parse la liste pour convertir les long en Date, avant affichage
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			cursor.moveToFirst();
			do
			{
				series.add(cursor.getPosition()+1, cursor.getInt(NoteDao.Properties.Value.ordinal));
				renderer.addXTextLabel(cursor.getPosition()+1, cursor.getString((NoteDao.Properties.Description.ordinal)));
			} while (cursor.moveToNext());         
		}
		renderer.setXAxisMax(cursor.getCount());
	}

	public void calculMoyenne()
	{
		float nbNotes = 0;
		float total = 0.0f;

		
		noteDao = daoSession.getNoteDao();
		matiereDao = daoSession.getMatiereDao(); 
		//Recupération des matieres en BD
		cursor = db.query(matiereDao.getTablename(), matiereDao.getAllColumns(), null, null, null, null, null);

		if(cursor.getCount() != 0)
		{
			
			//On parse la liste pour convertir les long en Date, avant affichage
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			cursor.moveToFirst();
			do
			{
				float nbNotesMatiere = 0;
				float totalMatiere = 0;
				matiere = matiereDao.load(cursor.getLong(MatiereDao.Properties.Id.ordinal));
				for (Note n : matiere.getNoteList())
				{
					nbNotesMatiere += n.getCoef();
					totalMatiere += n.getValue()*n.getCoef();
				}
				totalMatiere = totalMatiere / nbNotesMatiere;
				if(matiere.getNoteList().size()>0)
				{
					nbNotes+= matiere.getCoef();
					total+= totalMatiere*matiere.getCoef();
				}
				
			} while (cursor.moveToNext());         
		}
		total = total / (float)nbNotes;
		TextView tv = (TextView) findViewById(R.id.moyenneTv);
		if(total>0)
		{
			tv.setText(""+String.format("%.02f", total));
		}
		else
		{
			tv.setText("Pas de notes");
		}
	}

	public XYMultipleSeriesRenderer getBarDemoRenderer() 
	{
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(25.0f);
		renderer.setChartTitleTextSize(50.0f);
		renderer.setExternalZoomEnabled(false);
		renderer.setGridColor(getResources().getColor(android.R.color.holo_blue_light));
		renderer.setShowLegend(false);
		renderer.setXLabels(0);
		renderer.setPanEnabled(false, false);
		renderer.setLabelsTextSize(16.0f);
		renderer.setLegendTextSize(15);
		renderer.setBarSpacing(0.1);
		renderer.setZoomEnabled(false, false);
		renderer.setMarginsColor(color.white);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setMargins(new int[] {20, 30, 15, 0});
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(getResources().getColor(android.R.color.holo_blue_light));

		renderer.addSeriesRenderer(r);
		return renderer;
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer) 
	{
		renderer.setChartTitle("Mes notes");
		renderer.setXTitle("Devoirs");
		renderer.setYTitle("Points (/20)");
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(5);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(24);
	}

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notes, menu);
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

	public void goToNoteList (View v)
	{
		Intent intention = new Intent(NotesActivity.this, ListeNotesActivity.class);
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter_up,
				R.anim.animation_leave_up);
	}
	
	public void ajouter(View v)
	{
		Intent intention = new Intent(NotesActivity.this, NoteAddActivity.class);
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter_up,
				R.anim.animation_leave_up);

	}


}
