package com.tblin.firewall;

import com.tblin.firewall.core.Processer;
import com.tblin.firewall.core.ProcesserManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsService extends Service {  
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        // TODO Auto-generated method stub  
        return null;  
    }  
  
    @Override  
    public void onCreate() {  
        IntentFilter filter = new IntentFilter(ACTION);  
        filter.setPriority(2147483647);  
        MyBrocast myService = new MyBrocast();  
        registerReceiver(myService, filter);  
    }  
  
    private class MyBrocast extends BroadcastReceiver {  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
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
}