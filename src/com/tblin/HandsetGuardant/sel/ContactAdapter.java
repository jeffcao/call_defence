package com.tblin.HandsetGuardant.sel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tblin.HandsetGuardant.BlackUser;
import com.tblin.HandsetGuardant.R;

public class ContactAdapter extends BaseAdapter{
	
	private List<Contacter> contacters;
	private Context mContext;
	private Map<String, Contacter> sels;

	public ContactAdapter(List<Contacter> contacters, Context mContext) {
		super();
		this.contacters = contacters;
		this.mContext = mContext;
		this.sels = new HashMap<String, Contacter>();
	}

	@Override
	public int getCount() {
		return contacters.size();
	}

	@Override
	public Object getItem(int arg0) {
		return contacters.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(mContext).inflate(
                    R.layout.contact_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.contact_name = (TextView) rowView.findViewById(R.id.contact_name);
            viewHolder.number = (TextView) rowView
                    .findViewById(R.id.contact_number);
            viewHolder.check_box = (CheckBox) rowView.findViewById(R.id.contact_check_box);
            rowView.setTag(viewHolder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final Contacter item = this.contacters.get(position);
        Object checked = this.sels.get(item.mobile);
        holder.check_box.setChecked(null != checked);
        holder.check_box.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onCheckBoxChange(item, v);
			}
		});
        rowView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				holder.check_box.setChecked(!holder.check_box.isChecked());
				onCheckBoxChange(item, holder.check_box);
			}
		});
        String name = item.name.equals("") ? BlackUser.UNKNOWNAME : item.name;
        holder.contact_name.setText(name);
        holder.number.setText(item.mobile);
        return rowView;
    }
	
	private void onCheckBoxChange(Contacter item, View v) {
		if (((CheckBox)v).isChecked()) {
			sels.put(item.mobile, item);
		} else {
			sels.remove(item.mobile);
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		this.sels.clear();
		super.notifyDataSetChanged();
	}
	
	private static class ViewHolder {
        public TextView contact_name;
        public TextView number;
        public CheckBox check_box;
    }
	
	
	public List<Contacter> getSels() {
		List<Contacter> sels = new ArrayList<Contacter>(this.sels.values());
		return sels;
	}

}
