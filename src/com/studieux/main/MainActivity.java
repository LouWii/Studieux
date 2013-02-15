package com.studieux.main;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.*;

import android.R.color;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.app.DialogFragment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
public class MainActivity extends MenuActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMenu();
		View v1 = findViewById(R.id.viewRed0);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 0;
 
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {  
			      new GraphViewData(0, 9.0d)
			      , new GraphViewData(2, 1.5d)  
			      , new GraphViewData(3, 14.5d)  
			      , new GraphViewData(4, 20.0d)
			      , new GraphViewData(5, 20.0d)  
			});  
			
			GraphView graphView = new BarGraphView(  
			      this // context  
			      , "Mes notes" // heading  
			);  
			graphView.addSeries(exampleSeries); // data  
			graphView.setScrollable(true); 
			graphView.setHorizontalLabels(new String[] {"philo", "AAC", "Voice XML", "Grid"});  
			// optional - activate scaling / zooming 
			graphView.setScalable(false);  			
			LinearLayout layout = (LinearLayout) findViewById(R.id.layoutGraph);  
			layout.addView(graphView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,400)); 
		
}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	public void toggle(View v)
	{
		this.menu.toggle();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		  super.onConfigurationChanged(newConfig);
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
