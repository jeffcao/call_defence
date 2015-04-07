package com.tblin.HandsetGuardant.phoneattribute;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.tblin.HandsetGuardant.DisplayCallUitl;
import com.tblin.HandsetGuardant.Logger;
import com.tblin.HandsetGuardant.R;
import com.tblin.HandsetGuardant.ToastUtil;
import com.tblin.HandsetGuardant.YesNoDialog;

public class AskDownloadDialog {
	protected YesNoDialog dialog;
	private static final String TAG = "AskDownloadDialog";
	
	public AskDownloadDialog(final Context context) {
		dialog = new YesNoDialog(context);
		String title = context.getString(R.string.ask_download_title);
		String content = context.getString(R.string.ask_download_content);
		String yes = context.getString(R.string.ask_download_yes);
		String no = context.getString(R.string.ask_download_no);
		dialog.setTitle(title);
		dialog.setMessage(content);
		dialog.setYesButton(no, null, YesNoDialog.AUTO_DISMISS);
		dialog.setNoButton(yes, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PhoneDataDownloader downloader = PhoneDataDownloader.getInstance();
				downloader.init(context, DisplayCallUitl.PHONE_DB_PATH);
				downloader.startDownload();
				new ThreeSecondUI(context).start();
				Logger.i(TAG, "start to download phone.db");
			}
		});
	}
	
	public void show() {
		dialog.show();
	}
	
	public void dismiss() {
		dialog.dismiss();
	}
	
	private static class ThreeSecondUI extends Thread {
		long startTime;
		private static final long THREE_MINUTE = 3 * 1000;
		private static final long INTERVAL = 200;
		private Context mContext;
		
		public ThreeSecondUI(Context context) {
			super();
			mContext = context;
		}
		@Override
		public void run() {
			startTime = System.currentTimeMillis();
			PhoneDataDownloader downloader = PhoneDataDownloader.getInstance();
			while (System.currentTimeMillis() - startTime < THREE_MINUTE && downloader.isDownloading()) {
				int progress = downloader.getDownloadPercent();
				if (progress >= 0) {
					String pgStr = mContext.getString(R.string.progress);
					pgStr = pgStr.replace("progress", Integer.toString(progress));
					ToastUtil.toast(pgStr);
				}
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			super.run();
		}
	}
}
