package com.tblin.HandsetGuardant.phoneattribute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tblin.HandsetGuardant.Logger;

import android.content.Context;

public class PhoneDataDownloader {

	private static PhoneDataDownloader INSTANCE;
	private Context mContext;
	private String fileSavePath;
	private DownloadThread downloader;
	private static final String TAG = "PhoneDataDownloader";

	private PhoneDataDownloader() {
		lsnrs = new ArrayList<DownloadListener>();
		downloader = new DownloadThread();
	}

	public static PhoneDataDownloader getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new PhoneDataDownloader();
		}
		return INSTANCE;
	}

	public void init(Context context, String fileSavePath) {
		if (null != this.mContext) {
			Logger.w(TAG, "don't init again");
			return;
		}
		this.mContext = context;
		this.fileSavePath = fileSavePath.replace(".db", ".gzip");
	}

	public boolean hasInited() {
		return null != this.mContext;
	}
	
	public boolean isDownloading() {
		return downloader.isRunning();
	}

	public void startDownload() {
		Logger.i(TAG,"start download");
		if (downloader.isRunning()) {
			// do nothing
			Logger.i(TAG,"download thread is running, return");
			return;
		} else if (downloader.isDead()) {
			downloader = new DownloadThread();
		}
		downloader.start();
	}

	public void stopDownload() {
		downloader.stopThread();
	}

	public int getDownloadPercent() {
		if (downloader.isRunning()) {
			return downloader.getDownloadPercent();
		} else {
			return 0;
		}
	}

	private void onDownloadStart() {
		if (lsnrs.isEmpty()) {
			return;
		}
		for (DownloadListener lsnr : lsnrs) {
			lsnr.onStart();
		}
	}

	private void onDowloadError(ErrorType err) {
		Logger.i(TAG,"on download error " + err);
		if (lsnrs.isEmpty()) {
			return;
		}
		for (DownloadListener lsnr : lsnrs) {
			lsnr.onFail(err);
		}
	}

	private void onDownloadSuccess() {
		if (lsnrs.isEmpty()) {
			return;
		}
		for (DownloadListener lsnr : lsnrs) {
			lsnr.onSuccess();
		}
	}

	private void onDownloading(long total, long downloaded) {
		if (lsnrs.isEmpty()) {
			return;
		}
		for (DownloadListener lsnr : lsnrs) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(DownloadListener.TOTAL_SIZE_TAG, total);
			map.put(DownloadListener.DOWNLOAD_SIZE_TAG, downloaded);
			lsnr.onDowloading(map);
		}
	}

	private class DownloadThread extends Thread {

		public static final int STATUS_INITAIL = 1;
		public static final int STATUS_RUNNING = 2;
		public static final int STATUS_STOPED = 3;
		private int runningFlag = STATUS_INITAIL;
		private long fileSize = 0;
		private long downloaded = 0;

		@Override
		public void run() {
			runningFlag = STATUS_RUNNING;
			Logger.i(TAG,"download thread run1");
			if (preDownloadCheck()) {
				InputStream is = null;
				OutputStream os = null;
				try {
					URL url = new URL(
							"http://www.tblin.com/apk_dyn/phone_original.db.gz");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(5000);
					connection.setRequestMethod("GET");
					connection.connect();
					fileSize = connection.getContentLength();
					boolean isSdSpaceAvailable = PhoneStateUtil
							.isSpaceAvailableByte(fileSize);
					Logger.i(TAG,"save path is " + fileSavePath);
					Logger.i(TAG,"file size is " + fileSize);
					Logger.i(TAG,"download thread run2");
					if (!isSdSpaceAvailable) {
						onDowloadError(ErrorType.SDCARD_SPACE_UNAVAILABLE);
					} else {
						File file = new File(fileSavePath);
						if (!file.exists()) {
							//file.getParentFile().getParentFile().mkdirs();
							file.getParentFile().mkdirs();
							file.createNewFile();
						} else {
							file.createNewFile();
						}
						is = connection.getInputStream();
						os = new FileOutputStream(file);
						onDownloadStart();
						byte[] buffer = new byte[4 * 1024];
						downloaded = 0;
						int read = 0;
						while ((read = is.read(buffer)) != -1
								&& (runningFlag == STATUS_RUNNING)) {
							downloaded += read;
							os.write(buffer, 0, read);
							onDownloading(fileSize, downloaded);
							Logger.i(TAG,"on downloading " + downloaded
									+ "/" + fileSize);
						}
						os.flush();
						Logger.i(TAG,"download thread run3");
						if (runningFlag == STATUS_RUNNING) {
							// 下载成功，开始解压文件夹
							os.close();
							Logger.i(TAG,"file path is "
									+ file.getParent());
							String dest = file.getParent() + "/phone.db";
							GZipUtil.unpackageZip(file, dest);
							File fileOkTag = new File(dest + "ok");
							fileOkTag.createNewFile();
							onDownloadSuccess();
						}
						Logger.i(TAG,"download thread run4");
					}

				} catch (IOException e) {
					onDownloadingError();
					e.printStackTrace();
				} finally {
					if (null != is) {
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (null != os) {
						try {
							os.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			runningFlag = STATUS_STOPED;
		}

		private boolean preDownloadCheck() {
			boolean isNetAvailable = PhoneStateUtil.isNetConnected(mContext);
			if (!isNetAvailable) {
				onDowloadError(ErrorType.NO_NET);
				return false;
			}
			boolean isSdAvailable = PhoneStateUtil.isSdCardAvailable();
			if (!isSdAvailable) {
				onDowloadError(ErrorType.NO_SDCARD);
				return false;
			}

			return true;
		}

		private void onDownloadingError() {
			boolean isNetAvailable = PhoneStateUtil.isNetConnected(mContext);
			if (!isNetAvailable) {
				onDowloadError(ErrorType.NET_INTERRUPT);
				return;
			}
			boolean isSdAvailable = PhoneStateUtil.isSdCardAvailable();
			if (!isSdAvailable) {
				onDowloadError(ErrorType.NO_SDCARD);
				return;
			}
			boolean isSdSpaceAvailable = PhoneStateUtil
					.isSpaceAvailableByte(10 * 1024);
			if (!isSdSpaceAvailable) {
				onDowloadError(ErrorType.SDCARD_SPACE_UNAVAILABLE);
				return;
			}
			onDowloadError(ErrorType.OTHER);
		}

		public boolean isDead() {
			return runningFlag == STATUS_STOPED;
		}

		public boolean isRunning() {
			return runningFlag == STATUS_RUNNING;
		}

		public void stopThread() {
			if (isRunning()) {
				runningFlag = STATUS_STOPED;
			}
		}

		public int getDownloadPercent() {
			if (!isRunning()) {
				return -1;
			} else if (fileSize == 0) {
				return -1;
			} else {
				float p = (float) downloaded / (float) fileSize;
				return (int) (100 * p);
			}
		}
	}

	private List<DownloadListener> lsnrs;

	public void registListener(DownloadListener lsnr) {
		if (null != lsnr && !lsnrs.contains(lsnr)) {
			lsnrs.add(lsnr);
		}
	}

	public void unregistListener(DownloadListener lsnr) {
		lsnrs.remove(lsnr);
	}

	public interface DownloadListener {
		public static final String TOTAL_SIZE_TAG = "total_size";
		public static final String DOWNLOAD_SIZE_TAG = "downloaded_size";

		public void onStart();

		public void onDowloading(Map<String, Object> data);

		public void onSuccess();

		public void onFail(ErrorType err);
	}

	public static abstract class PercentDownloadListener implements
			DownloadListener {
		@Override
		public final void onDowloading(Map<String, Object> data) {
			long total = (Long) data.get(TOTAL_SIZE_TAG);
			long download = (Long) data.get(DOWNLOAD_SIZE_TAG);
			int percent = 0;
			if (total <= 0) {
				// do nothing
			} else {
				Logger.i(TAG,"download " + download + "/" + total);
				float p = (float) download / (float) total;
				Logger.i(TAG,"p is " + p);
				percent = (int) (100 * p);
			}
			onDowloadingPercent(percent);
		}

		public abstract void onDowloadingPercent(int percent);
	}

	public abstract class SizeDownloadListener implements DownloadListener {
		@Override
		public final void onDowloading(Map<String, Object> data) {
			long total = (Long) data.get(TOTAL_SIZE_TAG);
			long download = (Long) data.get(DOWNLOAD_SIZE_TAG);
			String totalStr = getSizeString(total);
			String downloadStr = getSizeString(download);
			String sizeStr = downloadStr + "/" + totalStr;
			onDownloadingSizeSize(total, download);
			onDownloadingSizeStr(sizeStr);
		}

		// override it if you want use this
		public void onDownloadingSizeStr(String str) {
			// do nothing
		}

		// override it if you want use this
		public void onDownloadingSizeSize(long total, long downloaded) {
			// do nothing
		}

		private String getSizeString(long bytes) {
			long KB = 1024;
			long MB = 1024 * 1024;
			if (bytes < KB) {
				return bytes + "Bytes";
			} else if (bytes < MB) {
				return bytes / KB + "KB";
			}
			return bytes / MB + "MB";
		}
	}

	public enum ErrorType {
		NO_SDCARD, NO_NET, SDCARD_SPACE_UNAVAILABLE, NET_INTERRUPT, OTHER
	};

}
