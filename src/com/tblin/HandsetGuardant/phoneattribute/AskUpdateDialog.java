package com.tblin.HandsetGuardant.phoneattribute;

import android.content.Context;

import com.tblin.HandsetGuardant.R;

public class AskUpdateDialog extends AskDownloadDialog{

	public AskUpdateDialog(Context context) {
		super(context);
		String title = context.getString(R.string.ask_update_title);
		String content = context.getString(R.string.ask_update_content);
		dialog.setTitle(title);
		dialog.setMessage(content);
	}

}
