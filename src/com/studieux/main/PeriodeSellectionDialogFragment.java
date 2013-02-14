package com.studieux.main;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PeriodeSellectionDialogFragment extends ListFragment{
	
	SimpleAdapter adapter;
	private String dataArray[];
	
	public PeriodeSellectionDialogFragment(SimpleAdapter adap)
	{
		this.adapter = adap;
		
		dataArray = new String[] {
				"One",
				"Two",
				"Three",
		};
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataArray);
		setListAdapter(listAdapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.periode_select_popup, container, false);
	}
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		/**
		 * Toast message will be shown when you click any list element
		 */
		Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
		getActivity().getFragmentManager().popBackStack();
	}
	
}
