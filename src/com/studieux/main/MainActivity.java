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
import com.studieux.bdd.Devoir;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
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

	//ListView des cours
	private ListView coursListView;
	private int currentRow;
	private ListUpdater coursUpdater;
	private List<Map<String, String>> data;
	SimpleAdapter adapter;
	private int oldCoursCount = 0;
	private List<Cours> listeCours;

	//LisstView des devoirs
	private ListView devoirsLisView;



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
		initDatabase();

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			initNotesGraph();
			initDateTV();
			initCoursList();
		}		
	}


	public void initDateTV()
	{
		TextView dateTV = (TextView) findViewById(R.id.dateTV);
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		java.text.DateFormat fmt = DateFormat.getLongDateFormat(this);	   
		dateTV.setText(fmt.format(c.getTime()));
	}

	public void initDatabase()
	{
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		devoirDao = daoSession.getDevoirDao();
		matiereDao = daoSession.getMatiereDao();
		noteDao = daoSession.getNoteDao();
		periodeDao = daoSession.getPeriodeDao();
		coursDao = daoSession.getCoursDao();
	}


	//--------------------------Methodes pour la liste des notes---------------------------------------------------------------------------

	public void initNotesGraph()
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
		renderer.setXAxisMax(cursor.getCount()+1);
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




	//--------------------------Methodes pour la iste des cours-------------------------------------------------------------------------------


	public void initCoursList()
	{
		String[] from = {"heure_debut", "heure_fin", "matiereNom", "type", "salle"};
		int[] to = { R.id.coursHeureDebut , R.id.coursHeureFin, R.id.coursNomMatiere, R.id.coursType, R.id.coursSalle };
		data = new ArrayList<Map<String, String>>();
		//Adapter pour notre listView
		adapter = new SimpleAdapter(this, 
				data,
				R.layout.cours_list_item,
				from,
				to);
		//on récupère la liste on lui affecte l'adapter
		coursListView = (ListView) findViewById(R.id.coursListView);

		coursListView.setAdapter(adapter);
	}

	public void updateCoursList()
	{
		runOnUiThread(new Runnable() {
			public void run() {
				String[] from = {"heure_debut", "heure_fin", "matiereNom", "type", "salle"};
				int[] to = { R.id.coursHeureDebut , R.id.coursHeureFin, R.id.coursNomMatiere, R.id.coursType, R.id.coursSalle };

				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				{



					Date d = new Date();
					Calendar calendar = Calendar.getInstance();
					int day = calendar.get(Calendar.DAY_OF_WEEK);
					Calendar heureCalendar = Calendar.getInstance();
					heureCalendar.set(1, 1, 1);

					QueryBuilder<Cours> qb = coursDao.queryBuilder();
					//on récupère les périodes courante (date courant > date_debut et date courante < date_fin)
					//normalement une seule période doit arriver (on n'autorise pas le chevauchement de périodes)
					qb.where(CoursDao.Properties.Date_debut.le(d), CoursDao.Properties.Date_fin.ge(d), CoursDao.Properties.Jour.eq(calendar.get(Calendar.DAY_OF_WEEK)), CoursDao.Properties.Heure_fin.gt(heureCalendar.getTime().getTime()));
					qb.orderAsc(CoursDao.Properties.Heure_debut);
					MainActivity.this.listeCours = qb.list();

					if(listeCours.size() != 0)
					{
						if(listeCours.size() != oldCoursCount)
						{
							MainActivity.this.data.clear();

							oldCoursCount = listeCours.size();
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
								MainActivity.this.data.add(datum);
							}


							System.out.println("courslist.size : " + listeCours.size());
							System.out.println("datalist.size : " + data.size());
							MainActivity.this.adapter = new SimpleAdapter(MainActivity.this, 
									MainActivity.this.data,
									R.layout.cours_list_item,
									from,
									to);



							//MainActivity.this.adapter.notifyDataSetChanged();
							//MainActivity.this.coursListView.invalidate();
							MainActivity.this.coursListView.setAdapter(adapter);

						}
						coursListView.post(new Runnable() {
							public void run() {
								MainActivity.this.repaintCurrentMatiere();
							}
						});
					}

				}
			}
		});
	}

	/**
	 * Change la couleur de la cellule d'index 0 si c'est le cours actuel
	 */
	private void repaintCurrentMatiere(){
		//Index de la cellule à modifier
		int index = 0 ;

		Calendar heureCalendar = Calendar.getInstance();
		heureCalendar.set(1, 1, 1);
		if(heureCalendar.getTime().getTime() >= listeCours.get(0).getHeure_debut())
		{

			if(index >= coursListView.getFirstVisiblePosition() && index <= coursListView.getLastVisiblePosition())
			{
				View v = coursListView.getChildAt(index - coursListView.getFirstVisiblePosition());
				LinearLayout background = (LinearLayout) v.findViewById(R.id.backGroundLayout);
				background.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
				TextView heureDebut = (TextView) v.findViewById(R.id.coursHeureDebut);
				heureDebut.setTextColor(Color.WHITE);
				TextView heureFin = (TextView) v.findViewById(R.id.coursHeureFin);
				heureFin.setTextColor(Color.WHITE);
				TextView nomMatiere = (TextView) v.findViewById(R.id.coursNomMatiere);
				nomMatiere.setTextColor(Color.WHITE);
				TextView salle = (TextView) v.findViewById(R.id.coursSalle);
				salle.setTextColor(Color.WHITE);
				TextView type = (TextView) v.findViewById(R.id.coursType);
				type.setTextColor(Color.WHITE);
				LinearLayout barre = (LinearLayout) v.findViewById(R.id.coursBarre);
				barre.setBackgroundResource(R.color.white);

				ImageView img = (ImageView) v.findViewById(R.id.coursIcon);
				img.setImageResource(R.drawable.blackboard_white);

				//mise à jour de la barre indiquant l'avancée du cours
				RelativeLayout timeBar = (RelativeLayout) v.findViewById(R.id.coursLigneTempsRestant);
				long tempsDuCours = listeCours.get(0).getHeure_fin() - listeCours.get(0).getHeure_debut();
				System.out.println("temps du cours " + tempsDuCours);

				long tempspasse = heureCalendar.getTime().getTime() - listeCours.get(0).getHeure_debut();
				System.out.println("temps passé " + tempspasse);
				System.out.println(Math.round(v.getWidth()*((float)tempspasse/(float)tempsDuCours)) + "  ::: " + v.getWidth() + " ::::" +(tempspasse/tempsDuCours) );
				timeBar.setLayoutParams(new android.widget.LinearLayout.LayoutParams(Math.round(v.getWidth()*((float)tempspasse/(float)tempsDuCours)),2));      
				timeBar.setBackgroundResource(R.color.white);
			}
		}
	}


	//--------------------------Methodes pour la iste des devoirs-------------------------------------------------------------------------------


	public void updateDevoirsList()
	{
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
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
				List<Map<String, String>> devoirsData = new ArrayList<Map<String, String>>();
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
					devoirsData.add(datum);
				} while (cursor.moveToNext());
				//Toast.makeText(PeriodeActivity.this, "t: " + data.size() + ";" + cursor.getCount(), Toast.LENGTH_SHORT).show();

				//Adapter pour notre listView
				SimpleAdapter devoirsAdapter = new SimpleAdapter(this, 
						devoirsData,
						R.layout.devoir_main_list_item,
						from,
						to);

				//on récupère la liste on lui affecte l'adapter
				ListView listview = (ListView) findViewById(R.id.devoirsListView);

				listview.setAdapter(devoirsAdapter);

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

						new AlertDialog.Builder(MainActivity.this).setTitle(d.getNom())
						.setMessage(Html.fromHtml(details))
						.setPositiveButton("OK", null)
						.setNegativeButton("Je l'ai fait, à supprimer de la liste !", new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								devoirDao.delete(d);
								updateDevoirsList();
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

						Intent intention = new Intent(MainActivity.this, DevoirsAddActivity.class);
						intention.putExtra( "id", Long.parseLong(data.get("id")) );
						startActivity(intention);

						//Toast.makeText(PeriodeActivity.this, "id: " + data.get("id"), Toast.LENGTH_SHORT).show();

						return false;
					}

				});
			}
		}
	}

	public void goToNotes(View v)
	{
		Intent intention = new Intent(MainActivity.this, NotesActivity.class);
		startActivity(intention);
		this.overridePendingTransition(R.anim.animation_enter,
				R.anim.animation_leave);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		coursUpdater = new ListUpdater();
		coursUpdater.execute();
		this.updateDevoirsList();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		coursUpdater.cancel(true);
	}



	private class ListUpdater extends AsyncTask<Void, Integer, Void>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Toast.makeText(getApplicationContext(), "Début du traitement asynchrone", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
			// Mise à jour de la ProgressBar
			MainActivity.this.updateCoursList();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			while(!isCancelled())
			{
				onProgressUpdate(1);
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
		}
	}
}
