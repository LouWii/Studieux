package com.studieux.main;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.R.color;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			GraphicalView chartView;
			XYSeries series ;
			series = new XYSeries("notes");
			series.add(1, 2);
			series.add(2, 20);
			series.add(3, 5);
			series.add(4, 15);
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			dataset.addSeries(series);
			XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
			chartView = ChartFactory.getBarChartView(this, dataset,renderer, Type.DEFAULT);
			setChartSettings(renderer);
			
			LinearLayout layout = (LinearLayout) findViewById(R.id.layoutGraph);
			layout.addView(chartView);
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
		renderer.addXTextLabel(1, "AAC");
		renderer.addXTextLabel(2, "Mon"); 
		renderer.addXTextLabel(3, "Tue"); 
		renderer.addXTextLabel(4, "Wed");
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
