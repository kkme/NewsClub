package com.example.newsclub.utils;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class Json {
	JSONObject root = null;

	public Json(String jsonString) {
		try {
			root = new JSONObject(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param jsonObject
	 */
	public Json(JSONObject jsonObject) {
		root = jsonObject;
	}

	public Json() {
		root = new JSONObject();
	}

	public boolean put(String key, int value) {
		try {
			root.put(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean put(String key, Object value) {
		try {
			root.put(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		if (root == null) {
			return null;
		}
		return root.toString();
	}

	public String getString(String key, String def) {
		try {
			if (!root.has(key) || TextUtils.isEmpty(root.getString(key))) {
				return def;
			} else {
				return root.getString(key);
			}
		} catch (Exception e) {
			return def;
		}
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public int getInt(String key, int def) {
		try {
			if (root.has(key)) {
				return root.getInt(key);
			} else {
				return def;
			}
		} catch (JSONException e) {
			return def;
		}
	}

	public long getLong(String key, long def) {
		try {
			if (root.has(key)) {
				return root.getLong(key);
			} else {
				return def;
			}
		} catch (JSONException e) {
			return def;
		}
	}

	public Double getDouble(String key, Double def) {
		try {
			if (root.has(key)) {
				return root.getDouble(key);
			} else {
				return def;
			}
		} catch (JSONException e) {
			return def;
		}
	}

	public boolean getBoolean(String key) {
		try {
			if (root.has(key)) {
				return root.getBoolean(key);
			} else {
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
	}

	public Json getJson(String key) {
		try {
			if (root.has(key)) {
				return new Json(root.getJSONObject(key));
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public Json[] getJsonArray(String key) {
		Json[] jsons;
		try {
			if (!root.has(key)) {
				return null;
			}
			JSONArray a = root.getJSONArray(key);
			jsons = new Json[a.length()];
			for (int i = 0; i < jsons.length; i++) {
				jsons[i] = new Json(a.getJSONObject(i));
			}
			return jsons;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Json[] convertJsonArray(JSONArray key) {
		if (key == null) {
			return null;
		}
		Json[] jsons;
		try {
			jsons = new Json[key.length()];
			for (int i = 0; i < jsons.length; i++) {
				jsons[i] = new Json(key.getJSONObject(i));
			}
			return jsons;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getObject(String key) {
		try {
			if (root.has(key)) {
				return root.get(key);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Iterator<String> keys() {
		return root.keys();
	}

	public int getLength() {
		return root.length();
	}

	public boolean hasKey(String key) {
		if (root.has(key)) {
			return true;
		}
		return false;
	}
}
