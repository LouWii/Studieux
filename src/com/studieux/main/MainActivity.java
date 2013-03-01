package com.studieux.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.studieux.bdd.Cours;
import com.studieux.bdd.CoursDao;
import com.studieux.bdd.CoursDao.Properties;
import com.studieux.bdd.DaoMaster;
import com.studieux.bdd.DaoSession;
import com.studieux.bdd.DevoirDao;
import com.studieux.bdd.Matiere;
import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.Note;
import com.studieux.bdd.NoteDao;
import com.studieux.bdd.Periode;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;

import de.greenrobot.dao.QueryBuilder;

import android.R.color;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.app.DialogFragment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
public class MainActivity extends MenuActivity {

	//DB stuff
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private MatiereDao matiereDao;
	private DevoirDao devoirDao;
	private NoteDao noteDao;
	private PeriodeDao periodeDao;
	private CoursDao coursDao;
	private Cursor cursor;

	private Matiere matiere;
	private Periode periode;

	//ListView des matieres
	private ListView matiereListView;
	private int currentRow;


	//graph stuff
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private XYSeries series;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMenu();

		View v1 = findViewById(R.id.viewRed0);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 0;

		//Database stuff
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		devoirDao = daoSession.getDevoirDao();
		matiereDao = daoSession.getMatiereDao();
		noteDao = daoSession.getNoteDao();
		periodeDao = daoSession.getPeriodeDao();
		coursDao = daoSession.getCoursDao();

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			GraphicalView chartView;
			series = new XYSeries("notes");
			dataset = new XYMultipleSeriesDataset();
			renderer = getBarDemoRenderer();
			fillGraphData();
			dataset.addSeries(series);
			chartView = ChartFactory.getBarChartView(this, dataset,renderer, Type.DEFAULT);
			setChartSettings(renderer);

			LinearLayout layout = (LinearLayout) findViewById(R.id.layoutGraph);
			layout.addView(chartView);
			calculMoyenne();
			updateMatiereList();

		}


	}





	public XYMultipleSeriesRenderer getBarDemoRenderer() 
	{
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setExternalZoomEnabled(false);
		renderer.setGridColor(getResources().getColor(android.R.color.holo_blue_light));
		renderer.setShowLegend(false);
		renderer.setXLabels(0);
		renderer.setPanEnabled(false, false);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
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

	public void fillGraphData()
	{
		noteDao = daoSession.getNoteDao();

		//Recupération des periodes en BD
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
		TextView tv = (TextView) findViewById(R.id.moyenneET);
		if(total>0)
		{
			tv.setText(""+String.format("%.02f", total));
		}
		else
		{
			tv.setText("Pas de notes");
		}
	}

	public void updateMatiereList()
	{

		String[] from = {"heure_debut", "heure_fin", "matiereNom", "type", "salle"};
		int[] to = { R.id.coursHeureDebut , R.id.coursHeureFin, R.id.coursNomMatiere, R.id.coursType, R.id.coursSalle };

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		Date d = new Date();
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);

		QueryBuilder<Cours> qb = coursDao.queryBuilder();
		//on récupère les périodes courante (date courant > date_debut et date courante < date_fin)
		//normalement une seule période doit arriver (on n'autorise pas le chevauchement de périodes)
		qb.where(CoursDao.Properties.Date_debut.le(d), CoursDao.Properties.Date_fin.ge(d), CoursDao.Properties.Jour.eq(calendar.get(Calendar.DAY_OF_WEEK)));
		qb.orderAsc(CoursDao.Properties.Heure_debut);
		List<Cours> listeCours = qb.list();

		for(Cours c : listeCours)
		{
			//Contient le détail d'une période
			Map<String, String> datum = new HashMap<String, String>(3);
			datum.put("id", "" + c.getId());
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			datum.put("heure_debut",formatter.format(c.getHeure_debut()));
			datum.put("heure_fin",formatter.format(c.getHeure_fin()));
			Matiere m = matiereDao.load(c.getMatiereId());
			datum.put("matiereNom", m.getNom());
			datum.put("type", c.getType());
			datum.put("salle", "Lieu : " + c.getSalle());
			data.add(datum);
		}


		//Adapter pour notre listView
		SimpleAdapter adapter = new SimpleAdapter(this, 
				data,
				R.layout.cours_list_item,
				from,
				to);

		//on récupère la liste on lui affecte l'adapter
		matiereListView = (ListView) findViewById(R.id.coursListView);

		matiereListView.setAdapter(adapter);


		matiereListView.post(new Runnable() {
			public void run() {
				MainActivity.this.updateView(0);
			}
		});
	}

	private void updateView(int index){

		System.out.println(index - matiereListView.getFirstVisiblePosition());
		if(index >= matiereListView.getFirstVisiblePosition() && index <= matiereListView.getLastVisiblePosition())
		{
			System.out.println("on peut modifier");
			View v = matiereListView.getChildAt(index - matiereListView.getFirstVisiblePosition());
			LinearLayout background = (LinearLayout) v.findViewById(R.id.backGroundLayout);
			background.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		}
		else
		{
			System.out.println(index + " | " + matiereListView.getFirstVisiblePosition() + " | " + matiereListView.getLastVisiblePosition());
			System.out.println("on peut pas modifier");
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	public void ajouterPeriode(View v)
	{
		Intent intention = new Intent(MainActivity.this, MatiereActivity.class);
		startActivity(intention);

		//DialogFragment newFragment = new PeriodeSellectionDialogFragment();
		//newFragment.show(getFragmentManager(), "ih");
		//String tag = "dez";
		//newFragment.show(getFragmentManager(), tag);
	}

	public void goToNotes(View v)
	{
		Intent intention = new Intent(MainActivity.this, NotesActivity.class);
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter,
				R.anim.animation_leave);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override   
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

}
