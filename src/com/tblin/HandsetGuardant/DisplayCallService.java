package com.tblin.HandsetGuardant;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;


public class DisplayCallService extends Service {
	 private static String TAG="DisplayCallServiec";
		private WindowManager.LayoutParams    wmParams=null;
		private  WindowManager  wm=null;
        private  View view;
        private float mTouchStartX;
        private float mTouchStartY;
        private float x;
        private float y;
        private float a;
        private float b;
        private boolean isNetwork=true;
        private TextView tv;
        private BroadcastReceiver receiver,connectionReceiver;
       
        private  Handler handler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			 Logger.i(TAG, "ishow="+view.isShown());
    				if(view.isShown()){
    				wm.removeView(view);
    				}
    			
    		}
        };
       
       
        @Override
        public void onCreate() {

	         	 super.onCreate();
        }
       
       
        @Override
        public void onStart( Intent intent, int startId) {
        	super.onStart(intent, startId);
        	Logger.i(TAG, "onstart()");
        	 TelephonyManager tel=(TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
               tel.listen(new Mlistener(), PhoneStateListener.LISTEN_CALL_STATE);
              


           	
	       
	            view = LayoutInflater.from(this).inflate(R.layout.displaycall, null);
	         	tv=(TextView) view.findViewById(R.id.display_name);
	         	 
	         	receiver =new BroadcastReceiver() {
					
					@Override
					public void onReceive(Context context, Intent intent) {
						Logger.i(TAG, "收到广播:");
						String num=	intent.getStringExtra("call");
						
						SharedPreferences date = context.getSharedPreferences(
								"addrees", 0);
						boolean k=date.getBoolean("add", false);
						Logger.i(TAG, "判断是否显示:="+k);
						if( k && DisplayCallUitl.checkIsPhoneDbExist()){

							goToView(num);
						}
							
						
							}
						}; 	
		        	IntentFilter filter = new IntentFilter();
		    		filter.addAction("com.tblin.firewall.out.call");
		    		registerReceiver(receiver, filter);
		    		
//		    		
//		    		connectionReceiver = new BroadcastReceiver() {
//		    			   
//		    			   @Override
//		    			   public void onReceive(Context context, Intent intent) {
//		    				   Log.i("king", "change：" );
//		    			    ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//		    			    NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//		    			 //   NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//		    			    NetworkInfo    info = connectMgr.getActiveNetworkInfo();  
//		    			    Log.i("king", "info=="+info.toString() );
//		    			    
//		                    if(info != null && info.getType()==ConnectivityManager.TYPE_MOBILE) {
//		                        String name = info.getTypeName();
//		                        Log.i("king", "当前网络名称：" + name);
//		                        Log.i("king", "connect");
//		    					isNetwork = true;
//		                        
//		                    } else {
//		                    	Log.i("king", "unconnect....");
//		    					isNetwork = false;
//		                        Log.i("king", "没有可用网络");
//		                    }
//
//
////				if (mobNetInfo.isConnected()) {
////					Log.i("king", "connect");
////					isNetwork = true;
////				} else {
////					Log.i("king", "unconnect....");
////					isNetwork = false;
////
////				}
//			}
//		    			  };
//
//
//		    		IntentFilter intentFilter = new IntentFilter();
//		    		  intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//		    		  registerReceiver(connectionReceiver, intentFilter);
// 
//		    		
//		    		Logger.i(TAG, "已经注册广播");
        	 
        }
        
		
        private void goToView(String temp) {
					Logger.i(TAG, "来电号码="+temp);
					   //    String num=DisplayCallUitl.proNumber(temp); //V3.3版本修改
					 
					  //      String str= getInCommingNUm(num);//V3.3版本修改
					       String str= getInCommingNUm(temp);    
					    
					   Logger.i(TAG, "显示结果="+str);
					   	tv.setText(str);
		        	     createView(view);
					    Logger.i(TAG, "显示窗体");   
				}
        private void createView(final View vv) {
        	
        	
                // 获取WindowManager
      	  wm = (WindowManager) getApplicationContext().getSystemService("window");
               
                // 设置LayoutParams(全局变量）相关参数
                  wmParams =  new WindowManager.LayoutParams();
                  
            //   wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
                 wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
          //      wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
                wmParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL  
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
               wmParams.gravity=Gravity.CENTER;
                // 以屏幕中间为原点，设置x、y初始值
                Location ll=getLoca();
                if(ll.getX()==0){
                	wmParams.x=0;
                	wmParams.y=-100;
                }else{
                wmParams.x = ll.getX();
                wmParams.y = ll.getY();
                }
                // 设置悬浮窗口长宽数据
                wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.format=PixelFormat.RGBA_8888;
         //      if(vv.getParent()==null)
                wm.addView(vv, wmParams);
                vv.setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                        	
                        	DisplayMetrics dm = new DisplayMetrics();
                        	wm.getDefaultDisplay().getMetrics(dm);
                        	a=dm.widthPixels/2;
                        	b=dm.heightPixels/2;
                                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                                x = event.getRawX()-a;
                                // 25是系统状态栏的高度,也可以通过方法得到准确的值，自己微调就是了
                                y = event.getRawY()-25-b ; 
                                switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                        // 获取相对View的坐标，即以此View左上角为原点
                                        mTouchStartX = event.getX();
                                        mTouchStartY = event.getY()+vv.getHeight()/2;
                                        break;
                                case MotionEvent.ACTION_MOVE:
                                        updateViewPosition(wmParams,wm);
                                        break;
                                case MotionEvent.ACTION_UP:
                                        updateViewPosition(wmParams,wm);
                                        mTouchStartX = mTouchStartY = 0;
                                        Location ll=new Location();
                                        ll.setX(wmParams.x);
                                        ll.setY(wmParams.y);
                                        saveLoca(ll);
                                       
                                        break;
                                }
                                return true;
                        }

                });
                Logger.i(TAG, "create view ok");
        }
        
        private void updateViewPosition( WindowManager.LayoutParams wmParams,WindowManager wm) {
                // 更新浮动窗口位置参数
                wmParams.x = (int) (x - mTouchStartX+100);
                wmParams.y = (int) (y - mTouchStartY+100);
                wm.updateViewLayout(view, wmParams);
        }

        @Override
        public IBinder onBind(Intent intent) {
                return null;
        }
        
        public static String getInCommingNUm(String number){
        	String ss=null;
        	
        		
        		ss=	DisplayCallUitl.queryInfos(number);
        	
        	return ss;
        }
        class Mlistener extends PhoneStateListener{
        	@Override
        	public void onCallStateChanged(final int state, final String incomingNumber) {
        		
        		Log.i("king", "收到状态:state="+state);
        		if(state==0){
        		Message mes=new Message();
        		
        		handler.sendMessage(mes);
        		}
        	super.onCallStateChanged(state, incomingNumber);
        	}

        	
        }
        @Override
        public void onDestroy() {
//        	 if (connectionReceiver != null) {
//        		   unregisterReceiver(connectionReceiver);
//        		  }

        	super.onDestroy();
        }
        private void saveLoca(Location loc){
        	Editor date = getSharedPreferences("xy", 0).edit();
        	date.putInt("x", loc.getX());
        	date.putInt("y", loc.getY());
        	date.commit();
        }
        private Location getLoca(){
        	SharedPreferences date = getSharedPreferences("xy", 0);
        	int x=date.getInt("x", 0);
        	int y=date.getInt("y", 0);
        	 Location ll=new Location();
        	 ll.setX(x);
        	 ll.setY(y);
        	 return ll;
        }
        class Location{
        	int x;
        	public int getX() {
				return x;
			}
			public void setX(int x) {
				this.x = x;
			}
			public int getY() {
				return y;
			}
			public void setY(int y) {
				this.y = y;
			}
			int y;
        }
       
}






