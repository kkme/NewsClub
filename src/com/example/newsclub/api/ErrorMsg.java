package com.example.newsclub.api;

import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.text.TextUtils;

/**
 * 
 */
public class ErrorMsg extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode;
	private static String defaultMsg = "未知错误";
	private static Properties props;

	// private static Context context;

	private String getErrorMsg(String key) {
		if (TextUtils.isEmpty(key))
			return defaultMsg;
		
		if (props != null && props.containsKey(key)) {
			return props.getProperty(key).replace("\"", "");
		}
		return key;
	}

	@Override
	public String toString() {
		return getErrorMsg(errorCode);
	}

	public ErrorMsg(String errCode) {
		this.errorCode = errCode;
	}

	public static void init(Context ctx) {
		props = new Properties();
		try {
			props.load(ctx.getAssets().open("error_define.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isNetWorkError() {
		if (this.errorCode != null && this.errorCode.startsWith("network")) {
			return true;
		}
		return false;
	}

}
