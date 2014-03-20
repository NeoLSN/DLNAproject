
package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.fragment.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NowPlayingFragment extends BaseFragment {

    private View mContentView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setContentView(mContentView);
        super.onActivityCreated(savedInstanceState);
        setContentShown(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_now_playing, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
