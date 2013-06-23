package com.helicopter88.changelog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.ArrayList;

import com.helicopter88.changelog.R;

public final class MainActivity extends Activity {
	private static ListView lvItem, lvItem2, lvItem3;

	public static ArrayList<ListItem> itemArray, itemArray2, itemArray3;
	public static ArrayList<String> commitArray, commitArray2, commitArray3;
	private static ArrayAdapter<String> itemAdapter, itemAdapter2,
			itemAdapter3;

	private TabHost tabHost;

	private static final String SD_CARD = Environment
			.getExternalStorageDirectory().getPath();

	/** SU related defines **/
	private static final String SYSTEM_CHANGELOG_PATH = "/system/etc/changelog.txt";
	private static final String CHANGELOG_PATH = SD_CARD + "/changelog.txt";
	private static final String CP_COMMAND = "su -c 'cp -f "
			+ SYSTEM_CHANGELOG_PATH + " " + CHANGELOG_PATH + "'";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		/** Let's make sure our file is in the external storage **/
		try {
			RunAsRoot(CP_COMMAND);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setContentView(R.layout.activity_main);
		tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();
		setUpView();
		setUpLv();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent launchNewIntent = new Intent(MainActivity.this,
				SearchActivity.class);
		startActivityForResult(launchNewIntent, 0);
		return true;
	}

	public void RunAsRoot(String cmd) throws IOException {
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		try {
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
		}
	}

	private void setUpLv() {
		/** Get sdcard directory **/
		File sdcard = Environment.getExternalStorageDirectory();

		/** Read file **/
		File file = new File(sdcard, "changelog.txt");
		int date = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
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
						Toast.makeText(getApplicationContext(), "Not a valid URL", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getApplicationContext(), "Not a valid URL", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getApplicationContext(), "Not a valid URL", Toast.LENGTH_SHORT).show();
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

		} finally {
			file.delete();
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
