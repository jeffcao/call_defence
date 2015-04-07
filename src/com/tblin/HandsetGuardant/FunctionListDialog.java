package com.tblin.HandsetGuardant;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FunctionListDialog extends CommonDialog {

	public static abstract class Function {

		public abstract String myName();

		@Override
		public String toString() {
			return myName();
		}

		public abstract void onClick();

	}

	public FunctionListDialog(Context context, List<Function> functions) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.effect_options, null);
		ListView list = (ListView) view.findViewById(R.id.effect_options);
		final ListAdapter adapter = new ArrayAdapter<Function>(context,
				R.layout.function_item, functions);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FunctionListDialog.this.dismiss();
				Function e = (Function) (adapter.getItem(position));
				e.onClick();
			}
		});
		setView(view);
	}

}
