package com.tblin.HandsetGuardant;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter{
	
	private List<String[]> contacters;
	private Context mContext;
	private LayoutInflater inflater;
	
	public ContactListAdapter(Context context, List<String[]> contacters) {
		mContext = context;
		this.contacters = contacters;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return contacters.size();
	}

	@Override
	public Object getItem(int position) {
		return contacters.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.contact_list_item, null);
		TextView nameText = (TextView) view.findViewById(R.id.contact_list_item_name);
		TextView mobileText = (TextView) view.findViewById(R.id.contact_list_item_mobile);
		String[] contact = (String[]) getItem(position);
		if (contact.length == 2) {
			String name = contact[0] == null ? contact[1] : contact[0];
			nameText.setText(name);
			mobileText.setText(contact[1]);
		}
		return view;
	}

}
