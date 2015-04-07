package com.tblin.HandsetGuardant;

import android.content.Context;
import android.content.DialogInterface;

public class YesNoDialog extends CommonDialog {

	public static final int AUTO_DISMISS = 1;
	public static final int MANUAL_DISMISS = 2;

	public YesNoDialog(Context context) {
		super(context);
		setYesButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		setNoButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}

	public void setYesButton(String text,
			DialogInterface.OnClickListener listener) {
		setYesButton(text, listener, AUTO_DISMISS);
	}

	public void setNoButton(String text,
			DialogInterface.OnClickListener listener) {
		final DialogInterface.OnClickListener lsnrFinal = listener;
		DialogInterface.OnClickListener lsnr;
		lsnr = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (lsnrFinal != null) {
					lsnrFinal.onClick(dialog, which);
				}
				dialog.dismiss();
			}
		};
		builder.setNegativeButton(text, lsnr);
	}

	public void setYesButton(DialogInterface.OnClickListener listener) {
		setYesButton("确定", listener);
	}

	public void setNoButton(DialogInterface.OnClickListener listener) {
		setNoButton("取消", listener);
	}

	public void setYesButton(String text,
			DialogInterface.OnClickListener listener, int autoDismiss) {
		final DialogInterface.OnClickListener lsnrFinal = listener;
		DialogInterface.OnClickListener lsnr;
		if (autoDismiss == AUTO_DISMISS) {
			lsnr = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (lsnrFinal != null) {
						lsnrFinal.onClick(dialog, which);
					}
				}
			};
		} else {
			lsnr = lsnrFinal;
		}
		builder.setPositiveButton(text, lsnr);
	}

}
