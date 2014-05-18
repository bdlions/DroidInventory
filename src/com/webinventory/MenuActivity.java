package com.webinventory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;


import com.google.gson.Gson;
import com.webinventory.parser.Customer;
import com.webinventory.parser.Queue;

public class MenuActivity extends Activity {
	private Button button;
	final Context context = this;
	private Queue queue;
	
	TableLayout table_layout;
	/*int rows = 4;
	int col = 4;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_menu);
		table_layout = (TableLayout) findViewById(R.id.tableLayout1);

	    TableRow tableRow;
	    TextView textView;

	    /*for (int i = 0; i < 4; i++) {
	        tableRow = new TableRow(getApplicationContext());
	        for (int j = 0; j < 3; j++) {
	            textView = new TextView(getApplicationContext());
	            textView.setText("test"+ i +j);
	            textView.setPadding(20, 20, 20, 20);
	            tableRow.addView(textView);
	        }
	        table_layout.addView(tableRow);
	    }*/
		
		
		button = (Button) findViewById(R.id.buttonSendSMS);
		
		String[][] customer_info = new String[3][3];
		
		Gson gson = new Gson();
		BufferedReader bufferedReader;
		BufferedInputStream bis = new BufferedInputStream(this.getResources()
				.openRawResource(R.raw.sample2));
		bufferedReader = new BufferedReader(new InputStreamReader(bis));
		queue = gson.fromJson(bufferedReader, Queue.class);
		int i = 0;
		for (Customer customer : queue.customer) {
		i++;
		tableRow = new TableRow(getApplicationContext());
		String content = customer.phone.message.content;
		int status = customer.phone.message.status;
		String strStatus = Integer.toString(status);
		String phoneNo = customer.phone.phoneNo;
		customer_info[i][0] = phoneNo;
		customer_info[i][1] = content;
		customer_info[i][2] = strStatus;
		
		for (int j= 0; j<3 ; j++) {
			textView = new TextView(getApplicationContext());
            textView.setText(customer_info[i][j]);
            textView.setPadding(20, 20, 20, 20);
            tableRow.addView(textView);
		}
			table_layout.addView(tableRow);
		}
		button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	for (Customer customer : MenuActivity.this.queue.customer) {
        			String content = customer.phone.message.content;
        			String phoneNo = customer.phone.phoneNo;
        			System.out.println("Phone no is : " + phoneNo + " SMS content is :" + content);
        			 if (phoneNo.length()>0 && content.length()>0){
        				 //buildTable(rows, col);
        				 sendSMS(phoneNo, content);
        			 }               
                     else
                     	Toast.makeText(getBaseContext(),
                             "Please enter both phone number and message.", 
                             Toast.LENGTH_SHORT).show();
        		}
            }
        });
	}
	
	/*private void buildTable(int rows, int cols) {

		// outer for loop
		for (int i = 1; i <= rows; i++) {

			TableRow row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			// inner for loop
			for (int j = 1; j <= cols; j++) {
				TextView tv = new TextView(this);
				tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				tv.setBackgroundResource(R.drawable.ic_launcher);
				tv.setPadding(5, 5, 5, 5);
				tv.setText("R " + i + ", C" + j);

				row.addView(tv);

			}

			table_layout.addView(row);

		}
	}*/
	
	//---sends a SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {      
    	/*
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, test.class), 0);                
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, pi, null);        
        */
    	
    	String SENT = "SMS_SENT";
    	String DELIVERED = "SMS_DELIVERED";
    	
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
        
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
    	
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS sent", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					    Toast.makeText(getBaseContext(), "Generic failure", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NO_SERVICE:
					    Toast.makeText(getBaseContext(), "No service", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NULL_PDU:
					    Toast.makeText(getBaseContext(), "Null PDU", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_RADIO_OFF:
					    Toast.makeText(getBaseContext(), "Radio off", 
					    		Toast.LENGTH_SHORT).show();
					    break;
					default:
						Toast.makeText(getBaseContext(), "Default", 
					    		Toast.LENGTH_SHORT).show();
				}
			}
        }, new IntentFilter(SENT));
        
        
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				Toast.makeText(getBaseContext(), "Delivery report", 
			    		Toast.LENGTH_SHORT).show();
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case Activity.RESULT_CANCELED:
					    Toast.makeText(getBaseContext(), "SMS not delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;					    
				}
			}
        }, new IntentFilter(DELIVERED)); 
        
        SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);         
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
