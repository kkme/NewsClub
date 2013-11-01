package com.example.newsclub.module;

import java.util.ArrayList;
import java.util.List;

import com.example.newsclub.api.IJsonAnalysis;
import com.example.newsclub.db.CategoryTable;
import com.example.newsclub.utils.Json;

import android.content.ContentValues;

public class NewsCategory implements IJsonAnalysis {
	public String id;
	public String showName;
	public String host;

	public ContentValues makeValues() {
		ContentValues v = new ContentValues();
		v.put(CategoryTable._ID, id);
		v.put(CategoryTable.NAME, showName);
		v.put(CategoryTable.HOST, host);
		return v;
	}

	@Override
	public List<NewsCategory> parseJsons(String jsons) {
		List<NewsCategory> ret = new ArrayList<NewsCategory>();
		Json j = new Json(jsons);
		if ("ok".equals(j.getString("status", null))) {
			Json[] dataArray = j.getJsonArray("data");
			for (Json json : dataArray) {
				NewsCategory category = new NewsCategory();
				category.id = json.getString("_id", "");
				category.showName = json.getString("column", "");
				category.host = json.getString("host", "");
				ret.add(category);
			}
		}
		return ret;
	}

	@Override
	public IJsonAnalysis parseJson(String json) {
		// TODO Auto-generated method stub
		return null;
	}
}
