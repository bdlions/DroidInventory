package com.webinventory;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.webinventory.parser.queue.QueueList;

public class MainActivity extends Activity {
	Button selectedQueuebtn;
	TableLayout tlayout_queue_list;
	TableRow tableRow;
	TextView textView;
	RadioGroup rg ;
	RadioButton[] rb;
	
	private QueueList queue_list;
	String[][] queue_info = new String[5][5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		JSONHandler task = new JSONHandler();
	    task.execute(new String[] {"http://192.168.0.102/webinventory/androidrpc/qprovider/"});

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
	            	String qList = client.callString("getqlist");
	            	System.out.println(qList);
	            	
	            	Gson gson = new Gson();
	            	queue_list = gson.fromJson(qList, QueueList.class);
	        		System.out.println(queue_list.queues.size());
	        		
	        		runOnUiThread(new Runnable()
	        		{
	        			@Override
	        			public void run()
	        			{
	        				rb = new RadioButton[queue_list.queues.size()];
	        	    		rg = new RadioGroup(getApplicationContext());
	        	    		
	        	    		int k = 0;
	        	    		for (com.webinventory.parser.queue.Queue queues : queue_list.queues) {
	        	    	        rb[k]  = new  RadioButton(MainActivity.this);
	        	    	        rb[k].setId(queues.id);
	        	    	        rg.addView(rb[k]); //the RadioButtons are added to the radioGroup instead of the layout
	        	    	        
	        	    	        rb[k].setText("		" + queues.name + "			"  + queues.noOfMsgs);
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
