package com.app.master.puntosderecargatrancaribe.Vista.Adaptadores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Rafael p on 2/8/2017.
 */

public class AdaptadorViewPagerPrincipal extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;

    public AdaptadorViewPagerPrincipal(FragmentManager fm,ArrayList<Fragment> fragment) {
        super(fm);
        this.fragments=fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
