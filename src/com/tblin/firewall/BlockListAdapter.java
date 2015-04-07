package com.tblin.firewall;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.firewall.R;
import com.tblin.firewall.FunctionListDialog.Function;

public class BlockListAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<BlockEvent> blocks;
	private LayoutInflater inflater;
	
	public BlockListAdapter(Context context, List<BlockEvent> blocks) {
		mContext = context;
		this.blocks = blocks;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return blocks.size();
	}

	@Override
	public Object getItem(int position) {
		return blocks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.block_list_item, null);
		BlockEvent event = (BlockEvent)getItem(position);
		final String blockMobile = event.getMobile();
		final String blockName = event.getName();
		final long blockTime = event.getTime();
		TextView name = (TextView) view.findViewById(R.id.block_list_item_name);
		TextView mobile = (TextView) view.findViewById(R.id.block_list_item_mobile);
		TextView time = (TextView) view.findViewById(R.id.block_list_item_time);
		name.setText(blockName);
		mobile.setText(blockMobile);
		time.setText(changeLong2String(blockTime));
		view.setOnClickListener(new OnClickListener() {
			
			@Override
				public void onClick(View v) {
				Function delete = new Function() {
					
					@Override
					public void onClick() {
						YesNoDialog dialog = new YesNoDialog(mContext);
						dialog.setTitle("删除");
						dialog.setMessage("确定删除？");
						dialog.setYesButton(new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								BlockListDBHelper db = BlockListDBHelper.getInstance(mContext);
								boolean result = db.delete(blockName, blockMobile, blockTime,0);
								if (result) {
									Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
								}
							}
						});
						dialog.show();
					}
					
					@Override
					public String myName() {
						return "删除";
					}
				};
				Function sendSms = new Function() {
					
					@Override
					public void onClick() {
						SmsCallHelper.sendSms(blockMobile, mContext);
					}
					
					@Override
					public String myName() {
						return "回复短信";
					}
				};
				Function call = new Function() {
					
					@Override
					public void onClick() {
						SmsCallHelper.call(blockMobile, mContext);
					}
					
					@Override
					public String myName() {
						return "呼叫此号码";
					}
				};
				List<Function> functions = new ArrayList<FunctionListDialog.Function>();
				functions.add(delete);
				functions.add(call);
				functions.add(sendSms);
				CommonDialog dialog = new FunctionListDialog(mContext, functions);
				dialog.setTitle(blockMobile);
				dialog.show();
			}
		});
		return view;
	}

	private String changeLong2String(long time) {
		Time timeFormal = new Time();
		timeFormal.set(time);
		String t = timeFormal.year + "-" + (timeFormal.month + 1)
				+ "-"
				+ timeFormal.monthDay
				+ "  "
				+ (timeFormal.hour < 10 ? "0" + timeFormal.hour
						: timeFormal.hour)
				+ ":"
				+ (timeFormal.minute < 10 ? "0" + timeFormal.minute
						: timeFormal.minute);
		return t;
	}
}
