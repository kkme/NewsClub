package com.example.newsclub.api;

import java.util.List;

public interface IJsonAnalysis {
	List<? extends IJsonAnalysis> parseJsons(String jsons);

	IJsonAnalysis parseJson(String json);

}
