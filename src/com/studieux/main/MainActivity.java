package com.studieux.main;

import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.app.DialogFragment;
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
		v1.setBackgroundColor(getResources().getColor(R.color.redButtons));
		currentButtonIndex = 0;
  	  	View v = this.findViewById(android.R.id.home);
  	  	v.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getMenu().toggle();
			}
		});
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
