package com.example.newsclub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.example.newsclub.api.ApiConfig;
import com.example.newsclub.api.ApiInfo;
import com.example.newsclub.api.HttpTask;
import com.example.newsclub.api.HttpTask.TaskListener;
import com.example.newsclub.api.TaskRequest;
import com.example.newsclub.api.TaskResult;
import com.example.newsclub.module.NewsPaper;

public class NewsPaperActivity extends Activity {

	private NewsPaper paper;
	private WebView webPaper;
	private ImageButton navigation;
	private HttpTask task;

	public static void actionShow(Context fromAct, String id) {
		Intent intent = new Intent(fromAct, NewsPaperActivity.class);
		intent.putExtra("article_id", id);
		fromAct.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.news_paper);

		String articleId = getIntent().getStringExtra("article_id");

		task = new HttpTask(this, new TaskListener() {

			@Override
			public void onPostResult(TaskResult result) {
				paper = (NewsPaper) result.singleData;
				webPaper.loadData("<h2 style=\"margin-left:12px;\">" + paper.title +  "</h2><p style=\"font-size:13px;margin-left:12px;\">&#26469;&#28304;: " + paper.host + "   " + paper.createTime +"</p>" + paper.content, "text/html", "UTF-8");
			}

			@Override
			public void onError(int errorCode, String msg) {
				// TODO Auto-generated method stub

			}
		});
		TaskRequest<NewsPaper> request = new TaskRequest<NewsPaper>(new ApiInfo(ApiConfig.SERVER_URL, ApiConfig.NEWS_CONTENT_MODEL, ApiConfig.NEWS_CONTENT_PARAMS),
				NewsPaper.class, TaskResult.TYPE_SINGLE);
		request.getApiInfo().setParam(ApiConfig.NEWS_CONTENT_PARAMS[0], articleId);
		task.execute(request);

		webPaper = (WebView) findViewById(R.id.paper);
		webPaper.getSettings().setDefaultTextEncodingName("utf-8");
		
		
		navigation = (ImageButton) findViewById(R.id.btn_back);

		navigation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					finish();

			}
		});
		
		super.onCreate(savedInstanceState);
	}


	
	@Override
	protected void onDestroy() {
		if (task != null && task.getStatus() != Status.FINISHED) {
			task.cancel(true);
		}
		super.onDestroy();
	}

}
