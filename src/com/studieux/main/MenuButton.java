package com.studieux.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MenuButton extends LinearLayout {


	private MenuActivity main;

	
	public MenuButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
	
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		this.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!main.currentButtonIndex.equals(MenuButton.this.getTag()))
				{
					main.goToNext((Integer)MenuButton.this.getTag());
				}	
			}
		});
	}
	
	public void setActivity(MenuActivity a)
	{
		this.main = a;
	}
}
