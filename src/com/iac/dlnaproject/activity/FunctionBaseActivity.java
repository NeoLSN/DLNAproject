
package com.iac.dlnaproject.activity;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.model.ActivityModel;
import com.iac.dlnaproject.model.Participant.Host;
import com.iac.dlnaproject.model.UIEvent;
import com.viewpagerindicator.PageIndicator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public abstract class FunctionBaseActivity extends ActionBarActivity implements Host {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private boolean hasLeftMenu = false;
    private boolean hasRightMenu = false;

    private ActivityModel mActivityActionModel;

    private ViewPager mPager;
    private View mMainLayout;
    private View nowPlayingBar;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_base);
        mMainLayout = findViewById(R.id.main_frame);

        mPager = (ViewPager)findViewById(R.id.pager);
        mIndicator = (PageIndicator)findViewById(R.id.indicator);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drw_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                onDrawerMenuOpened(drawerView);
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                            Gravity.RIGHT);
                } else {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                            Gravity.LEFT);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                onDrawerMenuClosed(drawerView);
                ensureLockMode();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ensureLockMode();

        mActivityActionModel = ActivityModel.create(this);

        nowPlayingBar = findViewById(R.id.now_playing_bar);
        nowPlayingBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        mActivityActionModel.Unregister(this);
        mActivityActionModel.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                            Gravity.LEFT);
                } else if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                            Gravity.RIGHT);
                } else if (hasLeftMenu) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMainContentFragment(Fragment fragment) {
        mMainLayout.setVisibility(View.VISIBLE);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_frame, fragment);
        t.commit();
    }

    public Fragment getMainContentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_frame);
    }

    public void setLeftDrawerMenuFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.menu_left_side, fragment);
        t.commit();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        hasLeftMenu = true;
        ensureLockMode();
    }

    public void setRightDrawerMenuFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.menu_right_side, fragment);
        t.commit();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_r, GravityCompat.END);
        hasRightMenu = true;
        ensureLockMode();
    }

    private void ensureLockMode() {
        if (hasLeftMenu) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        }
        if (hasRightMenu) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        }
    }

    public abstract void onDrawerMenuOpened(View drawerView);

    public abstract void onDrawerMenuClosed(View drawerView);

    @Override
    public ActivityModel getActivityActionModel() {
        return mActivityActionModel;
    }

    @Override
    public void saveModel(ActivityModel model) {}

    @Override
    public void send(UIEvent message) {
        getActivityActionModel().send(this, message);
    }

    public void closeMenus() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    public View getNowPlayingBar() {
        return nowPlayingBar;
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    public View getMainLayout() {
        return mMainLayout;
    }

    public PageIndicator getPageIndicator() {
        return mIndicator;
    }

}
