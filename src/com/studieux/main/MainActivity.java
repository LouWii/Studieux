package com.studieux.main;

import android.R.color;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
public class MainActivity extends MenuActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMenu();
		View v1 = findViewById(R.id.viewRed0);
		v1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		currentButtonIndex = 0;
  	  	
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
		Intent intention = new Intent(MainActivity.this, PeriodeActivity.class);
		startActivity(intention);
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
