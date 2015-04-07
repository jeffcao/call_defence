package com.tblin.firewall;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONObject;

import com.tblin.embedmarket.AppMarketConfig;

public class ConfigManager {

	private static final String CONFIG_PATH = "/mnt/sdcard/firewall/config.txt";
	private static final String KEY_EMB_PHP_URL = "KEY_EMB_PHP_URL";
	private static final String KEY_UPDATE_MODE = "KEY_UPDATE_MODE";

	public static void parseConfig() {
		parseContent(getConfigContent());
	}

	private static void parseContent(String content) {
		if (content == null) {
			return;
		}
		try {
			JSONObject root = new JSONObject(content);
			String embPhpUrl = root.getString(KEY_EMB_PHP_URL);
			String updateMode = root.getString(KEY_UPDATE_MODE);
			AppMarketConfig.PHP_SERVER_URL = embPhpUrl;
			FireWallConfig.UPDATE_MODE = updateMode;
		} catch (Exception e) {
			return;
		}
	}

	private static String getConfigContent() {
		File configFile = new File(CONFIG_PATH);
		if (configFile.exists()) {
			FileInputStream fis = null;
			StringBuffer sb = new StringBuffer();
			try {
				fis = new FileInputStream(configFile);
				byte[] buffer = new byte[1000];
				int length;
				while ((length = fis.read(buffer)) != -1) {
					sb.append(new String(buffer, 0, length));
				}
				return sb.toString();
			} catch (Exception e) {
				return null;
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
					}
			}
		} else {
			return null;
		}
	}
}
