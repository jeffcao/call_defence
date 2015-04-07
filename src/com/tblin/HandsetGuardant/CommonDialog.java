package com.tblin.HandsetGuardant;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class CommonDialog {
	protected Context mContext;
	protected AlertDialog.Builder builder;
	protected AlertDialog dialog;
	private static final String TAG = CommonDialog.class.toString();
	private View contentView;
	private DialogInterface.OnDismissListener dismissLsnr;
	private DialogInterface.OnCancelListener cancelLsnr;

	public CommonDialog(Context context) {
		mContext = context;
		builder = new Builder(mContext);
		builder.setTitle("温馨提示");
	}

	public void show() {
		dialog = builder.create();
		try {
			Field field = dialog.getClass().getDeclaredField("mAlert");
			field.setAccessible(true);
			Object obj = field.get(dialog);
			field = obj.getClass().getDeclaredField("mHandler");
			field.setAccessible(true);
			field.set(obj, new ButtonHandler(dialog));
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
		if (dismissLsnr != null) {
			dialog.setOnDismissListener(dismissLsnr);
		}
		if (cancelLsnr != null) {
			dialog.setOnCancelListener(cancelLsnr);
		}
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
	}

	public void setOnCancelListener(DialogInterface.OnCancelListener lsnr) {
		cancelLsnr = lsnr;
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener lsnr) {
		dismissLsnr = lsnr;
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public void setTitle(String title) {
		builder.setTitle(title);
	}

	public void setMessage(String message) {
		builder.setMessage(message);
	}

	public void setView(View view) {
		contentView = view;
	}

	private class ButtonHandler extends Handler {
		private WeakReference<DialogInterface> mDialog;

		public ButtonHandler(DialogInterface dialog) {
			mDialog = new WeakReference<DialogInterface>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DialogInterface.BUTTON_POSITIVE:
			case DialogInterface.BUTTON_NEGATIVE:
			case DialogInterface.BUTTON_NEUTRAL:
				((DialogInterface.OnClickListener) msg.obj).onClick(
						mDialog.get(), msg.what);
				break;
			}
		}
	}
}
