package com.example.newsclub;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.newsclub.adapter.LeftNavigationAdapter;
import com.example.newsclub.api.ApiConfig;
import com.example.newsclub.api.ApiInfo;
import com.example.newsclub.api.HttpClientConnector;
import com.example.newsclub.db.CategoryTable;
import com.example.newsclub.db.DatabaseUtil;
import com.example.newsclub.module.NewsCategory;

public class LeftNavigation {

	interface NavigationListener {
		void onItemSelect(int position,String label);
	}

	private NavigationListener mListener;
	private LeftNavigationAdapter menuAdapter;

	private List<NewsFragment> fragments = new ArrayList<NewsFragment>();

	public LeftNavigation(final Context context, ListView menuList) {
		Cursor cursor = DatabaseUtil.getInstance(context.getApplicationContext()).fetchAllCategorys();
		while (cursor.moveToNext()) {
			NewsCategory nc = CategoryTable.parseCursor(cursor);
			NewsFragment nf = new NewsFragment();
			Bundle b = new Bundle();
			b.putString("id", nc.id);
			nf.setArguments(b);
			fragments.add(nf);
		}
		menuAdapter = new LeftNavigationAdapter(context, cursor);
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				menuAdapter.setActiveView(position);
				menuAdapter.refresh();
				NewsCategory newsc = menuAdapter.getItem(position);
				if (mListener != null)
					mListener.onItemSelect(position,newsc.showName);
			}
		});

		if (cursor == null || cursor.getCount() == 0) {
			new AsyncTask<Object, Object, List<NewsCategory>>() {

				@Override
				protected List<NewsCategory> doInBackground(Object... params) {
					ApiInfo api = new ApiInfo(ApiConfig.SERVER_URL, ApiConfig.NEWS_CATEGORY_MODEL, ApiConfig.NEWS_CATEGORY_PARAMS);
					byte[] b = HttpClientConnector.getInstance(context.getApplicationContext()).request(api.getSignatruedUrl(context.getApplicationContext()), true);
					NewsCategory category = new NewsCategory();
					List<NewsCategory> data = category.parseJsons(new String(b));
					DatabaseUtil.getInstance(context.getApplicationContext()).insertCategorys(data);
					return data;
				}

				@Override
				protected void onPostExecute(List<NewsCategory> result) {
					menuAdapter.refresh();
					super.onPostExecute(result);
				}

			}.execute();
		}
	}

	public void setSelect(int position) {
		menuAdapter.setActiveView(position);
		menuAdapter.refresh();
	}

	public void setNavigationListener(NavigationListener listener) {
		mListener = listener;
	}

	public List<NewsFragment> getFragments() {
		return fragments;

	}
}
