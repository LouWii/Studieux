package com.studieux.main;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MenuButton extends LinearLayout {

	private View ligneRouge;
	private Activity main;
	
	public MenuButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		ligneRouge = this.getChildAt(0);
        this.setOnTouchListener(new OnTouchListener() 
        {
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(getResources().getColor(R.color.redButtons));
                    MenuButton.this.ligneRouge.setBackgroundColor(getResources().getColor(R.color.redButtons));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                	v.setBackgroundColor(getResources().getColor(R.color.gray_menu));
                	if(!((MainActivity) main).currentButton.equals(MenuButton.this))
            		{
            			((MainActivity)main).currentButton.removeRedColor();
            			((MainActivity)main).currentButton = MenuButton.this;
            		}
                }
				return false;
			}
        });
	}
	
	public void removeRedColor()
	{
		MenuButton.this.ligneRouge.setBackgroundColor(getResources().getColor(R.color.gray_menu));
	}
	
	public void drawRedLine()
	{
		MenuButton.this.ligneRouge.setBackgroundColor(getResources().getColor(R.color.redButtons));
	}
	
	public void setActivity(Activity a)
	{
		this.main = a;
	}
}
