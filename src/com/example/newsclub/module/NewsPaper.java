package com.example.newsclub.module;

import java.util.List;

import com.example.newsclub.api.IJsonAnalysis;
import com.example.newsclub.utils.Json;

public class NewsPaper implements IJsonAnalysis {
	
	public String id;
	public String title;
	public String desc;
	public String host;
	public String createTime;
	public String content;

	@Override
	public List<? extends IJsonAnalysis> parseJsons(String jsons) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJsonAnalysis parseJson(String json) {
		Json j = new Json(json);
		Json data = j.getJson("data");
		id = data.getString("_id");
		title = data.getString("title");
		desc = data.getString("description");
		host = data.getString("host");
		createTime = data.getString("created");
		content = data.getString("content");
		return this;
	}

}
