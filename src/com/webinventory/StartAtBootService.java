package com.webinventory;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class StartAtBootService extends Service {
	Handler handler = new Handler();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onCreate()
    {
    	Log.v("StartServiceAtBoot", "StartAtBootService Created");
    }
	
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
	    	getValue();
	    	handler.postDelayed(this, 10000); // 1000 - Milliseconds
	    }
	 };
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
    	Log.d("StartServiceAtBoot", "StartAtBootService -- onStartCommand()");
    	handler.postDelayed(runnable, 10000);
    	Toast.makeText(this, "My delivery sms Service Started", Toast.LENGTH_LONG).show();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
    	super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    	
    }
     
     public void getValue() {
    	 Toast.makeText(this, "i m for sms delivery", Toast.LENGTH_LONG).show();
     }
     
    @Override
    public void onDestroy() 
    {
    	Log.v("StartServiceAtBoot", "StartAtBootService Destroyed");
    }
	

}
