package com.example.newsclub;

import java.util.List;

import com.example.newsclub.adapter.NewsAdapter;
import com.example.newsclub.api.ApiConfig;
import com.example.newsclub.api.ApiInfo;
import com.example.newsclub.api.HttpTask;
import com.example.newsclub.api.HttpTask.TaskListener;
import com.example.newsclub.api.TaskRequest;
import com.example.newsclub.api.TaskResult;
import com.example.newsclub.module.News;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NewsFragment extends Fragment {

	private static final String TAG = "NewsFragment";

	private ListView newsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		newsList = (ListView) inflater.inflate(R.layout.fragment_news, container, false);
		return newsList;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		final NewsAdapter adapter = new NewsAdapter(getActivity());
		newsList.setAdapter(adapter);

		String id = (String) getArguments().getCharSequence("id");
		HttpTask task = new HttpTask(getActivity(), new TaskListener() {

			@Override
			public void onPostResult(TaskResult result) {
				List<News> data = (List<News>) result.collectionData;
				adapter.setData(data);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int errorCode, String msg) {
				Log.w(TAG, "error code:" + errorCode + ",msg:" + msg);
			}
		});
		TaskRequest<News> request = new TaskRequest<News>(new ApiInfo(ApiConfig.SERVER_URL, ApiConfig.NEWS_LIST_MODEL, ApiConfig.NEWS_LIST_PARAMS), News.class,
				TaskResult.TYPE_COLLECTION);
		request.getApiInfo().setParam(ApiConfig.NEWS_LIST_PARAMS[0], id);
		task.execute(request);

		newsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				News news = (News) parent.getAdapter().getItem(position);
				String articleId = news.id;
				NewsPaperActivity.actionShow(getActivity(), articleId);
			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	public void loadData() {
		Bundle bundle = getArguments();
		if (bundle != null) {

		}
	}

}
