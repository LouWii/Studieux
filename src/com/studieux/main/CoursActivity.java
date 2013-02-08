package com.studieux.main;

import com.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;

public class CoursActivity extends MenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cours);
		initMenu();	
		currentButtonIndex = 1;
		//findViewById(R.id.viewRed2).setBackgroundColor(getResources().getColor(R.color.redButtons));
		//menuButtons[1].drawRedLine();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cours, menu);
		return true;
	}

}
