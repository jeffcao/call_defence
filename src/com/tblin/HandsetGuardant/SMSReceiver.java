package com.tblin.HandsetGuardant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.tblin.HandsetGuardant.core.Processer;
import com.tblin.HandsetGuardant.core.ProcesserManager;

public class SMSReceiver extends BroadcastReceiver {
	private static final String TAG = SMSReceiver.class.toString();
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
    	
       Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        Processer processer = ProcesserManager.getInstance().getSmsProcesser();
       
        for (int n = 0; n < messages.length; n++) {
        	int firemode = SettingPreferenceHandler.getInstance().getSmsFireMode();
			Logger.d(TAG, "sms fire mode is " + firemode);
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            String number=smsMessage[n].getOriginatingAddress();
            String content = smsMessage[n].getMessageBody();
            Logger.d(TAG, "接收到短信\n" + number + "\n" + content);
            processer.processIncomingSms(number, context, "(" + number
					+ ")短信已被拦截", content, this);
           
        }
    }
    
  

}
