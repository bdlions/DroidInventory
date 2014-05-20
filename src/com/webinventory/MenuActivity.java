package com.webinventory;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams.Versions;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
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
	TableRow tableRow;
	TextView textView;
	TableLayout table_layout;
	
	final Context context = this;
	private Queue queue;
	
	private int qId;
	static int i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		table_layout = (TableLayout) findViewById(R.id.tableLayout1);
		button = (Button) findViewById(R.id.buttonSendSMS);

		// call to server with the queue id
		JSONHandler task = new JSONHandler();
		task.execute(new String[] { "http://192.168.0.102/webinventory/androidrpc/qprovider/" });
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
					final String phoneNo = intent.getCharSequenceExtra("phoneNo").toString();
					final String content = intent.getCharSequenceExtra("content").toString();
					for (final Customer customer : queue.customer) {
						if (customer.phone.phoneNo == phoneNo && customer.phone.message.content == content) {
							runOnUiThread(new Runnable()
			        		{

								@Override
								public void run() {
									customer.phone.message.statusText = "DELIVERED";
									Toast.makeText(getApplicationContext(),
											"SMS delivered to this " + phoneNo,
											Toast.LENGTH_LONG).show();
								}
								
			        		});
							
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
		registerReceiver(deliveredBroadcastReceiver,
				new IntentFilter(DELIVERED));
		unregisterReceiver(deliveredBroadcastReceiver);

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, message, sentPI,
				deliveredPI);

	}

	private class JSONHandler extends AsyncTask<String, Void, String> {
		String queue_list;

		@Override
		protected String doInBackground(String... urls) {
			for (String url : urls) {
				JSONRPCClient client = JSONRPCClient.create(url,
						Versions.VERSION_2);
				client.setConnectionTimeout(2000);
				client.setSoTimeout(2000);

				try {
					String queue_id = getIntent().getStringExtra("QUQUE_ID");
					qId = Integer.parseInt(queue_id);
					System.out.println("Q ID: " + qId);
					//Toast.makeText(MenuActivity.this,"got queue id " + qId, Toast.LENGTH_LONG).show();
					queue_list = client.callString("getQ", qId);
					System.out.println(queue_list);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String[][] customer_info = new String[3][3];
							Gson gson = new Gson();
							queue = gson.fromJson(JSONHandler.this.queue_list,
									Queue.class);

							i = 0;
							for (Customer customer : queue.customer) {

								tableRow = new TableRow(MenuActivity.this);
								tableRow.setLayoutParams(new LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT));

								String status = customer.phone.message.statusText;
								String phoneNo = customer.phone.phoneNo;

								customer_info[i][0] = phoneNo;
								customer_info[i][1] = status;

								CheckBox box = new CheckBox(MenuActivity.this);
								box.setId(i);
								box.setChecked(true);

								tableRow.addView(box);
								for (int j = 0; j < customer_info.length; j++) {

									textView = new TextView(MenuActivity.this);
									textView.setLayoutParams(new LayoutParams(
											LayoutParams.WRAP_CONTENT,
											LayoutParams.WRAP_CONTENT));

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
									for (int i = 0, j = table_layout
											.getChildCount(); i < j; i++) {
										TableRow row = (TableRow) table_layout
												.getChildAt(i);

										CheckBox checkBox = (CheckBox) row
												.getChildAt(0);
										if (checkBox.isChecked()) {
											int customerIndex = checkBox.getId();

											Customer customer = queue.customer.get(customerIndex);
											String phoneNo = customer.phone.phoneNo;
											String content = customer.phone.message.content;

											if (phoneNo.length() > 0
													&& content.length() > 0) {
												sendSMS(phoneNo, content);
												Toast.makeText(getBaseContext(),"Send message to "+ phoneNo,Toast.LENGTH_LONG).show();
											} else
												Toast.makeText(
														getBaseContext(),
														"Please enter both phone number and message.",
														Toast.LENGTH_SHORT)
														.show();
										}

									}
								}
							});
						}
					});

				} catch (JSONRPCException e) {
					e.printStackTrace(); // Invalid JSON Response caught here
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
