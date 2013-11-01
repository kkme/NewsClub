package com.example.newsclub.api;

import java.util.List;

import com.example.newsclub.utils.Json;

public class TaskResult {

	public static final int STATUS_ERROR = -1;
	public static final int STATUS_OK = 1;

	public static final int TYPE_RAW = 101;
	public static final int TYPE_SINGLE = 102;
	public static final int TYPE_COLLECTION = 103;

	/***
	 * rawData,collectionData,singleData只会有一个被赋值,根据请求时提供的所需结果类型进行结果解析
	 */
	public int status;//结果状态码
	public Json rawData;//服务器返回的原始数据字符串
	public List<? extends IJsonAnalysis> collectionData;//结果集
	public IJsonAnalysis singleData;//单体对象的结果

	public TaskResult() {
	}

	public TaskResult(int status) {
		this.status = status;
	}
}
