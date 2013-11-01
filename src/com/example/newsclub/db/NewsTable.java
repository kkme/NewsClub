package com.example.newsclub.db;

import com.example.newsclub.module.News;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

public class NewsTable implements BaseColumns {
	private static final String TAG = "NewsTable";

	public static String TABLE_NAME = "news";

	public static String TITLE = "title";
	public static String DESC = "desc";
	public static String IMG = "img";
	public static String CREATE_AT = "create_at";

	public static String[] TABLE_COLUMNS = { _ID, TITLE, DESC, CREATE_AT, IMG };
	public static String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " text not null,"//id
			+ TITLE + " text,"//标题
			+ DESC + " text,"//描述
			+ CREATE_AT + " text,"//时间
			+ IMG + " text,"//图片
			+ "primary key(_id))";

	public static News parserCursor(Cursor c) {
		if (c == null || c.getCount() == 0) {
			Log.w(TAG, "Cann't parse Cursor, bacause cursor is null or empty.");
			return null;
		} else if (-1 == c.getPosition()) {
			c.moveToFirst();
		}
		News n = new News();
		n.id = c.getString(c.getColumnIndexOrThrow(_ID));
		n.title = c.getString(c.getColumnIndexOrThrow(TITLE));
		n.desc = c.getString(c.getColumnIndexOrThrow(DESC));
		n.createTime = c.getString(c.getColumnIndexOrThrow(CREATE_AT));
		n.img = c.getString(c.getColumnIndexOrThrow(IMG));
		return n;
	}
}
