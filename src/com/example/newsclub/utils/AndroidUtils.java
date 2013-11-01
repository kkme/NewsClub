package com.example.newsclub.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

public class AndroidUtils {
	public static View inflateView(Context context, int resource, ViewGroup root) {
		return View.inflate(context, resource, root);
	}

	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static boolean isEmpty(List<?> list) {
		return list == null || list.size() == 0;
	}


	/**
	 * 上传图片质量的方法
	 * 
	 * @param _b
	 * @param format
	 * @param isPost
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap _b, CompressFormat format, boolean isPost) {
		if (null == _b) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		_b.compress(format, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap drawable2Bitmap(Drawable _d) {
		return null == _d ? null : ((BitmapDrawable) _d).getBitmap();
	}

	public static Bitmap byteArray2Bitmap(byte[] _b) {
		return null == _b ? null : BitmapFactory.decodeByteArray(_b, 0, _b.length);
	}

	public static Drawable resource2Drawable(Activity _activity, int id) {
		return null == _activity ? null : bitmap2Drawable(BitmapFactory.decodeResource(_activity.getResources(), id));
	}

	public static Drawable byteArray2Drawable(byte[] _b) {
		return null == _b ? null : bitmap2Drawable(BitmapFactory.decodeByteArray(_b, 0, _b.length));
	}

	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		if (160 != bitmap.getDensity()) {
			bitmap.setDensity(160);
		}
		return new BitmapDrawable(bitmap);
	}


	public static boolean fileInvoke(Context activity, String path) {
		String mimeType = getMimeType(path);
		System.out.println("file path:" + path + ",mimeType:" + mimeType);
		if (!TextUtils.isEmpty(mimeType)) {
			Intent it = new Intent(Intent.ACTION_VIEW);
			Uri uri = null;
			if (mimeType.startsWith("application/") || mimeType.startsWith("image/")) {
				it.addCategory("android.intent.category.DEFAULT");
				uri = Uri.fromFile(new File(path));
				it.setDataAndType(uri, mimeType);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(it);
				return true;
			} else if (mimeType.startsWith("audio/") || mimeType.startsWith("video/") || mimeType.startsWith("text/")) {
				uri = Uri.parse("file://" + path);
				it.setDataAndType(uri, mimeType);
				activity.startActivity(it);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static String getMimeType(String path) {
		String ext = getFileExtensionFromUrl(path);
		if (TextUtils.isEmpty(ext)) {
			return "";
		}
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
	}

	public static String getFileExtensionFromUrl(String url) {
		if (url != null && url.length() > 0) {
			int query = url.lastIndexOf('?');
			if (query > 0) {
				url = url.substring(0, query);
			}
			int filenamePos = url.lastIndexOf('/');
			String filename = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;

			if (filename.length() > 0) {
				int dotPos = filename.lastIndexOf('.');
				if (0 <= dotPos) {
					return filename.substring(dotPos + 1);
				}
			}
		}

		return "";
	}

	/**
	 * 判断网络是否可用
	 * @return
	 */
	public static boolean isNetworkValidate(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null && cm.getActiveNetworkInfo() != null) {
			return cm.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static void showDialog(final Context c, String title, final String[] datas, DialogInterface.OnClickListener listener) {
		final String[] items;
		if (datas != null) {
			String[] temp = new String[datas.length + 1];
			System.arraycopy(datas, 0, temp, 0, datas.length);
			temp[temp.length - 1] = "取消";
			items = temp;
		} else {
			items = new String[] { "取消" };
		}
		new AlertDialog.Builder(c).setTitle(title).setItems(items, listener).create().show();
	}

	public static int getSampleSize(String path) {
		BitmapFactory.Options bmfoptions = new BitmapFactory.Options();
		bmfoptions.inSampleSize = 1;
		bmfoptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmfoptions);

		int wr = bmfoptions.outWidth / 480;
		int hr = bmfoptions.outHeight / 800;
		if (wr > hr) {
			return hr;
		} else {
			return wr;
		}
	}

	public static BitmapFactory.Options getBitmapOptions(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inSampleSize = getSampleSize(path);
		return options;
	}

	public static int getSelectDataToNow(long data) {
		try {
			Date date2 = Calendar.getInstance().getTime();
			return (int) ((date2.getTime() - data) / 1000 / 60 / 60 / 24);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 调节屏幕亮度
	 */
	public static void adjustScreenBright(Activity context, int screentBrightValue) {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		if (screentBrightValue >= 0f && screentBrightValue <= 255f) {
			float value = screentBrightValue / 255f;
			lp.screenBrightness = value;
		} else {
			lp.screenBrightness = -1.0f;
		}
		context.getWindow().setAttributes(lp);
	}

	public static void adjustScreenBright(Activity context) {
		// adjustScreenBright(context,Constant.screenBrightValue);
	}

	public static DisplayMetrics getMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm;
	}


	public static String GetHostIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return "";
	}

	public static String getVerCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

	/*
	 * 获取客户端版本号
	 * */
	public static String getVerCodeParam(Context context) {
		try {
			return "&version=" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "&version=0";
		}
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei == null ? "" : imei;
	}

	public static String getMac(Context context) {
		String mac = PreferenceManager.getDefaultSharedPreferences(context).getString("macAddress", "");
		if (!TextUtils.isEmpty(mac))
			return mac;
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info != null)
			mac = info.getMacAddress();
		if (mac != null)
			mac = mac.replace(":", "").toUpperCase();
		if (!TextUtils.isEmpty(mac))
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("macAddress", mac).commit();
		return mac;
	}

	public static String getUUID(Context context) {
		String imei = getIMEI(context);
		String macAddress = getMac(context);
		if (TextUtils.isEmpty(macAddress) && TextUtils.isEmpty(imei)) {
			String uuid = PreferenceManager.getDefaultSharedPreferences(context).getString("uuid", "");
			if (TextUtils.isEmpty(uuid)) {
				uuid = UUID.randomUUID().toString();
				PreferenceManager.getDefaultSharedPreferences(context).edit().putString("uuid", uuid).commit();
			}
			return "&macaddress=" + uuid + "&device_id=" + uuid;
		} else
			return "&macaddress=" + macAddress + "&device_id=" + imei;
	}
}
