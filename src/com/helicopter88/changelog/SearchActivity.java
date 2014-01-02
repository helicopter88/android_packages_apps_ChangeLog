package com.helicopter88.changelog;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class SearchActivity extends Activity {

    private static ArrayList<String> searchResults;
    private static ArrayAdapter<String> searchAdapter;
    private static ListView searchItem;

    private static final void AddResults(String match) {
        searchResults.clear();
        int i = 1;
        for (ArrayList<ListItem> array : MainActivity.itemArray) {
            for (ListItem element : array) {
                if (element.Commit.contains(match)) {
                    searchResults.add("In Day " + i + " " + element.Commit);
                    searchAdapter.notifyDataSetChanged();
                }
                i++;
            }
            if (searchResults.isEmpty()) {
                searchResults.add("No matches found for " + match);
            }
        }
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpLv();
        AddResults(MainActivity.Query);
    }

    private final void setUpLv() {
        searchItem = (ListView) this.findViewById(R.id.listView_results);
        searchResults = new ArrayList<>();
        searchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, searchResults);
        searchItem.setAdapter(searchAdapter);
    }
}