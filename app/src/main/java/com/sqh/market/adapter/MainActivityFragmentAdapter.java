package com.sqh.market.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 首页fragment适配器
 *
 * @author 郑龙
 */
public class MainActivityFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public MainActivityFragmentAdapter(FragmentManager fm, List<Fragment> mFragment) {

        super(fm);
        this.mFragments = mFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
