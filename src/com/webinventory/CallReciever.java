package com.webinventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallReciever extends BroadcastReceiver {
    @Override
    /*public void onReceive(Context context, Intent intent) {

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_RINGING)) {

                // Phone number 
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                // Ringing state
                // This code will execute when the phone has an incoming call
        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_IDLE)
                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                        TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            // This code will execute when the call is answered or disconnected
        }
    	
    	

    }*/
    
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // mProfile = new LoadProfImage(context);

        PhoneListener phoneListener = new PhoneListener(context);
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    class PhoneListener extends PhoneStateListener {
        private Context context;

        public PhoneListener(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
        }

        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {

            case TelephonyManager.CALL_STATE_IDLE:
                //Do your stuff
            	System.out.println("Idle");
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                //Do your stuff
            	System.out.println("Ringing");
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Do your stuff
            	System.out.println("OffHook");
                break;

            }
        }
    }
}