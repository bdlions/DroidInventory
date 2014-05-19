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
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;


import com.google.gson.Gson;
import com.webinventory.parser.customer.Customer;
import com.webinventory.parser.customer.Queue;

public class MenuActivity extends Activity {
	private Button button;
	final Context context = this;
	private Queue queue;
	TableLayout table_layout;
	
	TableRow tableRow;
	TextView textView;
	
	static int i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		table_layout = (TableLayout) findViewById(R.id.tableLayout1);
		button = (Button) findViewById(R.id.buttonSendSMS);

		String[][] customer_info = new String[3][3];

	
		Gson gson = new Gson();
		BufferedReader bufferedReader;
		BufferedInputStream bis = new BufferedInputStream(this.getResources()
				.openRawResource(R.raw.sample2));
		bufferedReader = new BufferedReader(new InputStreamReader(bis));

		queue = gson.fromJson(bufferedReader, Queue.class);
		
		i = 0;
		for (Customer customer : queue.customer) {

			tableRow = new TableRow(this); tableRow.setLayoutParams(new
					  LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			String content = customer.phone.message.content;
			String phoneNo = customer.phone.phoneNo;

			customer_info[i][0] = phoneNo;
			customer_info[i][1] = content;

			CheckBox box = new CheckBox(this);
			box.setId(i);
			box.setChecked(true);
			
			// for testing clicked ckeck button
			/*box.setOnClickListener( new View.OnClickListener() {
			     public void onClick(View v) {
			    	 if (((CheckBox) v).isChecked()) {
				    	 Toast.makeText(MenuActivity.this,"hello :)" + "Id: " + v.getId(), Toast.LENGTH_LONG).show();
			    	 }
			     }
			    });*/
			
			//gson.toJson(queue);
			tableRow.addView(box);
			for (int j = 0; j < customer_info.length; j++) {
				
				textView = new TextView(this);
				textView.setLayoutParams(new
						 LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				textView.setTextColor(0xFF000000);
				textView.setText(customer_info[i][j]);
				textView.setPadding(1, 1, 1, 1);
				
				tableRow.addView(textView);
			}
			table_layout.addView(tableRow);
			i++;
		}

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for (int i = 0, j = table_layout.getChildCount(); i < j; i++) {
					TableRow row = (TableRow) table_layout.getChildAt(i);

					CheckBox checkBox = (CheckBox) row.getChildAt(0);
					if (checkBox.isChecked()) {
						int customerIndex = checkBox.getId();

						Customer customer = queue.customer.get(customerIndex);
						String phoneNo = customer.phone.phoneNo;
						String content = customer.phone.message.content;

						if (phoneNo.length() > 0 && content.length() > 0) {
							sendSMS(phoneNo, content);
							Toast.makeText(getBaseContext(), "Send message to " + phoneNo, Toast.LENGTH_LONG).show();;
						} else
							Toast.makeText(
									getBaseContext(),
									"Please enter both phone number and message.",
									Toast.LENGTH_SHORT).show();
					}
					
				}
			}
		});
	}

	// ---sends a SMS message to another device---
	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		Intent tempIntent = new Intent(DELIVERED);
		tempIntent.putExtra("phoneNo", phoneNumber);
		tempIntent.putExtra("content", message);
		
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				tempIntent, 0);

	
		// ---when the SMS has been sent---
		BroadcastReceiver sentBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
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
		};
		
		registerReceiver(sentBroadcastReceiver, new IntentFilter(SENT));
		unregisterReceiver(sentBroadcastReceiver);

		// ---when the SMS has been delivered---
		BroadcastReceiver deliveredBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				Toast.makeText(getBaseContext(), "Delivery report",
						Toast.LENGTH_SHORT).show();
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					String phoneNo = intent.getCharSequenceExtra("phoneNo").toString();
					String content = intent.getCharSequenceExtra("content").toString();
					for (Customer customer : queue.customer) {
						if(customer.phone.phoneNo == phoneNo && customer.phone.message.content == content){
							customer.phone.message.statusText = "DELIVERED";
							Toast.makeText(getApplicationContext(), "SMS delivered to this " + phoneNo, Toast.LENGTH_LONG).show();
						}
					}
					
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		registerReceiver(deliveredBroadcastReceiver, new IntentFilter(DELIVERED));
		unregisterReceiver(deliveredBroadcastReceiver);

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, message, sentPI,
				deliveredPI);

	}

	//monitor phone call activities
		private class PhoneCallListener extends PhoneStateListener {
	 
			private boolean isPhoneCalling = false;
	 
			String LOG_TAG = "LOGGING 123";
	 
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
	 
				if (TelephonyManager.CALL_STATE_RINGING == state) {
					// phone ringing
					Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
				}
	 
				if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
					// active
					Log.i(LOG_TAG, "OFFHOOK");
	 
					isPhoneCalling = true;
				}
	 
				if (TelephonyManager.CALL_STATE_IDLE == state) {
					// run when class initial and phone call ended, 
					// need detect flag from CALL_STATE_OFFHOOK
					Log.i(LOG_TAG, "IDLE");
	 
					if (isPhoneCalling) {
	 
						Log.i(LOG_TAG, "restart app");
	 
						// restart app
						Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
								getBaseContext().getPackageName());
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
	 
						isPhoneCalling = false;
					}
	 
				}
			}
		}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
