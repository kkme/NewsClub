package com.example.newsclub;

import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.MenuDrawer.Type;
import net.simonvt.menudrawer.Position;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newsclub.LeftNavigation.NavigationListener;
import com.example.newsclub.adapter.ViewPagerAdapter;

public class MainActivity extends FragmentActivity {

	private MenuDrawer menuDrawer;
	private ListView menuList;

	private ViewPager viewPager;
	private int mPagerOffsetPixels;
	private int mPagerPosition;

	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		menuDrawer = MenuDrawer.attach(this,Type.BEHIND,Position.LEFT,MenuDrawer.MENU_DRAG_WINDOW);
		menuDrawer.setMenuView(R.layout.left_navigation);
		menuDrawer.setContentView(R.layout.activity_main);
		menuDrawer.setDrawerIndicatorEnabled(true);
		menuDrawer.setSlideDrawable(R.drawable.ic_launcher);

		menuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
			@Override
			public boolean isViewDraggable(View v, int dx, int x, int y) {
				if (v == viewPager) {
					return !(mPagerPosition == 0 && mPagerOffsetPixels == 0) || dx < 0;
				}
				return false;
			}
		});
		title = (TextView) findViewById(R.id.header_title);

		menuList = (ListView) findViewById(R.id.menu);
		final LeftNavigation navigation = new LeftNavigation(this, menuList);
		navigation.setNavigationListener(new NavigationListener() {

			@Override
			public void onItemSelect(int position,String label) {
				viewPager.setCurrentItem(position);
				menuDrawer.closeMenu();
				title.setText(label);
			}
		});

		viewPager = (ViewPager) findViewById(R.id.news_pager);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				mPagerPosition = position;
				mPagerOffsetPixels = positionOffsetPixels;
				navigation.setSelect(position);
			}
		});

		List<NewsFragment> fragments = navigation.getFragments();
		final ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(vpAdapter);

		findViewById(R.id.btn_nav).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (menuDrawer.isMenuVisible())
					menuDrawer.closeMenu();
				else
					menuDrawer.openMenu();
			}
		});
		

		super.onCreate(savedInstanceState);
	}

}
