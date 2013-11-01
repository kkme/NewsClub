package com.example.newsclub.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class SecureUtils {

	private static final String TAG = "SecureUtils";

	public static String getSignature(String url) {
		//参数键值对列表
		Map<String, String> paraMap = new HashMap<String, String>();
		//获取url参数列表
		String[] params = url.substring(url.indexOf("?") + 1).split("&");

		//获取url的host
		String urlNoParams = url.substring(0, url.indexOf("?"));
		//获取url的方法模块
		String method = urlNoParams.substring(urlNoParams.lastIndexOf("/") + 1).replace(".do", "");
		Log.d(TAG, "method =" + method);

		//获取参数名列表
		String[] paramsNameArray = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			String[] tmpArray = params[i].split("=");
			if (tmpArray.length > 1)
				paraMap.put(tmpArray[0], tmpArray[1]);
			else
				paraMap.put(tmpArray[0], "");
			paramsNameArray[i] = tmpArray[0];
			if (tmpArray.length > 1)
				Log.d(TAG, "params map:params[" + i + "] " + tmpArray[0] + "=" + tmpArray[1]);
			else
				Log.d(TAG, "params map:params[" + i + "] " + tmpArray[0] + "=");
		}
		//按字典参数名排序
		String[] aimStr = sortByPOPO(paramsNameArray);
		//用于计算签名的字符串
		StringBuffer sb = new StringBuffer();
		//urlencode后的参数字符串
		StringBuffer encodeArgs = new StringBuffer();
		for (int j = 0; j < aimStr.length; j++) {
			try {
				if (j == 0) {
					sb.append(aimStr[j].trim() + "=" + URLDecoder.decode(paraMap.get(aimStr[j].trim()), "UTF-8"));
					encodeArgs.append(aimStr[j].trim() + "=" + paraMap.get(aimStr[j].trim()));
				} else {
					sb.append("&" + aimStr[j].trim() + "=" + URLDecoder.decode(paraMap.get(aimStr[j].trim()), "UTF-8"));
					encodeArgs.append("&" + aimStr[j].trim() + "=" + paraMap.get(aimStr[j].trim()));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		String needToMd5 = sb.toString() + "b247f859d0f1474a6bad80b008cd80de";
		Log.d(TAG, "=needToMd5== " + needToMd5);
		String md5Str = MD5(needToMd5);
		Log.d(TAG, "===Md5Result md5== " + md5Str);
		Log.d(TAG, "===Md5Result url== " + urlNoParams + "?" + encodeArgs + "&signature=" + md5Str);
		return urlNoParams + "?" + encodeArgs + "&signature=" + md5Str;
	}

	private static String[] sortByPOPO(String[] args) {
		String tmp;
		for (int i = 0; i < args.length; i++) {
			for (int j = i + 1; j < args.length; j++) {
				if (args[i].compareTo(args[j]) > 0) {
					tmp = args[i];
					args[i] = args[j];
					args[j] = tmp;
				}
			}
		}
		String str = Arrays.toString(args);
		return str.substring(1, str.length() - 1).split(",");
	}

	private static int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		int MASK = 0xFFDF;
		int length1 = o1.length();
		int length2 = o2.length();
		int length = length1 > length2 ? length2 : length1;
		int c1, c2;
		int d1, d2;
		for (int i = 0; i < length; i++) {
			c1 = o1.charAt(i);
			c2 = o2.charAt(i);
			d1 = c1 & MASK;
			d2 = c2 & MASK;
			if (d1 > d2) {
				return 1;
			} else if (d1 < d2) {
				return -1;
			} else {
				if (c1 > c2) {
					return 1;
				} else if (c1 < c2) {
					return -1;
				}
			}
		}
		if (length1 > length2) {
			return 1;
		} else if (length1 < length2) {
			return -1;
		}
		return 0;
	}

	/**
	 * 方法: MD5
	 * 描述: 计算MD5摘要
	 * 参数: @param s
	 * 参数: @return
	 * 返回: String
	 * 异常
	 * 作者: King zhaokang@1000chi.com
	 * 日期: 2012-5-18
	 */
	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
