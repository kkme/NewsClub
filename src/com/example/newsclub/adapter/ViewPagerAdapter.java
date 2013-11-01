package com.example.newsclub.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.newsclub.NewsFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private List<NewsFragment> fragments;

	public ViewPagerAdapter(FragmentManager fm, List<NewsFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

}
