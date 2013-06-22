package com.helicopter88.changelog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.ArrayList;

import com.helicopter88.changelog.R;

public class MainActivity extends Activity {
	private static ListView lvItem, lvItem2, lvItem3;
	private static ArrayList<String> itemArray, itemArray2, itemArray3;
	private static ArrayAdapter<String> itemAdapter, itemAdapter2,
			itemAdapter3;
	
	private static ArrayList<String> urlArray, urlArray2, urlArray3;
	private static ArrayList<String> project = new ArrayList<String>();

	private TabHost tabHost;

	private static final String SD_CARD = Environment
			.getExternalStorageDirectory().getPath();

	/** SU related defines **/
	private static final String SYSTEM_CHANGELOG_PATH = "/system/etc/changelog.txt";
	private static final String CHANGELOG_PATH = SD_CARD + "/changelog.txt";
	private static final String CP_COMMAND = "su -c 'cp -f "
			+ SYSTEM_CHANGELOG_PATH + " " + CHANGELOG_PATH + "'";

	public static String formatChangelog(String line) {
		StringBuilder sb = new StringBuilder();
		String[] splitted = line.split("\\|");

		for (String str : splitted) {

			if (str == splitted[splitted.length - 1]) {

				sb.append(str.trim());

			} else {
				sb.append(str.trim() + "\n");
			}
		}
		if (!sb.toString().contains("project"))
			sb.delete(0, 49);

		return sb.toString();

	}

	public static String parseUrl(String line) {
		StringBuilder remoteUrl = new StringBuilder();
		StringBuilder finalUrl = new StringBuilder();
		String[] remotes = line.split("\\| Remote: ");

		if (line.contains("project")) {

			String remote = line.substring(8, (line.length() - 1)).replace("/",
					"_");
			if (remote.contains("android")) {
				remoteUrl.append("android");
			} else {
				remoteUrl.append("android_");
				remoteUrl.append(remote);
			}
			project.add(remoteUrl.toString());
		}

		for (String srt : remotes) {
			if (!line.isEmpty() && line.length() > 50) {
				String commit_hash = line.substring(9, 50);

				if (srt.contains("cr")) {
					finalUrl.append("https://github.com/CarbonDev/");
				} else if (srt.contains("cm") && !srt.contains("cr")) {
					finalUrl.append("https://github.com/CyanogenMod/");
				} else {
					finalUrl.append("https://github.com/CarbonDev/");
				}

				finalUrl.append(project.get(project.size() - 1).trim());
				finalUrl.append("/commit/".trim());
				finalUrl.append(commit_hash.trim());
				// More remotes should be done,but how to handle gh?
			}
			return finalUrl.toString();
		}
		return "";

	}

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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
					itemArray.add(formatChangelog(line.trim()));
					urlArray.add(parseUrl(line.trim()));
					itemAdapter.notifyDataSetChanged();
					break;
				case 2:
					itemArray2.add(formatChangelog(line.trim()));
					urlArray2.add(parseUrl(line.trim()));
					itemAdapter2.notifyDataSetChanged();
					break;
				case 3:
					itemArray3.add(formatChangelog(line.trim()));
					urlArray3.add(parseUrl(line.trim()));
					itemAdapter3.notifyDataSetChanged();
					break;
				default:
					itemArray.add("Ill-formed changelog");
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
					Uri uri = Uri.parse(urlArray.get(position));
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(launchBrowser);
				}
			});

			lvItem2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Uri uri = Uri.parse(urlArray2.get(position));
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(launchBrowser);
				}
			});

			lvItem3.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Uri uri = Uri.parse(urlArray3.get(position));
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(launchBrowser);
				}
			});

		} catch (IOException e) {
			itemArray
					.add(0,
							"Unable to parse changelog \n Please tap here to \n parse it again");
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

		lvItem = (ListView) this.findViewById(R.id.listView1);
		lvItem2 = (ListView) this.findViewById(R.id.listView2);
		lvItem3 = (ListView) this.findViewById(R.id.listView3);

		itemArray = new ArrayList<String>();
		itemArray2 = new ArrayList<String>();
		itemArray3 = new ArrayList<String>();

		urlArray = new ArrayList<String>();
		urlArray2 = new ArrayList<String>();
		urlArray3 = new ArrayList<String>();

		itemAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, itemArray);
		lvItem.setAdapter(itemAdapter);
		itemAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, itemArray2);
		lvItem2.setAdapter(itemAdapter2);
		itemAdapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, itemArray3);
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
