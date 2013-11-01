package com.example.newsclub.db;

import com.example.newsclub.module.NewsCategory;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

public class CategoryTable implements BaseColumns {

	private static final String TAG = "CategoryTable";

	public static final String TABLE_NAME = "category";

	public static final String NAME = "name";

	public static final String HOST = "host";

	public static final String[] TABLE_COLUMNS = { _ID, NAME, HOST };

	public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " text not null," //id
			+ NAME + " text,"//显示名字
			+ HOST + " text,"//地址
			+ "PRIMARY KEY (" + _ID + "))";

	public static NewsCategory parseCursor(Cursor cursor) {
		if (null == cursor || 0 == cursor.getCount()) {
			Log.w(TAG, "Cann't parse Cursor, bacause cursor is null or empty.");
			return null;
		} else if (-1 == cursor.getPosition()) {
			cursor.moveToFirst();
		}
		NewsCategory category = new NewsCategory();
		category.id = cursor.getString(cursor.getColumnIndexOrThrow(_ID));
		category.showName = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
		category.host = cursor.getString(cursor.getColumnIndexOrThrow(HOST));
		return category;
	}
}
