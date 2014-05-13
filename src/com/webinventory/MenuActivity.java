package com.webinventory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.webinventory.parser.Customer;
import com.webinventory.parser.Queue;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Gson gson = new Gson();
		BufferedReader bufferedReader;
		BufferedInputStream bis = new BufferedInputStream(this.getResources().openRawResource(R.raw.sample2));
		bufferedReader = new BufferedReader(new InputStreamReader(bis));
		Queue queue = gson.fromJson(bufferedReader, Queue.class);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
