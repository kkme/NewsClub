package com.example.newsclub.api;

import java.util.List;

import android.text.TextUtils;

import com.example.newsclub.utils.Json;

public class TaskRequest<T extends IJsonAnalysis> {

	private ApiInfo apiInfo;
	private T jsonAnalysis;
	private int resultType;
	private Json rawJson;

	public TaskRequest(ApiInfo apiInfo, Class<T> clazz, int resultType) {
		this.apiInfo = apiInfo;
		this.resultType = resultType;
		if (resultType != TaskResult.TYPE_RAW)
			try {
				jsonAnalysis = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	}

	public ApiInfo getApiInfo() {
		return apiInfo;
	}

	//解析返回结果
	public TaskResult read(String jsonString) throws ErrorMsg {
		TaskResult result = new TaskResult();
		switch (resultType) {
		case TaskResult.TYPE_RAW:
			result.rawData = rawJson;
			break;
		case TaskResult.TYPE_SINGLE:
			result.singleData = readBean(jsonString);
			break;
		case TaskResult.TYPE_COLLECTION:
			result.collectionData = readList(jsonString);
			result.rawData = rawJson;
			break;
		}
		result.status = TaskResult.STATUS_OK;
		return result;
	}

	//解析获取結果集
	private List<? extends IJsonAnalysis> readList(String jsonString) throws ErrorMsg {
		Json json = parseRetInfo(jsonString);
		if (json.getString("status").equals("ok")) {
			return jsonAnalysis.parseJsons(jsonString);
		} else {
			throw new ErrorMsg("");
		}
	}

	//解析获取单独的对象
	private IJsonAnalysis readBean(String jsonString) throws ErrorMsg {
		Json json = parseRetInfo(jsonString);
		if (json.getString("status").equals("ok")) {
			return jsonAnalysis.parseJson(jsonString);
		} else {
			throw new ErrorMsg("");
		}
	}

	private Json parseRetInfo(String json) {
		if (!TextUtils.isEmpty(json)) {
			rawJson = new Json(json);
			return rawJson;
		}
		return null;
	}
}
