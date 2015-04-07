package com.tblin.HandsetGuardant;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlockListActivity extends Activity {

	private ListView blockList;
	private TextView noBlock;
	private Button clearLog;
//	private ImageView adView;
	private BlockListDBHelper blockListDB;
	private List<BlockEvent> blockEvents;
	private List<BlockEvent> blockSMS;
	private BaseAdapter blockAdapter;
	private ListView blockSmsList;
	private BlackListSmsAdapter adapter;
	private TextView noSms;
	private Button call;
	private Button sms;
	public static BlockListActivity INSTANCE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.block_list_main);
		init();
		INSTANCE = this;
	}

	private void init() {
		clearLog = (Button) findViewById(R.id.block_list_main_clear_log);
		noBlock = (TextView) findViewById(R.id.block_list_main_no_black);
		blockList = (ListView) findViewById(R.id.block_list_main_list_view);
		blockListDB = BlockListDBHelper.getInstance(this);
		blockEvents = blockListDB.getCallRecords();
		blockSMS=blockListDB.getSMSRecords();
		
		noSms=(TextView) findViewById(R.id.block_list_no_sms);
		blockSmsList=(ListView) findViewById(R.id.block_list_main_list_sms);
		call=(Button) findViewById(R.id.block_call_bt);
		sms=(Button) findViewById(R.id.block_sms_bt);
		
		
		call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goCall();
				
			}
		});
		sms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goSms();
				
			}
		});
		BroadcastReceiver blockDataReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				if (blockAdapter != null) {
					List<BlockEvent> events = blockListDB.getCallRecords();
					blockEvents.clear();
					for (BlockEvent e : events) {
						blockEvents.add(e);
					}
					
//					noBlock.setVisibility(View.INVISIBLE);
//					blockList.setVisibility(0);
//					blockSmsList.setVisibility(100);
				}
				goCall();
//				}else{
//					noBlock.setVisibility(View.VISIBLE);
//					blockList.setVisibility(100);
//					blockSmsList.setVisibility(100);
//				} 
//				blockAdapter.notifyDataSetChanged();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(BlockListDBHelper.BLOCK_DATA_CHANGED);
		registerReceiver(blockDataReceiver, filter);
		
		BroadcastReceiver blockSMSReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				if(adapter !=null){
					List<BlockEvent> smses = blockListDB.getSMSRecords();
					blockSMS.clear();
					for (BlockEvent e : smses) {
						blockSMS.add(e);
					}
					goSms();
					}
//					noSms.setVisibility(View.INVISIBLE);
//					blockList.setVisibility(100);
//					blockSmsList.setVisibility(0);
//					
//				}else{
//					noSms.setVisibility(View.VISIBLE);
//					blockSmsList.setVisibility(100);
//					blockList.setVisibility(100);
//				}
//				adapter.notifyDataSetChanged();
			}
		};
		IntentFilter fi = new IntentFilter();
		fi.addAction(BlockListDBHelper.BLOCK_SMS_CHANGED);
		registerReceiver(blockSMSReceiver, fi);

		blockAdapter = new BlockListAdapter(this, blockEvents);
		blockList.setAdapter(blockAdapter);
		
		if(blockEvents.isEmpty()){
			noBlock.setVisibility(View.VISIBLE);
		}
		adapter=new BlackListSmsAdapter(this, blockSMS);
		blockSmsList.setAdapter(adapter);
	
		clearLog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
					
				if(!blockEvents.isEmpty() || !blockSMS.isEmpty()){
					
				YesNoDialog ynd = new YesNoDialog(BlockListActivity.this);
				ynd.setTitle("清除日志");
				ynd.setMessage("是否清除所有拦截日志？");
				ynd.setYesButton(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						blockListDB.clearAll();
						Toast.makeText(BlockListActivity.this, "日志已清除",
								Toast.LENGTH_LONG).show();
						
					}
				});
				ynd.show();
			}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	public  void goCall(){
		call.setBackgroundResource(R.drawable.block_call_bt1);
		sms.setBackgroundResource(R.drawable.block_sms_bt2);
		 
		if (!blockEvents.isEmpty()) {
				blockSmsList.setVisibility(100);
				blockList.setVisibility(0);
				noBlock.setVisibility(100);
				noSms.setVisibility(100);
		}else{
			blockSmsList.setVisibility(100);
			blockList.setVisibility(100);
			noBlock.setVisibility(0);
			noSms.setVisibility(100);
		}
		blockAdapter.notifyDataSetChanged();
		
		
		
	}
	
	public void goSms(){
		call.setBackgroundResource(R.drawable.block_call_bt2);
		sms.setBackgroundResource(R.drawable.block_sms_bt1);
		 
	    
		if(!blockSMS.isEmpty()){
			noBlock.setVisibility(100);
			blockList.setVisibility(100);
			blockSmsList.setVisibility(0);
			noSms.setVisibility(100);
		}else{
			noBlock.setVisibility(100);
			blockList.setVisibility(100);
			blockSmsList.setVisibility(100);
			noSms.setVisibility(0);
		}
		
		adapter.notifyDataSetChanged();
	}
}
