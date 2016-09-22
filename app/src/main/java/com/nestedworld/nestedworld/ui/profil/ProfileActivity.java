package com.nestedworld.nestedworld.ui.profil;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;

import butterknife.Bind;

public class ProfileActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    /*
    ** Life Cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty_with_toolbar;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setUpToolbar();
        ProfileFragment.load(getSupportFragmentManager(), false);
    }

    /*
    ** Utils
     */
    private void setUpToolbar() {
        //Set the toolbar as actionBar
        setSupportActionBar(toolbar);

        //Get back the Toolbar as actionBar and then custom it
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //customise the actionBar
            actionBar.setTitle(getString(R.string.profile_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}