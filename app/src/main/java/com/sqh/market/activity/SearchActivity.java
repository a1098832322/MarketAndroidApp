package com.sqh.market.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sqh.market.R;
import com.sqh.market.fragments.AllCommodityFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;


    private String searchContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //设置从别的页面传过来的搜索内容
        Intent intent = getIntent();
        searchContent = intent.getStringExtra("content");

        //绑定控件
        init();

        //初始化数据
        initData();

        //给Toolbar添加返回按钮
        mToolbar.setTitle("商品搜索");//设置ToolBar的标题
        mToolbar.setTitleTextColor(Color.WHITE);
        //返回按钮颜色显示不正常时,以下三行是修改回退按钮为白色的逻辑
        Drawable upArrow = ContextCompat.getDrawable(SearchActivity.this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回按鈕点击事件，关闭当前activity
                finish();
            }
        });


    }


    /**
     * 初始化控件绑定
     */
    public void init() {
        mToolbar = findViewById(R.id.toolbar);
        mViewPager = findViewById(R.id.view_pager_menu);
    }

    /**
     * 初始化fragment
     */
    public void initData() {
        //viewpager初始化、监听设置
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setOffscreenPageLimit(1);

        List<Fragment> fragments = new ArrayList<>(1);
        AllCommodityFragment page = new AllCommodityFragment();
        page.setSearchContent(searchContent);
        fragments.add(page);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pagerAdapter);


    }


    /**
     * 设置viewPage样式
     */
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0: {
                    mViewPager.setBackgroundColor(ContextCompat.getColor(SearchActivity.this, R.color.white));
                    break;
                }
                case 1: {
                    mViewPager.setBackgroundColor(ContextCompat.getColor(SearchActivity.this, R.color.white));
                    break;
                }


            }
        }
    };

    /**
     * viewpager适配器
     */
    private static class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }


}
