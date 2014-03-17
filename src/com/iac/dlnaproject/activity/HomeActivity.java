
package com.iac.dlnaproject.activity;

import com.iac.dlnaproject.DLNAapp;
import com.iac.dlnaproject.MediaControllerService;
import com.iac.dlnaproject.fragment.FeedsFragment;
import com.iac.dlnaproject.fragment.HomeFragment;
import com.iac.dlnaproject.fragment.menu.HomeMenuFragment;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.model.UIEventHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class HomeActivity extends FunctionBaseActivity {

    protected Fragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //initial UI
            mFrag = HomeFragment.getInstance(null);
            setMainContentFragment(mFrag);
            setLeftDrawerMenuFragment(HomeMenuFragment.getInstance(null));
        } else {
            mFrag = getMainContentFragment();
        }
        startService(new Intent(DLNAapp.getApplication(),
                MediaControllerService.class));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(DLNAapp.getApplication(),
                MediaControllerService.class));
        super.onDestroy();
    }

    @Override
    public void receive(UIEvent message) {
        switch (UIEventHelper.match(message)) {
            case UIEventHelper.REQUEST_FRAGMENT_DLNA_HOME:
                closeMenus();
                mFrag = HomeFragment.getInstance(null);
                setMainContentFragment(mFrag);
                break;
            case UIEventHelper.REQUEST_FRAGMENT_IAC_CLOUD:
                closeMenus();
                mFrag = FeedsFragment.getInstance(null);
                setMainContentFragment(mFrag);
                break;
        }
    }

    @Override
    public void onDrawerMenuOpened(View drawerView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDrawerMenuClosed(View drawerView) {
        // TODO Auto-generated method stub

    }

}
