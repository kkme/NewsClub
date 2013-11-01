package com.example.newsclub.module;

import java.util.ArrayList;
import java.util.List;

import com.example.newsclub.api.IJsonAnalysis;
import com.example.newsclub.utils.Json;

public class News implements IJsonAnalysis {
	public String id;
	public String title;
	public String desc;
	public String createTime;
	public String img;

	@Override
	public List<News> parseJsons(String jsons) {
		List<News> ret = new ArrayList<News>();
		Json json = new Json(jsons);
		if ("ok".equals(json.getString("status", ""))) {
			Json[] dataArray = json.getJsonArray("data");
			for (Json j : dataArray) {
				News n = new News();
				n.id = j.getString("_id", "");
				n.title = j.getString("title", "");
				n.desc = j.getString("description", "");
				n.createTime = j.getString("created", "");
				n.img = j.getString("top_image", "");
				ret.add(n);
			}
		}
		return ret;
	}

	@Override
	public News parseJson(String json) {
		// TODO Auto-generated method stub
		return null;
	}
}
