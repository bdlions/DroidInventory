package com.webinventory;

import java.util.Properties;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams.Versions;

import android.app.Activity;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.webinventory.parser.customer.AssetsPropertyReader;
import com.webinventory.parser.queue.QueueList;

public class MainActivity extends Activity {
	Button selectedQueuebtn;
	TableLayout tlayout_queue_list;
	TableRow tableRow;
	TextView textView;
	RadioGroup rg ;
	RadioButton[] rb;
	//LayoutParams layoutParams;
	private final int WC = RadioGroup.LayoutParams.MATCH_PARENT;
	private final int HC = RadioGroup.LayoutParams.WRAP_CONTENT;
	
	private QueueList queue_list;
	String[][] queue_info = new String[5][5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Here we are reading server url from properties file
		AssetsPropertyReader asserproperties = new AssetsPropertyReader(MainActivity.this);
		Properties project_properties = asserproperties.getProperties("project_config.properties");
		
		JSONHandler task = new JSONHandler();
	    task.execute(new String[] {project_properties.getProperty("server_url") + "androidrpc/qprovider/"});

		selectedQueuebtn = (Button) findViewById(R.id.btnQueueSelect);
	}
	
	
	public void sendSelectedQueue(View v) {
		Intent intent= new Intent(MainActivity.this, MenuActivity.class);

		int radioButtonID = rg.getCheckedRadioButtonId();
		if(radioButtonID > 0 ) {
		 	intent.putExtra("QUQUE_ID", Integer.toString(radioButtonID) );
		 	startActivity(intent);
		} else {
			Toast.makeText(MainActivity.this, "You have to select one queue", Toast.LENGTH_LONG).show();
		}
		 
	}


	private class JSONHandler extends AsyncTask<String, Void, String> {

	    @Override
	    protected String doInBackground(String... urls) {
	        for (String url : urls) {
	            JSONRPCClient client = JSONRPCClient.create(url, Versions.VERSION_2);
	            client.setConnectionTimeout(2000);
	            client.setSoTimeout(2000);

	            try {
	            	tlayout_queue_list = (TableLayout) findViewById(R.id.tlQueueList);
	            	String qList = client.callString("getQList");
	            	System.out.println(qList);
	            	
	            	Gson gson = new Gson();
	            	queue_list = gson.fromJson(qList, QueueList.class);
	        		System.out.println(queue_list.queues.size());
	        		
	        		runOnUiThread(new Runnable()
	        		{
	        			@Override
	        			public void run()
	        			{
	        				TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
	        				layoutParams.setMargins (5, 5, 5, 5);
	        				rb = new RadioButton[queue_list.queues.size()];
	        	    		rg = new RadioGroup(getApplicationContext());
	        	    		RadioGroup.LayoutParams rParams;
	        	    		int k = 0;
	        	    		for (com.webinventory.parser.queue.Queue queues : queue_list.queues) {
	        	    	        rb[k]  = new  RadioButton(MainActivity.this);
	        	    	        rb[k].setId(queues.id);
	        	    	        rb[k].setText("					" + queues.name + "						"  + queues.noOfMsgs);
	        	    	        rParams = new RadioGroup.LayoutParams(WC, HC);
	        	    	        rg.addView(rb[k],0,layoutParams); //the RadioButtons are added to the radioGroup instead of the layout
	        	    	        
	        	    	        k++;
	        	    	    }
	        				tlayout_queue_list.addView(rg);//you add the whole RadioGroup to the layout	
	        			}
	        		});
	        		
	            } catch (JSONRPCException e) {
	                e.printStackTrace(); //Invalid JSON Response caught here
	            }
	        }
	        return null;
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
