package com.webinventory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams.Versions;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;


import com.google.gson.Gson;
import com.webinventory.parser.queue.QueueList;

public class MainActivity extends Activity {

	private QueueList queues;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		JSONHandler task = new JSONHandler();
	    task.execute(new String[] {"http://172.17.43.121/webinventory/androidrpc/qprovider/"});
		
	}

	private class JSONHandler extends AsyncTask<String, Void, String> {

	    @Override
	    protected String doInBackground(String... urls) {
	        for (String url : urls) {
	            JSONRPCClient client = JSONRPCClient.create(url, Versions.VERSION_2);
	            client.setConnectionTimeout(2000);
	            client.setSoTimeout(2000);

	            try {
	              
	            	String qList = client.callString("getqlist");
	            	System.out.println(qList);
	            	Gson gson = new Gson();
	        		queues = gson.fromJson(qList, QueueList.class);
	        		System.out.println(queues.queues.size());
	            	
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
