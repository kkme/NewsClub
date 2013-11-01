/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.newsclub.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * Class containing some static utility methods.
 */
public class Utils {
	private static final String TAG = "Utils";
	public static final int IO_BUFFER_SIZE = 8 * 1024;

	private Utils() {
	};

	/**
	 * Get the size in bytes of a bitmap.
	 * @param bitmap
	 * @return size in bytes
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		//		if (Build.VERSION.SDK_INT >= Constant.HONEYCOMB_MR1) {//Android 3.1.
		//			return bitmap.getByteCount();
		//		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Get the memory class of this device (approx. per-app memory limit)
	 *
	 * @param context
	 * @return
	 */
	public static int getMemoryClass(Context context) {
		return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

	public static boolean isCmwap(Context context) {
		boolean result = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && "MOBILE".equalsIgnoreCase(info.getTypeName())) {
			Log.d(TAG, "network type:" + info.getExtraInfo());
			if (info.getExtraInfo() != null) {
				if ((info.getExtraInfo().contains("cmwap") || info.getExtraInfo().contains("CMWAP"))) {
					return true;
				}
			}
		}
		return result;
	}

	public static boolean isMobile(Context context) {
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.getTypeName().equalsIgnoreCase("MOBILE")) {
			Log.d(TAG, "network type:" + info.getExtraInfo());
			return true;
		}
		return false;
	}

	public static String getWapUrl(String url) {
		if (url == null || url.equals(""))
			return null;

		url = url.replaceFirst("http://", "");
		url = url.substring(url.indexOf("/"), url.length());
		return "http://10.0.0.172" + url;
	}

	public static String getHost(String url) {
		if (url == null || url.equals(""))
			return null;
		url = url.replaceFirst("http://", "");
		return url.substring(0, url.indexOf("/"));
	}

}
