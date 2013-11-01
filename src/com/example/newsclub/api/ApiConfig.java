package com.example.newsclub.api;

public class ApiConfig {

	public static final String SERVER_URL = "http://54.251.250.73:25678/";

	/**
	 * 通用参数
	 */
	public static String URL_POSTFIX = "app_key=399453420138";
	/**
	 * api模块
	 * */

	public static final String NEWS_CATEGORY_MODEL = "news/get_columns";
	public static final String NEWS_LIST_MODEL = "news/get_articles";
	public static final String NEWS_CONTENT_MODEL = "news/get_article";

	public static final String[] NEWS_CATEGORY_PARAMS = {};
	public static final String[] NEWS_LIST_PARAMS = { "column_id" };
	public static final String[] NEWS_CONTENT_PARAMS = { "article_id" };

}
