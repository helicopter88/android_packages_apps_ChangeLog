package com.helicopter88.changelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class MainActivity extends Activity {

    public static ArrayList<ArrayList<ListItem>> itemArray;
    public static ArrayList<ArrayList<String>> commitArray;
    public static String Query;
    private static ArrayList<ArrayAdapter<String>> itemAdapter;

    private static ArrayList<ListView> lvItem;
    private static Future<File> sChangelog;
    private static File Changelog;
    private TabHost tabHost;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        downloadChangelog();

    }

    private void downloadChangelog() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Toast.makeText(this, "Downloading...", Toast.LENGTH_LONG).show();

        sChangelog = Ion.with(this, "http://carbon-rom.com/changelog/" +
                "changelog.txt")
                .progressBar(progressBar)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(int downloaded, int total) {
                        System.out.println("" + downloaded + " / " + total);
                    }
                })
                .write(getFileStreamPath("changelog.txt"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null)
                            return;
                        try {
                            Changelog = sChangelog.get();
                        } catch (Exception e1) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());
                            ad.setTitle("Download of the file failed \n please check your connection").setCancelable(false);
                            ad.setNeutralButton("Close",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.this.finish();
                                }
                            });
                        }
                        setUpView();
                        setUpLv();
                    }
                });

        progressBar.setEnabled(false);

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
        short date = -1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(Changelog));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("--")) {
                    date++;
                }
                itemArray.get(date).add(new ListItem(line.trim()));
                commitArray.get(date).add(itemArray.get(itemArray.size() - 1).get(date).Commit);
                itemAdapter.get(date).notifyDataSetChanged();

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            itemArray.get(0).add(new ListItem(2));
            lvItem.get(0).setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    itemArray.clear();
                    itemAdapter.get(0).notifyDataSetChanged();
                    setUpLv();
                }
            });
        }

        while(lvItem.iterator().hasNext())
            lvItem.iterator().next().setClickable(true);


    }


    private void setUpView() {
        /** Seems more complex,but should do the trick */

        ArrayList<TabSpec> ts = new ArrayList<>();
        commitArray = new ArrayList<>();
        itemAdapter = new ArrayList<>();
        itemArray = new ArrayList<>();
        lvItem = new ArrayList<>();

        for (int i = 0; i <= 2; i++) {
            String day = Integer.toString(i + 1);
            int id = getResources().getIdentifier("listView" + day, "id", "com.helicopter88.changelog");
            int tid = getResources().getIdentifier("tab" + day, "id", "com.helicopter88.changelog");
            lvItem.add(i, (ListView) this.findViewById(id));
            itemArray.add(i, new ArrayList<ListItem>());
            commitArray.add(i, new ArrayList<String>());
            itemAdapter.add(i, new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, commitArray.get(i)));
            lvItem.get(i).setAdapter(itemAdapter.get(i));
            ts.add(i, tabHost.newTabSpec("Day" + day));
            ts.get(i).setIndicator("Day" + day).setContent(tid);
            tabHost.addTab(ts.get(i));
        }

    }
}
