package com.example.newsclub.db;

import java.io.File;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.newsclub.module.NewsCategory;

public class DatabaseUtil {
	private static final String TAG = "DatabaseUtil";

	private static final String DATABASE_NAME = "news_db";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseUtil instance = null;
	private static DatabaseHelper mOpenHelper = null;
	private Context mContext = null;

	/**
	 * SQLiteOpenHelper
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		// Construct
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		public DatabaseHelper(Context context) {
			this(context, DATABASE_NAME, DATABASE_VERSION);
		}

		public DatabaseHelper(Context context, String name, int version) {
			this(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Create Database.");
			db.execSQL(CategoryTable.CREATE_TABLE);
		}

		@Override
		public synchronized void close() {
			Log.d(TAG, "Close Database.");
			super.close();
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			Log.d(TAG, "Open Database.");
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrade Database.");
			dropAllTables(db);
			db.execSQL(CategoryTable.CREATE_TABLE);
		}

		private void dropAllTables(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE_NAME);
		}
	}

	private DatabaseUtil(Context context) {
		mContext = context;
		mOpenHelper = new DatabaseHelper(context);
	}

	public static synchronized DatabaseUtil getInstance(Context context) {
		if (null == instance) {
			return new DatabaseUtil(context);
		}
		return instance;
	}

	public void close() {
		if (null != instance) {
			mOpenHelper.close();
			instance = null;
		}
	}

	/**
	 * 清空所有表中数据, 谨慎使用
	 * 
	 */
	public void clearData() {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		db.execSQL("DELETE FROM " + CategoryTable.TABLE_NAME);

	}

	/**
	 * 直接删除数据库文件, 调试用
	 * 
	 * @return true if this file was deleted, false otherwise.
	 * 
	 */
	public boolean deleteDatabase() {
		File dbFile = mContext.getDatabasePath(DATABASE_NAME);
		return dbFile.delete();
	}

	//批量插入分类信息
	public int insertCategorys(List<NewsCategory> categorys) {
		if (null == categorys || 0 == categorys.size()) {
			return 0;
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int result = 0;
		try {
			db.beginTransaction();

			for (int i = categorys.size() - 1; i >= 0; i--) {
				NewsCategory category = categorys.get(i);

				Log.d(TAG, "insertCategory, category id=" + category.id);
				if (TextUtils.isEmpty(category.id) || category.id.equals("false")) {
					Log.e(TAG, "tweet id is null, ghost message encounted");
					continue;
				}

				ContentValues initialValues = category.makeValues();
				long id = db.insert(CategoryTable.TABLE_NAME, null, initialValues);

				if (-1 == id) {
					Log.e(TAG, "cann't insert the category : " + category.toString());
				} else {
					++result;
					Log.v("TAG", "Insert category");
				}
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return result;
	}

	//获取分类信息
	public Cursor fetchAllCategorys() {
		SQLiteDatabase mDb = mOpenHelper.getReadableDatabase();

		return mDb.query(CategoryTable.TABLE_NAME, CategoryTable.TABLE_COLUMNS, null, null, null, null, CategoryTable._ID + " DESC ");
	}
}