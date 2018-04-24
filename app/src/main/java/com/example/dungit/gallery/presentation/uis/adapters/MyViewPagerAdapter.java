package com.example.dungit.gallery.presentation.uis.adapters;

/**
 * Created by DUNGIT on 4/22/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    // Tab Captions
    private String tabCaption[] = new String[] { "PICTURES", "ALBUMS", "STORIES" };
    private ArrayList<Fragment> lstFragMent;

    public MyViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void setLstFragMent(ArrayList<Fragment>lstFragMent) {
        this.lstFragMent = lstFragMent;
    }

    @Override
    public int getCount() {
        return tabCaption.length;
    }

    @Override
    public Fragment getItem(int position) {
       return lstFragMent.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabCaption[position]; // return tab caption
    }
}