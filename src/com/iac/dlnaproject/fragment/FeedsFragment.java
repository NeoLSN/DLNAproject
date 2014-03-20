
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FeedsFragment extends BaseFragment {
    private View mContentView;

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new FeedsFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.content_feeds, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Setup content view
        setContentView(mContentView);
        // same as restoreInstanceState on activity
        super.onActivityCreated(savedInstanceState);
        setContentShown(true);
    }

}
