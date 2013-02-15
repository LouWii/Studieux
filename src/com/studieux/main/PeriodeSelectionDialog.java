package com.studieux.main;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.studieux.main.MatiereActivity.MyDialogListener;

public class PeriodeSelectionDialog extends Dialog {

	private Context context;
	private ListView dlg_priority_lvw = null;
	private ListAdapter adapter;
	private Integer i;
	private MyDialogListener dialogListener;
	
	public PeriodeSelectionDialog(Context context, ListAdapter adap, Integer i) {
	    super(context);
	    this.context = context;
	    this.adapter = adap;
	    this.i = i;
	}
	
	public PeriodeSelectionDialog(Context context, int theme, ListAdapter adap) {
	    super(context, theme);
	    this.context = context;
	    this.adapter = adap;
	}
	
	
	public MyDialogListener getDialogListener() {
		return dialogListener;
	}

	public void setDialogListener(MyDialogListener dialogListener) {
		this.dialogListener = dialogListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.selection_periode_layout);
	    dlg_priority_lvw = (ListView) findViewById(R.id.periodeSelectListView);
	    
	    // ListView
	    dlg_priority_lvw.setAdapter((ListAdapter) adapter);
	    //ListView
	    dlg_priority_lvw
	            .setOnItemClickListener(new AdapterView.OnItemClickListener() {

	                @Override
	                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	                	HashMap<String, String> data = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
	                	
	                	//Toast.makeText(context, "id: " + data.get("id"), Toast.LENGTH_SHORT).show();
	                	dialogListener.userSelectedAValue( Long.parseLong(data.get("id")) );
	                	
	                	dismiss();
	                }

	            });
	}
	
}
