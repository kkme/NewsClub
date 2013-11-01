package com.example.newsclub.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.example.newsclub.utils.Utils;

public class HttpClientConnector {
	private static final String HTTP_404 = "请求资源未找到!";
	private static final String HTTP_408 = "请求超时!";
	private static final String HTTP_502 = "服务端出现错误!";
	private static final String HTTP_504 = "服务端响应超时!";
	/**
	 * python服务端注册uid使用的信息
	 */

	private static final String TAG = "HttpClientConnector";
	private static HttpClientConnector instance = null;
	private HttpClient client;
	private Context mContext;

	private HttpClientConnector(Context context) {
		mContext = context;
		HttpParams params = new BasicHttpParams();
		// 设置基本参数
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		// HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
		// + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		// 超时设置
		/* 从连接池中取连接的超时时间 */
		ConnManagerParams.setTimeout(params, 15000);
		/* 连接超时 */
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout(params, 10000);

		// 设置我们的HttpClient支持HTTP和HTTPS
		SchemeRegistry schReg = new SchemeRegistry();
		//构建SSLSocketFactory,对于任何形式的https请求都不进行证书验证,可根据需求修改SSLSocketFactoryEx,自定义证书签名验证规则
		SSLSocketFactoryEx sslsf = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sslsf = new SSLSocketFactoryEx(trustStore);
			sslsf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		schReg.register(new Scheme("https", sslsf, 443));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

		client = new DefaultHttpClient(conMgr, params);
	}

	public static HttpClientConnector getInstance(Context context) {
		if (instance == null)
			instance = new HttpClientConnector(context);
		return instance;
	}

	/**
	 * 方法: getDownloadStream
	 * 描述: TODO
	 * 
	 * @param resUrl
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public synchronized InputStream getStream(String resUrl, boolean isPost) throws ClientProtocolException, IOException {
		HttpUriRequest request = null;
		if (isPost)
			request = parseParams(resUrl);
		else
			request = new HttpGet(resUrl);
		if (Utils.isMobile(mContext)) {// 使用移动网络时,延长超时时间,和增加相应的代理设置
			HttpParams param = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(param, 15000);
			HttpConnectionParams.setSoTimeout(param, 10000);
			request.setParams(param);

			if (Utils.isCmwap(mContext)) {
				String wapUrl = Utils.getWapUrl(resUrl);
				String host = Utils.getHost(resUrl);
				((HttpRequestBase) request).setURI(URI.create(wapUrl));
				request.setHeader("X-Online-Host", host);
			}
		}
		if (client != null) {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resEntity = response.getEntity();
				InputStream is = resEntity.getContent();
				return is;
			} else {
				request.abort();
				return null;
			}
		}
		return null;
	}

	public InputStream getStream(String url, byte[] entity) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);
		ByteArrayEntity bae = new ByteArrayEntity(entity);
		bae.setContentType("application/x-www-form-urlencoded; charset=utf-8");
		request.setEntity(bae);
		if (client != null) {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resEntity = response.getEntity();
				InputStream is = resEntity.getContent();
				return is;
			} else {
				request.abort();
				return null;
			}
		}
		return null;
	}

	public byte[] requestByPost(String resUrl, List<NameValuePair> params) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(resUrl);
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return processRequest(httpPost);
	}

	/**
	 * 方法: requestByGet
	 * 描述: TODO
	 * 
	 * @param reqUrl
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @see com.onekchi.apps.networks.IHttpConnector#requestByGet(java.lang.String)
	 */
	public byte[] requestByGet(String reqUrl) {
		HttpGet httpGet = new HttpGet(reqUrl);
		return processRequest(httpGet);
	}

	/**
	 * 
	 * 方法: uploadFile 
	 * 描述: 上传文件,post方法
	 * 参数: @param url 带参数的url地址
	 * 参数: @param filename 文件的参数名
	 * 参数: @param file 文件路径
	 * 参数: @return
	 * 返回: byte[]
	 * 异常 
	 * 作者: king 
	 * 日期: 2013-6-15
	 */

	public byte[] uploadFile(String url, String filename, String file) {
		HttpPost httpPost = new HttpPost(url.substring(0, url.indexOf("?")));
		MultipartEntity mpEntity = parseEntity(url); //文件传输
		ContentBody cbFile = new FileBody(new File(file));
		mpEntity.addPart(filename, cbFile); // <input type="file" name="userfile" />  对应的
		httpPost.setEntity(mpEntity);
		return processRequest(httpPost);
	}

	/**
	 * 方法: request
	 * 描述: TODO
	 * 参数: @param httpGet
	 * 参数: @return
	 * 返回: ByteArrayOutputStream
	 * 异常
	 * 作�?: zhaokang zhaokang@1000chi.com
	 * 日期: 2011-12-9
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public byte[] processRequest(HttpUriRequest request) {
		if (Utils.isMobile(mContext)) {// 使用移动网络时,延长超时时间,和增加相应的代理设置
			HttpParams param = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(param, 15000);
			HttpConnectionParams.setSoTimeout(param, 10000);
			request.setParams(param);
			if (Utils.isCmwap(mContext)) {
				try {
					String resUrl = request.getURI().toURL().toString();
					String wapUrl = Utils.getWapUrl(resUrl);
					String host = Utils.getHost(resUrl);
					((HttpRequestBase) request).setURI(URI.create(wapUrl));
					request.setHeader("X-Online-Host", host);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d(TAG, "Request Line: " + request.getRequestLine().toString());
		Header[] reqHeaders = request.getAllHeaders();
		for (Header header : reqHeaders)
			Log.d(TAG, header.getName() + ":" + header.getValue());

		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e1) {
			request.abort();
			e1.printStackTrace();
			return handleException(-1, "请求协议异常,请检查url");
		} catch (IOException e1) {
			request.abort();
			e1.printStackTrace();
			return handleException(-1, "I/O异常,网络上行传输流可能已中断");
		}
		StatusLine statusLine = response.getStatusLine();
		Log.d(TAG, "Status Line: " + statusLine);
		Header[] resHeaders = response.getAllHeaders();
		for (Header header : resHeaders)
			Log.d(TAG, header.getName() + ":" + header.getValue());
		if (statusLine.getStatusCode() == 200) {// 网络请求成功
			HttpEntity responseEntity = response.getEntity();
			try {
				return EntityUtils.toByteArray(responseEntity);
			} catch (IOException e1) {
				e1.printStackTrace();
				return handleException(-1, "I/O异常,网络上行传输流可能已中断");
			}
		} else {// 网络请求异常
			request.abort();
			return handleException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
	}

	/**
	 * 方法: request
	 * 描述: TODO 采用http方式实现的请求方�?
	 * 
	 * @param reqUrl
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public byte[] request(String reqUrl, boolean isPost) {
		byte[] retVal = null;
		if (isPost)
			retVal = processRequest(parseParams(reqUrl));
		else
			retVal = requestByGet(reqUrl);
		return retVal;
	}

	private MultipartEntity parseEntity(String url) {
		MultipartEntity entity = new MultipartEntity();
		int splitFlag = url.indexOf("?");
		String paramStr = url.substring(splitFlag + 1);
		String[] paramsArray = paramStr.split("&");
		for (String param : paramsArray) {
			String[] paramPair = param.split("=");
			try {
				if (paramPair.length > 1)
					entity.addPart(paramPair[0], new StringBody(URLDecoder.decode(paramPair[1], "UTF-8"), Charset.defaultCharset()));
				else
					entity.addPart(paramPair[0], new StringBody("", Charset.defaultCharset()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	/**
	 * 方法: parseParams
	 * 描述: TODO 将get请求的url转换封装为post请求对象
	 * 参数: @param url
	 * 参数: @return
	 * 参数: @throws UnsupportedEncodingException
	 * 返回: HttpPost
	 * 异常
	 * 作�?: King zhaokang@1000chi.com
	 * 日期: 2011-12-23
	 */
	private HttpPost parseParams(String url) {
		Log.d(TAG, "post convert to get:" + url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		int splitFlag = url.indexOf("?");
		String paramStr = url.substring(splitFlag + 1);
		String[] paramsArray = paramStr.split("&");
		for (String param : paramsArray) {
			String[] paramPair = param.split("=");
			if (paramPair.length > 1)
				try {
					params.add(new BasicNameValuePair(paramPair[0], URLDecoder.decode(paramPair[1], "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			else
				params.add(new BasicNameValuePair(paramPair[0], ""));
		}
		HttpPost httpPost = new HttpPost(url.substring(0, splitFlag));
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpPost;
	}

	// 异常处理
	private byte[] handleException(int code, String defMsg) {
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			obj.put("status", "err");
			obj.put("data", data);
			data.put("code", code);
			switch (code) {
			case 404:
				data.put("msg", HTTP_404);
				break;
			case 408:
				data.put("msg", HTTP_408);
				break;
			case 502:
				data.put("msg", HTTP_502);
				break;
			case 504:
				data.put("msg", HTTP_504);
				break;
			default:
				data.put("msg", defMsg);
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString().getBytes();
	}


	/**
	 * 方法: shutConnection
	 * 描述: TODO
	 */
	public void shutConnection() {
		client.getConnectionManager().shutdown();
		client = null;
		instance = null;
	}
}
