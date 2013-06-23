package com.helicopter88.changelog;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public final class SearchActivity extends Activity {

	private static ArrayList<String> searchResults;
	private static ArrayList<String> urlArray;
	private static ArrayAdapter<String> searchAdapter;
	private static ListView searchItem;
	private static SearchView searchView;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);
		setUpLv();
		searchView = (SearchView) this.findViewById(R.id.searchView1);
		searchView.setOnQueryTextListener(new OnQueryTextListener()
		{

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				AddResults(arg0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		searchItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (!urlArray.get(position).isEmpty()) {
					Uri uri = Uri.parse(urlArray.get(position));
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(launchBrowser);
				} else {
					Toast.makeText(getApplicationContext(), "Not a valid URL", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private final void setUpLv() {
		searchItem = (ListView) this.findViewById(R.id.listView_results);
		searchResults = new ArrayList<String>();
		urlArray = new ArrayList<String>();
		searchAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, searchResults);
		searchItem.setAdapter(searchAdapter);
	}

	private static final void AddResults(String match) {
		searchResults.clear();
		for (ListItem search : MainActivity.itemArray) {
			if (search.Commit.contains(match)) {
				searchResults.add("In day 1 \n" + search.Commit);
				urlArray.add(search.Url);
				searchAdapter.notifyDataSetChanged();
			}
		}
		for (ListItem search : MainActivity.itemArray2) {
			if (search.Commit.contains(match)) {
				searchResults.add("In day 2 \n" + search.Commit);
				urlArray.add(search.Url);
				searchAdapter.notifyDataSetChanged();
			}
		}
		for (ListItem search : MainActivity.itemArray2) {
			if (search.Commit.contains(match)) {
				searchResults.add("In day 3 \n" + search.Commit);
				urlArray.add(search.Url);
				searchAdapter.notifyDataSetChanged();
			}
		}
		if (searchResults.isEmpty()) {
			searchResults.add("No matches found for " + match);
		}

	}
}
