package com.helicopter88.changelog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private ListView lvItem;
    private ArrayList<String> itemArray;
    private ArrayAdapter<String> itemAdapter;

    private static final String SD_CARD = Environment.getExternalStorageDirectory().getPath();

    /** SU related defines						  **/
    private static final String SYSTEM_CHANGELOG_PATH = "/system/etc/changelog.txt";
    private static final String CHANGELOG_PATH = SD_CARD + "/changelog.txt";
    private static final String CP_COMMAND = "su -c 'cp -f " + SYSTEM_CHANGELOG_PATH + " " + CHANGELOG_PATH +"'";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        itemArray = new ArrayList<String>();
        itemArray.clear();
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemArray);

	    setContentView(R.layout.activity_main);
        setUpView();

        
        try {
            /** Let's make sure our file is in the external storage **/
	        RunAsRoot(CP_COMMAND);
	        
	        /** Get sdcard directory **/
	        File sdcard = Environment.getExternalStorageDirectory();
	      
            /** Read file **/
	        File file = new File(sdcard,"changelog.txt");

	        try {
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String line;

	            while ((line = br.readLine()) != null) {
	                itemArray.add(0,formatChangelog(line));
	                itemAdapter.notifyDataSetChanged();
	            }
	      
            } catch (IOException e) {
		        Log.e("ChangeLog",e.toString());
		        itemArray.add(0,"Unable to parse changelog");
            	
            } finally {
            	file.delete();
            }
            
        } catch (IOException e) {
        	Log.e("ChangeLog",e.toString());
        	itemArray.add(0,"Superuser call failed");
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
            	Log.e("ChangeLog",e.toString());
            }
    }
    
    public String formatChangelog(String line)
    {
    	StringBuilder sb = new StringBuilder();
    	String[] splitted = line.split("\\|");
    	for (String str : splitted)
    	{
    		sb.append(str + "\n");
    	}
    	
    	return sb.toString();
    	
    }

}
