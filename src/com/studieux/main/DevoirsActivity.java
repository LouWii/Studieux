package com.studieux.main;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DevoirsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devoirs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_devoirs, menu);
		return true;
	}

}
