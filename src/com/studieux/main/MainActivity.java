package com.studieux.main;

import com.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.View;
public class MainActivity extends Activity {

	MenuButton coursButton;
	MenuButton devoirsButton;
	MenuButton matieresButton;
	MenuButton parametresButton;
	MenuButton currentButton;
	SlidingMenu menu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int behindOffset = (int) (width - (0.7*width));
		
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);
        menu.setBehindOffset(behindOffset);
        
        coursButton = (MenuButton) findViewById(R.id.coursButton);
        coursButton.drawRedLine();
        coursButton.setActivity(this);
        devoirsButton = (MenuButton) findViewById(R.id.devoirsButton);
        devoirsButton.setActivity(this);
        matieresButton = (MenuButton) findViewById(R.id.matieresButton);
        matieresButton.setActivity(this);
        parametresButton = (MenuButton) findViewById(R.id.parametresButton);
        parametresButton.setActivity(this);
        currentButton = coursButton; 
}

	public void toggle(View v)
	{
		this.menu.toggle();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	

}
