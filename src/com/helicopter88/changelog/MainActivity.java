package com.helicopter88.changelog;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;


public final class MainActivity extends Activity {
	private static ListView lvItem, lvItem2, lvItem3;

	public static ArrayList<ListItem> itemArray, itemArray2, itemArray3;
	public static ArrayList<String> commitArray, commitArray2, commitArray3;
	private static ArrayAdapter<String> itemAdapter, itemAdapter2,
			itemAdapter3;

	private TabHost tabHost;
	public static String Query;
	private static final File Changelog = new File(Environment.getExternalStorageDirectory().getPath(),"changelog.txt");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        downloadChangelog();
	}

    private void downloadChangelog() {
        ProgressBar progressBar = (ProgressBar) findViewById (R.id.progressBar);
        Toast.makeText(this,"Downloading...",Toast.LENGTH_LONG).show();
        Ion.with(this, "http://carbon-rom.com/changelog/changelog.txt")
                .progressBar(progressBar)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(int downloaded, int total) {
                        System.out.println("" + downloaded + " / " + total);
                    }
                })
                .write(Changelog);
        progressBar.setEnabled(false);
        setUpView();
        setUpLv();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView;
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				Query = arg0;
				Intent launchNewIntent = new Intent(MainActivity.this,
						SearchActivity.class);
				startActivityForResult(launchNewIntent, 0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}

		});

		return true;
	}


	private void setUpLv() {

		/** Parse the file **/
		short date = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(Changelog));
			String line;

			while ((line = br.readLine()) != null) {
				if (line.contains("--")) {
					date++;
				}
				switch (date) {
				case 1:
					itemArray.add(new ListItem(line.trim()));
					commitArray.add(itemArray.get(itemArray.size() - 1).Commit);
					itemAdapter.notifyDataSetChanged();
					break;
				case 2:
					itemArray2.add(new ListItem(line.trim()));
					commitArray2
							.add(itemArray2.get(itemArray2.size() - 1).Commit);
					itemAdapter2.notifyDataSetChanged();
					break;
				case 3:
					itemArray3.add(new ListItem(line.trim()));
					commitArray3
							.add(itemArray3.get(itemArray3.size() - 1).Commit);
					itemAdapter3.notifyDataSetChanged();
					break;
				default:
					itemArray.add(new ListItem(1));
					commitArray.add(itemArray.get(itemArray.size() - 1).Commit);
					itemAdapter.notifyDataSetChanged();
				}
			}
			br.close();
			lvItem.setClickable(true);
			lvItem2.setClickable(true);
			lvItem3.setClickable(true);

			lvItem.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (!itemArray.get(position).Url.isEmpty()) {
						Uri uri = Uri.parse(itemArray.get(position).Url);
						Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
								uri);
						startActivity(launchBrowser);

					} else {
						Toast.makeText(getApplicationContext(),
								"Not a valid URL", Toast.LENGTH_SHORT).show();
					}
				}
			});

			lvItem2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (!itemArray.get(position).Url.isEmpty()) {
						Uri uri = Uri.parse(itemArray2.get(position).Url);
						Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
								uri);
						startActivity(launchBrowser);
					} else {
						Toast.makeText(getApplicationContext(),
								"Not a valid URL", Toast.LENGTH_SHORT).show();
					}
				}
			});

			lvItem3.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (!itemArray.get(position).Url.isEmpty()) {
						Uri uri = Uri.parse(itemArray3.get(position).Url);
						Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
								uri);
						startActivity(launchBrowser);
					} else {
						Toast.makeText(getApplicationContext(),
								"Not a valid URL", Toast.LENGTH_SHORT).show();
					}
				}
			});

		} catch (IOException e) {
			itemArray.add(new ListItem(2));
			lvItem.setClickable(true);
			lvItem.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					itemArray.clear();
					itemAdapter.notifyDataSetChanged();
					setUpLv();
				}
			});

		}

	}

	private void setUpView() {
		/** I wish there was a better way **/

		lvItem = (ListView) this.findViewById(R.id.listView);
		lvItem2 = (ListView) this.findViewById(R.id.listView2);
		lvItem3 = (ListView) this.findViewById(R.id.listView3);

		itemArray = new ArrayList<ListItem>();
		itemArray2 = new ArrayList<ListItem>();
		itemArray3 = new ArrayList<ListItem>();

		commitArray = new ArrayList<String>();
		commitArray2 = new ArrayList<String>();
		commitArray3 = new ArrayList<String>();

		itemAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, commitArray);
		lvItem.setAdapter(itemAdapter);
		itemAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, commitArray2);
		lvItem2.setAdapter(itemAdapter2);
		itemAdapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, commitArray3);
		lvItem3.setAdapter(itemAdapter3);

		TabSpec spec1 = tabHost.newTabSpec("Day 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Day 1");

		TabSpec spec2 = tabHost.newTabSpec("Day 2");
		spec2.setIndicator("Day 2");
		spec2.setContent(R.id.tab2);

		TabSpec spec3 = tabHost.newTabSpec("Day 3");
		spec3.setContent(R.id.tab3);
		spec3.setIndicator("Day 3");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
	}
}
