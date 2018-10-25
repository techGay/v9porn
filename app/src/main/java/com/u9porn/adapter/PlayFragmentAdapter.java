package com.u9porn.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.u9porn.ui.BaseFragment;

import java.util.List;

import javax.inject.Inject;

public class PlayFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    @Inject
    public PlayFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList == null ? "" : ((BaseFragment) fragmentList.get(position)).getTitle();
    }
}
