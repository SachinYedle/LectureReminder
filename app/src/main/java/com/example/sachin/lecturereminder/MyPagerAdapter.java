package com.example.sachin.lecturereminder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.sachin.lecturereminder.dbModel.classData;

import java.util.ArrayList;

/**
 * Created by admin1 on 5/10/16.
 */

/**tab layout pager adapter*/
public class MyPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> tabs;
    public MyPagerAdapter(FragmentManager fm, ArrayList<String> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = new TabFragment();
        fragment.setPosition(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }
}
