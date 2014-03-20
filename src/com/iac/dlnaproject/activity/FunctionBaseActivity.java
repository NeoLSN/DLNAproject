
package com.iac.dlnaproject.activity;

import com.iac.dlnaproject.MediaPlayingMonitorService;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.model.UIEvent;
import com.viewpagerindicator.PageIndicator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public abstract class FunctionBaseActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private boolean hasLeftMenu = false;
    private boolean hasRightMenu = false;

    private boolean isBind = false;
    private Messenger bindService;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

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
                ensureDrawerToggleState();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ensureDrawerToggleState();

        doBindService();
        nowPlayingBar = findViewById(R.id.now_playing_bar);
        nowPlayingBar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionBaseActivity.this, NowPlayingActivity.class);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
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
                if (hasLeftMenu || hasRightMenu) {
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
                    } else if (hasRightMenu) {
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }
                } else {
                    Intent upIntent = NavUtils.getParentActivityIntent(this);
                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
                    } else {
                        NavUtils.navigateUpTo(this, upIntent);
                    }
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
        ensureDrawerToggleState();
    }

    public void setRightDrawerMenuFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.menu_right_side, fragment);
        t.commit();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_r, GravityCompat.END);
        hasRightMenu = true;
        ensureDrawerToggleState();
    }

    private void ensureDrawerToggleState() {
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
        if (hasLeftMenu || hasRightMenu) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    public abstract void onDrawerMenuOpened(View drawerView);

    public abstract void onDrawerMenuClosed(View drawerView);

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

    //now playing bar update
    protected void doBindService() {
        Intent intent = new Intent(this, MediaPlayingMonitorService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    protected void doUnbindService() {
        if (isBind) {
            if (bindService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MediaPlayingMonitorService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    bindService.send(msg);
                } catch (RemoteException e) {
                }
            }
            unbindService(conn);
            isBind = false;
        }
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bindService = new Messenger(service);
            try {
                Message msg = Message.obtain(null,
                        MediaPlayingMonitorService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                bindService.send(msg);
            } catch (RemoteException e) {
            }
            isBind = true;
        }
    };

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //update now playing bar
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

    //UI control
    private List<Messenger> mClients = new ArrayList<Messenger>();
    private final Messenger mHostMessenger = new Messenger(new UIControlHandler());

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_UPDATE_VIEW = 3;

    class UIControlHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //update
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_UPDATE_VIEW:
                    for (int i = mClients.size() - 1; i >= 0; i--) {
                        try {
                            //TODO: send update message
                            mClients.get(i).send(Message.obtain(null, MSG_UPDATE_VIEW, 0, 0, msg.obj));
                        } catch (RemoteException e) {
                            mClients.remove(i);
                        }
                    }
                    onHandleMessage(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    protected boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_VIEW:
                if (msg.obj != null) {
                    if (msg.obj instanceof UIEvent)
                        receive((UIEvent)msg.obj);
                }
                return true;
            default:
                return false;
        }
    }

    protected void send(UIEvent event) {
        try {
            Message msg = Message.obtain(null,
                    FunctionBaseActivity.MSG_UPDATE_VIEW, 0, 0, event);
            mHostMessenger.send(msg);
        } catch (RemoteException e) {
        }
    }

    protected void receive(UIEvent obj) {}

    public IBinder getBinder() {
        return mHostMessenger.getBinder();
    }
}
