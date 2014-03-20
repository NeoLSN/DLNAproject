
package com.iac.dlnaproject.activity;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.nowplay.NowPlayingFragment;
import com.iac.dlnaproject.nowplay.PlayQueueFragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;

public class NowPlayingActivity extends FunctionBaseActivity {

    private Fragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // initial UI
            mFrag = new NowPlayingFragment();
            setMainContentFragment(mFrag);
            setRightDrawerMenuFragment(PlayQueueFragment.getInstance(null));
        } else {
            mFrag = getMainContentFragment();
        }
        getNowPlayingBar().setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.now_playing, menu);
        return true;
    }

    @Override
    public void onDrawerMenuOpened(View drawerView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDrawerMenuClosed(View drawerView) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_VIEW:
                //receive((UIEvent)msg.obj);
                return true;
            default:
                return false;
        }
    }

}
