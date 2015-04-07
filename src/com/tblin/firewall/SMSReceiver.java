package com.tblin.firewall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.tblin.firewall.core.Processer;
import com.tblin.firewall.core.ProcesserManager;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
    	
       Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        Processer processer = ProcesserManager.getInstance().getSmsProcesser();
       
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            String number=smsMessage[n].getOriginatingAddress();
            String content = smsMessage[n].getMessageBody();
          
            processer.processIncomingSms(number, context, "(" + number
					+ ")短信已被拦截", content, this);
           
        }
    }
    
  

}
