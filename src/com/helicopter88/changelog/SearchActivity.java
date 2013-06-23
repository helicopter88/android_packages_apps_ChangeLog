package com.helicopter88.changelog;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class SearchActivity extends Activity {

	private static ArrayList<String> searchResults;
	private static ArrayList<String> urlArray;
	private static ArrayAdapter<String> searchAdapter;
	private static ListView searchItem;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setUpLv();
		AddResults(MainActivity.Query);
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
