package com.studieux.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.studieux.bdd.Periode;
import com.studieux.bdd.PeriodeDao;
import com.studieux.bdd.DaoMaster.DevOpenHelper;
import com.studieux.main.MatiereActivity.MyDialogListener;

import de.greenrobot.dao.QueryBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DevoirsAddActivity extends Activity {

	
	private Periode periode;
	private Matiere matiere;
	private Devoir devoir;
	
	private TextView periodeLabel;
	private TextView matiereLabel;
	
	private EditText dateDeadlineET;
	private EditText nomDevoirET;
	private EditText descDevoirET;
	
	static final int DATEDEADLINE_DIALOG_ID = 0;
	
	//DB Stuff
	private Cursor cursor;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PeriodeDao periodeDao;
    private MatiereDao matiereDao;
    private DevoirDao devoirDao;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devoirs_add);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_devoirs_add, menu);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);
		switch (id) {
			case DATEDEADLINE_DIALOG_ID:
				return new DatePickerDialog(this,  mDateDeadlineSetListener,  cyear, cmonth, cday);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener mDateDeadlineSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			//On récupère les valeurs de la date et on les met dans le champ texte
			String date_selected = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1) + "/"+String.valueOf(year);
			dateDeadlineET.setText(date_selected);
		}
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (periodeDao == null)//Db init
		{
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "studieux-db.db", null);
		    db = helper.getWritableDatabase();
		    daoMaster = new DaoMaster(db);
		    daoSession = daoMaster.newSession();
		    periodeDao = daoSession.getPeriodeDao();
		    matiereDao = daoSession.getMatiereDao();
		    devoirDao = daoSession.getDevoirDao();
		    devoir = new Devoir();
		    periodeLabel = (TextView) findViewById(R.id.devoirAddPeriodeLabel);
		    matiereLabel = (TextView) findViewById(R.id.devoirsAddMatiereLabel);
		    nomDevoirET = (EditText) findViewById(R.id.devoirAddNomET);
		    descDevoirET = (EditText) findViewById(R.id.devoirAddDescET);
		    dateDeadlineET = (EditText) findViewById(R.id.devoirAddDeadlineTE);
		    dateDeadlineET.setOnTouchListener(new OnTouchListener(){ 
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(v == dateDeadlineET)
					{
						showDialog(DATEDEADLINE_DIALOG_ID);
					}
					return false;  
				}
			});
		}
		
		if (periode == null)// on cherche la période courante
		{
			Date d = new Date();
			QueryBuilder<Periode> qb = periodeDao.queryBuilder();
			//on récupère les périodes courante (date courant > date_debut et date courante < date_fin)
			//normalement une seule période doit arriver (on n'autorise pas le chevauchement de périodes)
			qb.where(com.studieux.bdd.PeriodeDao.Properties.Date_debut.le(d), com.studieux.bdd.PeriodeDao.Properties.Date_fin.ge(d));
			List<Periode> periodes = qb.list();
			
			if (periodes.size() >= 1)
			{
				periode = periodes.get(0);
				periodeLabel.setText(periode.getNom());
				//Toast.makeText(MatiereActivity.this, "periodetrouvée:" + periode.getNom(), Toast.LENGTH_SHORT).show();
			}
		}
		
		if (periode != null)
		{
			periodeHasChanged();
		}
	}
	
	/**
	 * Appui sur le bouton pour sélectionner la période.
	 * On crée la liste des périodes puis on affiche.
	 * @param v
	 */
	public void selectionnerPeriode(View v)
	{
        //Recupération des periodes en BD
		//On rééxécute la requete a chaque fois. Car il est possible de revenir sur cette page et que la liste des périodes ait changé.
        String ddColumn = PeriodeDao.Properties.Date_debut.columnName;
        String orderBy = ddColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(periodeDao.getTablename(), periodeDao.getAllColumns(), null, null, null, null, orderBy);
        
        if(cursor.getCount() != 0)
        {
            String[] from = {"title", "date_debut", "date_fin"};
            int[] to = { R.id.periodeListItemNomPeriode , R.id.periodeListItemDateDebut, R.id.periodeListItemDateFin };
            
            //On parse la liste pour convertir les long en Date et ranger tout ça correctement, pour affichage
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            cursor.moveToFirst();
            do
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
            } while (cursor.moveToNext());
            
            //Toast.makeText(MatiereActivity.this, "lol:" + data.size(), Toast.LENGTH_SHORT).show();	
            
            //Adapter pour notre listView
            SimpleAdapter adapter = new SimpleAdapter(this, 
            		data,
            		R.layout.periodepopup_list_item,
                    from,
                    to);
    		final Integer i = -1;
    		PeriodeSelectionDialog dlg = new PeriodeSelectionDialog(DevoirsAddActivity.this, adapter, i);
    		dlg.setTitle("Liste des périodes");
    		dlg.setDialogListener( new MyDialogListener()
    	    {
    		    public void userCanceled()
    		    {
    		    }
    			@Override
    			public void userSelectedAValue(Long value) {
    				// l'utilisateur a choisi une période dans la liste
    				
    				//Toast.makeText(MatiereActivity.this, "id: " + value, Toast.LENGTH_SHORT).show();
    				periodeToChange(value);
    			}
    		});
    	   
    		dlg.show();
    		dlg.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					
				}
			});
    		Display display =((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	    Point p = new Point();
    	    display.getSize(p);
    		int width = p.x;
    	    int height=p.y;

    	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    	    dlg.getWindow().setLayout((9*width)/10,(9*height)/10);
        }		
	}
	
	public void periodeToChange(Long id)
	{
		this.periode = periodeDao.load(id);
		//Toast.makeText(MatiereActivity.this, "id: " + periode.getNom(), Toast.LENGTH_SHORT).show();
		periodeLabel.setText(periode.getNom());
		periodeHasChanged();
	}
	
	/**
	 * La période vient de changer.
	 */
	public void periodeHasChanged()
	{
		matiereLabel.setText("Sélectionnez une matière");
	}
	
	public void selectionnerMatiere(View v)
	{
        //Recupération des matières liées à la Période
		periode.resetMatiereList();
        List<Matiere> listeMatieres = periode.getMatiereList();
        
        if(listeMatieres.size() != 0)
        {
            String[] from = {"title", "coef"};
            int[] to = { R.id.matiereListItemNomMatiere , R.id.matiereListItemCoef };
            
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            for (Matiere mat : listeMatieres) {
            	Map<String, String> datum = new HashMap<String, String>(3);
            	datum.put("id", "" + mat.getId() );
            	datum.put("title", mat.getNom());
            	datum.put("coef", "" + mat.getCoef());
            	data.add(datum);
			}
            
            //Toast.makeText(MatiereActivity.this, "taille liste:" + data.size(), Toast.LENGTH_SHORT).show();	
            
            //Adapter pour notre listView
            SimpleAdapter adapter = new SimpleAdapter(this, 
            		data,
            		R.layout.matiere_list_item,
                    from,
                    to);
    		final Integer i = -1;
    		PeriodeSelectionDialog dlg = new PeriodeSelectionDialog(DevoirsAddActivity.this, adapter, i);
    		dlg.setTitle("Liste des matières");
    		dlg.setDialogListener( new MyDialogListener()
    	    {
    		    public void userCanceled()
    		    {
    		    }
    			@Override
    			public void userSelectedAValue(Long value) {
    				// l'utilisateur a choisi une matière dans la liste
    				
    				//Toast.makeText(MatiereActivity.this, "id: " + value, Toast.LENGTH_SHORT).show();
    				matiereToChange(value);
    			}
    		});
    	   
    		dlg.show();
    		dlg.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					
				}
			});
    		Display display =((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	    Point p = new Point();
    	    display.getSize(p);
    		int width = p.x;
    	    int height=p.y;

    	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    	    dlg.getWindow().setLayout((9*width)/10,(9*height)/10);
        }
        else //pas de matière
        {
        	new AlertDialog.Builder(this).setTitle("Pas de matière dans cette période")
			.setMessage("Veuillez au moins une matière dans cette période")
			.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intention = new Intent(DevoirsAddActivity.this, MatiereActivity.class);
					intention.putExtra("periodeId", periode.getId());
					startActivity(intention);
				}
			})
			.setNegativeButton("Annuler", null)
			.show();
			return;
        }
	}
	
	/**
	 * La matière sélectionnée vient de changer.
	 * @param id ID de la matière sélectionnée
	 */
	public void matiereToChange(Long id)
	{
		this.matiere = matiereDao.load(id);
		//Toast.makeText(MatiereActivity.this, "id: " + periode.getNom(), Toast.LENGTH_SHORT).show();
		matiereLabel.setText(matiere.getNom());
	}
	
	/**
	 * Appui sur le bouton "Enregistrer"
	 * @param v
	 */
	public void enregistrer(View v)
	{
		
		//vérifier que le devoir a un nom
		if (nomDevoirET.getText().toString().equals("") )
		{
			new AlertDialog.Builder(this).setTitle("Nom vide")
			.setMessage("Veuillez au moins entrer un nom pour le devoir")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		devoir.setNom(nomDevoirET.getText().toString());
		
		//vérifier qu'une matière est sélectionnée
		if (matiere==null)
		{
			new AlertDialog.Builder(this).setTitle("Pas de matière")
			.setMessage("Veuillez sélectionner une matière à laquelle sera rattaché le devoir.")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		devoir.setMatiere(matiere);
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");		
		Date dateDeadline = null;
		Date dateNow = new Date();

		try {
			dateDeadline = formatter.parse(this.dateDeadlineET.getText().toString());
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e("Erreur CAST", "Erreur du cast de String en Date. " + e.getMessage());
		}
		
		if (dateDeadline != null)
		{
			if (dateNow.after(dateDeadline) )
			{
				new AlertDialog.Builder(this).setTitle("Date incorrecte")
				.setMessage("La date de rendu de ce devoir est inférieure à la date du jour. Veuillez entrer une date représentant un jour futur.")
				.setPositiveButton("OK", null)
				.show();
				return;
			}
		}
		else
		{
			new AlertDialog.Builder(this).setTitle("Date incorrecte")
			.setMessage("La date de rendu de ce devoir semble incorrecte...")
			.setPositiveButton("OK", null)
			.show();
			return;
		}
		devoir.setDeadline(dateDeadline);
		
		devoir.setDescription(descDevoirET.getText().toString());
		
		if (devoirDao.insert(devoir) != 0 )
		{
			Toast.makeText(DevoirsAddActivity.this, "Devoir enregistré", Toast.LENGTH_SHORT).show();	
		}
		else
		{
			Toast.makeText(DevoirsAddActivity.this, "Problème lors de l'enregistrement", Toast.LENGTH_SHORT).show();	
		}

		daoSession.update(devoir);

		//Toast.makeText(PeriodeAddActivity.this, "pId:" + periode.getId(), Toast.LENGTH_SHORT).show();

		db.close();

		finish();
	}

}
