
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FeedsFragment extends BaseFragment {

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new FeedsFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_feeds, container, false);
    }

}
