package com.tblin.HandsetGuardant.phoneattribute;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class GZipUtil {

	public static void unpackageZip(File source, String dest)
			throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			File destFile = new File(dest);
			destFile.createNewFile();
			is = new GZIPInputStream(new BufferedInputStream(
					new FileInputStream(source)));
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[4 * 1024];
			int read = 0;
			while ((read = is.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.flush();
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

}
