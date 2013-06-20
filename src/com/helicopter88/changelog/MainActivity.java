package com.helicopter88.changelog;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;

public class MainActivity extends Activity {
    private ListView lvItem;
    private ArrayList<String> itemArray;
    private ArrayAdapter<String> itemAdapter;

    private static final String SD_CARD = Environment.getExternalStorageDirectory().getPath();

    /** SU related defines						  **/
    private static final String SYSTEM_CHANGELOG_PATH = "/system/etc/changelog.txt";
    private static final String CHANGELOG_PATH = SD_CARD + "changelog.txt";
    private static final String CP_COMMAND = "su -c 'cp -f /system/etc/changelog.txt /sdcard/changelog.txt'";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        InputStreamReader inputReader = null;
        String file                   = null;
        String last_string            = null;
        String[] splitlines           = null;


        char tmp[] = new char[2048];


        itemArray = new ArrayList<String>();
        itemArray.clear();
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemArray);

	setContentView(R.layout.activity_main);
        setUpView();

        try {
            /** Let's make sure our file is in the external storage **/
	    RunAsRoot(CP_COMMAND);

            /** Read file **/
            inputReader = new FileReader(CHANGELOG_PATH);

		while ((inputReader.read(tmp)) >= 0) {

                    file = new String(tmp);
                    splitlines = file.split("\n");
                }

            for (String str : splitlines) {
		last_string = formatChangelog(str);
                itemArray.add(0,last_string);
                itemAdapter.notifyDataSetChanged();
            }

        } catch (IOException e) {
		Log.e("ChangeLog:",e.toString());
        	itemArray.add(0,"Unable to parse changelog");
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
		Log.e("ChangeLog:",e.toString());
        	itemArray.add(0,"Unable to read changelog");
            }
        }


    }

    private void setUpView() {
        // TODO Auto-generated method stub
        lvItem = (ListView)this.findViewById(R.id.listView_items);


        itemArray = new ArrayList<String>();
        itemArray.clear();

        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemArray);
        lvItem.setAdapter(itemAdapter);



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

    public static String formatChangelog(String rawChangelog) {

    /*
     * Example: | Commit:e38f4ac | Title:Primary output check for sonification is removed. | By:Vimal Puthanveed | Date: whatever
     */

    	final String CHANGELOG_REGEX =
    			"| Commit: (\\S+) " + 			/* group 1: "Commit hash" */
    			"| Title: (\\S+) "  +    		/* group 2: Title         */
    			"| Author: (\\S+) " +   		/* group 3: "#1"          */
    			"| Date: (\\S+) ";  			/* group 4: "Date"        */

    	Matcher m = Pattern.compile(CHANGELOG_REGEX).matcher(rawChangelog);
    	if (!m.matches()) {
    		return "Unavailable";
    	} else if (m.groupCount() < 5) {
    		return "Unavailable";
    	}
    	return  "Commit" + m.group(1) + "\n" +                         // Commit: e38f4ac
    			   m.group(2) + "\n" +                         // Primary output check for sonification is removed.
    			   " by" + m.group(3) + "on" + m.group(4);     // By Vimal Puthanveed on $date
    }
}
