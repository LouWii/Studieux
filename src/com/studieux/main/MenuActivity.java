package com.studieux.main;

import com.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MenuActivity extends Activity {

	protected MenuButton coursButton;
	


	protected MenuButton devoirsButton;
	protected MenuButton matieresButton;
	protected MenuButton parametresButton;
	protected MenuButton currentButton;
		
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
		System.out.println("heheeeeee");
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
        coursButton.setActivity(this);
        devoirsButton = (MenuButton) findViewById(R.id.devoirsButton);
        devoirsButton.setActivity(this);
        matieresButton = (MenuButton) findViewById(R.id.matieresButton);
        matieresButton.setActivity(this);
        parametresButton = (MenuButton) findViewById(R.id.parametresButton);
        parametresButton.setActivity(this);
        currentButton = coursButton;
	}
	
	protected void goToNext(String tag)
	{
		menu.toggle();
		Intent i = new Intent(this, CoursActivity.class);
		startActivity(i);
	}
	
	
	
	public MenuButton getCoursButton() {
		return coursButton;
	}


	public void setCoursButton(MenuButton coursButton) {
		this.coursButton = coursButton;
	}


	public MenuButton getDevoirsButton() {
		return devoirsButton;
	}


	public void setDevoirsButton(MenuButton devoirsButton) {
		this.devoirsButton = devoirsButton;
	}


	public MenuButton getMatieresButton() {
		return matieresButton;
	}


	public void setMatieresButton(MenuButton matieresButton) {
		this.matieresButton = matieresButton;
	}


	public MenuButton getParametresButton() {
		return parametresButton;
	}


	public void setParametresButton(MenuButton parametresButton) {
		this.parametresButton = parametresButton;
	}


	public MenuButton getCurrentButton() {
		return currentButton;
	}


	public void setCurrentButton(MenuButton currentButton) {
		this.currentButton = currentButton;
	}


	public SlidingMenu getMenu() {
		return menu;
	}


	public void setMenu(SlidingMenu menu) {
		this.menu = menu;
	}
	
}
