package com.nestedworld.nestedworld.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom FragmentPagerAdapter
 * It's use for displaying the TABS under activity.mainMenu
 */
public class TabsAdapter extends FragmentPagerAdapter {
    protected final String TAG = getClass().getSimpleName();

    private final List<CustomTab> tabList = new ArrayList<>();

    /*
    ** Constructor
     */
    public TabsAdapter(@NonNull final FragmentManager fm) {
        super(fm);
    }

    /*
    ** Public method
     */
    public void addFragment(@NonNull final String title, @NonNull final Fragment fragment, final int icon) {
        tabList.add(new CustomTab(title, fragment, icon));
    }

    /*
    ** Parents method
     */
    @Override
    public Fragment getItem(int position) {
        return tabList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).getTitle();
    }

    public int getPageIcon(int position) {
        return tabList.get(position).getIcon();
    }

    /**
     * Custom class for easy tab management
     */
    public class CustomTab {
        private final Fragment mFragment;
        private final int mIcon;
        private String mTitle = "";

        public CustomTab(@NonNull final String title, @NonNull final Fragment fragment, final int icon) {
            mTitle = title;
            mFragment = fragment;
            mIcon = icon;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public String getTitle() {
            return mTitle;
        }

        public int getIcon() {
            return mIcon;
        }
    }
}
