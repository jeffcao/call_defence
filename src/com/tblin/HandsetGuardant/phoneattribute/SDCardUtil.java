package com.tblin.HandsetGuardant.phoneattribute;

import com.tblin.HandsetGuardant.Logger;

import android.os.Environment;
import android.os.StatFs;

public class SDCardUtil {
	
	private static final String TAG = SDCardUtil.class.getName();

	public static boolean isSdCardAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean isSpaceAvailableByte(long bytes) {
		long available = getAvailableSpace();
		boolean isAvailable = available > bytes;
		return isAvailable;
	}

	public static boolean isSpaceAvailableKB(long kbs) {
		return isSpaceAvailableByte(kbs * 1024);
	}

	public static boolean isSpaceAvailableMB(long mbs) {
		return isSpaceAvailableByte(mbs * 1024 * 1024);
	}

	public static long getAvailableSpace() {
		Logger.i(TAG,"get available space");
		long available = -1;
		if (isSdCardAvailable()) {
			String sdcard = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long avaBlocks = statFs.getAvailableBlocks();
			long totalBlocks = statFs.getBlockCount();
			available = blockSize * avaBlocks;
			long totalSize = blockSize * totalBlocks;
			Logger.i(TAG,"block size: " + blockSize
					+ ", available blocks: " + avaBlocks + " total blocks: "
					+ totalBlocks + ", size: " + available + "/" + totalSize
					+ "----" + (totalSize / 1024 / 1024));
		}
		return available;
	}
}
