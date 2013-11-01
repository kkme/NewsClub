/**
 * 工程: Game100
 * 标题: ApiInfo.java
 * 包: com.appcate.game.basic
 * 描述: TODO
 * 作者: King zhaokang@1000chi.com
 * 日期: 2012-6-20 下午6:27:16
 * 版权: 2012 www.1000chi.com Inc. All rights reserved.
 * 版本: V1.0
 */

package com.example.newsclub.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.example.newsclub.utils.AndroidUtils;
import com.example.newsclub.utils.SecureUtils;

import android.content.Context;
import android.util.Log;

/**
 * 类: ApiInfo 描述: TODO 作者: King zhaokang@1000chi.com 日期: 2012-6-20
 */
public class ApiInfo {
	private static final String TAG = "ApiInfo";

	public static final int REQ_GET = 0;
	public static final int REQ_POST = 1;
	public static final int REQ_PUT = 2;

	private String method;
	private String host;
	private int requstType;
	private String[] paramNames;
	private Map<String, Object> params;

	private String filePath;

	public ApiInfo(String host, String method, String[] paramNames) {
		this.host = host;
		this.method = method;
		this.paramNames = paramNames;
		this.params = new HashMap<String, Object>();
	}

	public ApiInfo(String host, String method, String[] paramNames, int requestType) {
		this.host = host;
		this.method = method;
		this.paramNames = paramNames;
		this.requstType = requestType;
		this.params = new HashMap<String, Object>();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getRequstType() {
		return requstType;
	}

	public void setRequstType(int requstType) {
		this.requstType = requstType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public Map<String, Object> getParams() {
		if (params == null)
			params = new HashMap<String, Object>();
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public void setParam(String key, String v) {
		this.params.put(key, v);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSignatruedUrl(Context context) {
		StringBuffer url = new StringBuffer();
		url.append(host);
		url.append(method);
		url.append("?");
		for (String paramName : getParamNames()) {
			Log.d(TAG, "paramName=" + paramName);
			url.append(paramName);
			url.append("=");
			if (getParams().get(paramName) != null) {
				try {
					url.append(URLEncoder.encode(getParams().get(paramName).toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			url.append("&");
		}
		url.append(ApiConfig.URL_POSTFIX);
		url.append(AndroidUtils.getUUID(context));
		url.append(AndroidUtils.getVerCodeParam(context));
		return SecureUtils.getSignature(url.toString());
	}

	public String getPlainUrl() {
		StringBuffer url = new StringBuffer();
		url.append(host);
		url.append(method);
		url.append("?");
		for (String paramName : getParamNames()) {
			Log.d(TAG, "paramName=" + paramName);
			url.append(paramName);
			url.append("=");
			if (getParams().get(paramName) != null)
				try {
					url.append(URLEncoder.encode(getParams().get(paramName).toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			url.append("&");
		}
		url.deleteCharAt(url.length() - 1);
		return url.toString();
	}
}
