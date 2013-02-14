package com.studieux.main;

import com.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MenuActivity extends Activity {

	MenuButton[] menuButtons;
	Integer currentButtonIndex;
	protected SlidingMenu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void initMenu()
	{
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

        this.menuButtons = new MenuButton[5];
        
        this.menuButtons[0] = (MenuButton) findViewById(R.id.acceuilButton);
        this.menuButtons[1] = (MenuButton) findViewById(R.id.periodesButton);
        this.menuButtons[2] = (MenuButton) findViewById(R.id.devoirsButton);
        this.menuButtons[3] = (MenuButton) findViewById(R.id.matieresButton);
        this.menuButtons[4] = (MenuButton) findViewById(R.id.parametresButton);
       
        for(int i = 0 ; i < menuButtons.length ; i++)
        {
        	menuButtons[i].setActivity(this);
        	menuButtons[i].setTag(i);
        }
        
        this.addHomeButtonAction();
	}
	
	
	protected void addHomeButtonAction()
	{
		View v = this.findViewById(android.R.id.home);
  	  	v.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getMenu().toggle();
			}
		});
	}
	
	protected void goToNext(Integer tag)
	{
		Intent intention = null;
		switch (tag) {
		case 0:
			intention = new Intent(MenuActivity.this, MainActivity.class);
			intention.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		case 1 :
			 intention = new Intent(MenuActivity.this, PeriodeActivity.class);
			
			break;
		default:
			intention = new Intent(MenuActivity.this, CoursActivity.class);
			break;
		}
		startActivity(intention);
		if(tag == 0)
		{
			overridePendingTransition(R.anim.animation_back_enter,
	                R.anim.animation_back_leave);
		}
		else
		{
			this.overridePendingTransition(R.anim.animation_enter,
			        R.anim.animation_leave);
			menu.toggle();
		}
		//this.overridePendingTransition(R.anim.animation_enter,
          //      R.anim.animation_leave);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.animation_back_enter,
                R.anim.animation_back_leave);
}

	public SlidingMenu getMenu() {
		return menu;
	}


	public void setMenu(SlidingMenu menu) {
		this.menu = menu;
	}
	
}
