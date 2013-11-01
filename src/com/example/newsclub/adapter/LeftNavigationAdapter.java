package com.example.newsclub.adapter;

import com.example.newsclub.R;
import com.example.newsclub.db.CategoryTable;
import com.example.newsclub.module.NewsCategory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LeftNavigationAdapter extends CursorAdapter {

	private int activePosition = 0;

	public LeftNavigationAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public void setActiveView(int position) {
		this.activePosition = position;
	}

	@Override
	public NewsCategory getItem(int position) {
		return CategoryTable.parseCursor((Cursor) super.getItem(position));
	}

	@Override
	public void bindView(View convertView, Context ctx, Cursor c) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		NewsCategory category = CategoryTable.parseCursor(c);
		holder.name.setText(category.showName);
		if (activePosition == c.getPosition())
			holder.name.setBackgroundColor(ctx.getResources().getColor(android.R.color.holo_orange_light));
		else
			holder.name.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup arg2) {
		View convertView = LayoutInflater.from(ctx).inflate(R.layout.item_navigation, null);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.cate);
		convertView.setTag(holder);
		return convertView;
	}

	class ViewHolder {
		TextView name;
	}

	public boolean refresh() {
		return getCursor().requery();
	}
}
