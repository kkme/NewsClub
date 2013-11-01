package com.example.newsclub.adapter;

import java.util.List;

import com.example.newsclub.R;
import com.example.newsclub.R.layout;
import com.example.newsclub.module.News;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	private Context context;

	private List<News> data;

	public NewsAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<News> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public News getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_news, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		News news = data.get(position);
		holder.title.setText(news.title);
		holder.time.setText(news.createTime);
		holder.desc.setText(news.desc);
		Picasso.with(context).load(news.img).into(holder.icon);
		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView desc;
		TextView time;
		ImageView icon;
	}
}
