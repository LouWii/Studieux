package com.studieux.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
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
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!((MainActivity)main).currentButton.equals(MenuButton.this))
				{
					MenuButton.this.drawRedLine();
					((MenuActivity)main).currentButton.removeRedColor();
					((MenuActivity)main).currentButton = MenuButton.this;
					((MenuActivity)main).goToNext((String)getTag());
					System.out.println("dflkfjkdfkkdhfkjdh");
				}	
			}
		});
	}
	
	
	//Eleve la bande rouge du bouton
	public void removeRedColor()
	{
		MenuButton.this.ligneRouge.setBackgroundColor(getResources().getColor(R.color.transparent));
	}
	
	
	//dessine la bande rouge du bouton
	public void drawRedLine()
	{
		MenuButton.this.ligneRouge.setBackgroundColor(getResources().getColor(R.color.redButtons));
	}
	
	public void setActivity(Activity a)
	{
		this.main = a;
	}
}
