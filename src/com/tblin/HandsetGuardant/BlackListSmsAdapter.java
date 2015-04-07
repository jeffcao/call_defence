package com.tblin.HandsetGuardant;

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
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.HandsetGuardant.FunctionListDialog.Function;

public class BlackListSmsAdapter extends BaseAdapter {
	private Context context;
	private List<BlockEvent> BlockEvents;
	private LayoutInflater inflater;
	public BlackListSmsAdapter(Context context,List<BlockEvent> BlockEvents){
		this.BlockEvents=BlockEvents;
		this.inflater=LayoutInflater.from(context);
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return BlockEvents.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return BlockEvents.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView=inflater.inflate(R.layout.block_list_sms, null);
		TextView number = (TextView) convertView.findViewById(R.id.block_sms_number);
//		TextView time = (TextView) convertView.findViewById(R.id.block_sms_time);
		TextView content = (TextView) convertView.findViewById(R.id.block_sms_contnet);
		TextView add=(TextView) convertView.findViewById(R.id.block_sms_adress);
		final BlockEvent user=BlockEvents.get(position);
		String ss=user.getMobile();
		if(ss.contains("+86")){
		String k=	ss.substring(3, ss.length());
		number.setText(k);
		}else{
			number.setText(ss);
		}
		String ad=user.getAddress();
		add.setText(ad);
		String time=changeLong2String(user.getTime());
		String content1=user.getContnet();
//		content.setText("["+time+"]"+"    "+content1);
		content.setText(user.getContnet()+"    "+"["+time+"]");
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(context,user);
				
			}
		});
		return convertView;
		
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
	public void showDialog(final Context context,final BlockEvent user){
		List<Function> functions = new ArrayList<FunctionListDialog.Function>();
		FunctionListDialog.Function delete = new FunctionListDialog.Function() {

			@Override
			public void onClick() {
				deleteOnClick(user);
			}

			@Override
			public String myName() {
				return "删除该短信";
			}
		};
		FunctionListDialog.Function recall = new FunctionListDialog.Function() {

			@Override
			public void onClick() {
				
				SmsCallHelper.sendSms(user.getMobile(), context);
			}

			@Override
			public String myName() {
				return "回复短信";
			}
		};
		FunctionListDialog.Function resend = new FunctionListDialog.Function() {

			@Override
			public void onClick() {
				SmsCallHelper.sendSms2You(user.getContnet(), context);
			}

			@Override
			public String myName() {
				return "转发该短信";
			}
		};
		FunctionListDialog.Function call = new FunctionListDialog.Function() {

			@Override
			public void onClick() {
				SmsCallHelper.call(user.getMobile(), context);
			}

			@Override
			public String myName() {
				return "呼叫号码";
			}
		};
		FunctionListDialog.Function inbox = new FunctionListDialog.Function() {

			@Override
			public void onClick() {
				SmsCallHelper.writeInbox(user.getMobile(), user.getContnet(),  context);
				
				BlockListDBHelper db = BlockListDBHelper.getInstance(context);
				boolean result = db.delete(user.getName(),user.getMobile(),user.getTime(),1);
				if (result) {
					
					notifyDataSetChanged();
				
				}
			}	
			@Override
			public String myName() {
				return "恢复到收件箱";
			}
		};
		functions.add(delete);
		functions.add(recall);
		functions.add(resend);
		functions.add(call);
		functions.add(inbox);
		
		CommonDialog dialog = new FunctionListDialog(context,
				functions);
		dialog.setTitle(user.getMobile());
		dialog.show();
	}
	public void deleteOnClick(final BlockEvent user){
		YesNoDialog dialog = new YesNoDialog(context);
		dialog.setTitle("删除");
		dialog.setMessage("确定删除？");
		dialog.setYesButton(new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				BlockListDBHelper db = BlockListDBHelper.getInstance(context);
				boolean result = db.delete(user.getName(),user.getMobile(),user.getTime(),1);
				if (result) {
					Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
					notifyDataSetChanged();
				}
			}
		});
		dialog.show();
	}
}
