package com.webinventory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
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
		
		Gson gson = new Gson();
		BufferedReader bufferedReader;
		BufferedInputStream bis = new BufferedInputStream(this.getResources()
				.openRawResource(R.raw.sample3));
		bufferedReader = new BufferedReader(new InputStreamReader(bis));
		queues = gson.fromJson(bufferedReader, QueueList.class);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
