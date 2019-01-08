package com.innovation.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

//add by xuly 2018-06-12
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    //存储所有的fragment
    private List<Fragment> list;
    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list=list;
        //  Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        //  Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        //  Auto-generated method stub
        return list.size();
    }
}
