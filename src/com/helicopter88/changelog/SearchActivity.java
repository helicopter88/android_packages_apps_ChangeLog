package com.helicopter88.changelog;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public final class SearchActivity extends Activity {
	
	private static ArrayList<String> searchResults;
	private static ArrayAdapter<String> searchAdapter;
	private static ListView searchItem;
	private static EditText editText;
	private static Button searchButton;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_search);
		setUpLv();
		searchButton = (Button) this.findViewById(R.id.button1);
		editText = (EditText) this.findViewById(R.id.editText1);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddResults(editText.getText().toString());
			}
		});
	}
	
	protected final void setUpLv()
	{
		searchItem = (ListView) this.findViewById(R.id.listView_results);
		searchResults = new ArrayList<String>();
		searchAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, searchResults);
		searchItem.setAdapter(searchAdapter);
	}

	private static final void AddResults(String match)
	{
		searchResults.clear();
		for(String search : MainActivity.itemArray)
		{
			if(search.contains(match))
			{
				searchResults.add("In day 1 \n" + search);
				searchAdapter.notifyDataSetChanged();
			}
		}
		for(String search : MainActivity.itemArray2)
		{
			if(search.contains(match))
			{
				searchResults.add("In day 2 \n" + search);
				searchAdapter.notifyDataSetChanged();
			}
		}
		for(String search : MainActivity.itemArray2)
		{
			if(search.contains(match))
			{
				searchResults.add("In day 3 \n" + search);
				searchAdapter.notifyDataSetChanged();
			}
		}
		if(searchResults.isEmpty())
		{
			searchResults.add("No results found");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

}
