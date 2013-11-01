package com.example.newsclub.api;

import java.io.UnsupportedEncodingException;

import com.example.newsclub.utils.AndroidUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class HttpTask extends AsyncTask<TaskRequest<? extends IJsonAnalysis>, Object, TaskResult> {

	private static final String TAG = "HttpTask";
	private Context activity;
	private TaskListener mListener;

	public HttpTask(Context activity, TaskListener listener) {
		this.activity = activity;
		this.mListener = listener;
	}

	@Override
	protected TaskResult doInBackground(TaskRequest<? extends IJsonAnalysis>... params) {
		TaskRequest<? extends IJsonAnalysis> request = params[0];
		TaskResult taskResult = new TaskResult(TaskResult.STATUS_ERROR);
		try {
			if (!AndroidUtils.isNetworkValidate(activity)) {
				throw new ErrorMsg("network");
			}
			if (request == null && !this.isCancelled()) {
				throw new ErrorMsg("请求数据错误");
			}
			String url = request.getApiInfo().getSignatruedUrl(activity);
			Log.d(TAG, "url=" + url);
			if (url == null && !this.isCancelled()) {
				return taskResult;
			}

			if (request.getApiInfo().getRequstType() != ApiInfo.REQ_PUT) {
				String result;
				result = new String(HttpClientConnector.getInstance(activity).request(url, request.getApiInfo().getRequstType() != ApiInfo.REQ_GET), "UTF-8");
				Log.d(TAG, "result" + result);
				taskResult = request.read(result);
			} else {
				String result;
				result = new String(HttpClientConnector.getInstance(activity).uploadFile(url, "", request.getApiInfo().getFilePath()));
				Log.d(TAG, "result" + result);
				taskResult = request.read(result);
			}

		} catch (ErrorMsg e) {
			e.printStackTrace();
			if (e.isNetWorkError()) {
				publishProgress(-1, new ErrorMsg("network"));
			} else {
				publishProgress(-1, e.toString());
			}
			this.cancel(true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			publishProgress(-1, e.toString());
			this.cancel(true);
		}
		return taskResult;
	}

	@Override
	protected void onPostExecute(TaskResult result) {
		Log.d(TAG, "onPostExecute");
		if (!this.isCancelled()) {
			if (mListener != null) {
				mListener.onPostResult(result);
			}
		}
	}

	public interface TaskListener {
		void onPostResult(TaskResult result);

		void onError(int errorCode, String msg);
	}
}
