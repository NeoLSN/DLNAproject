
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.R;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.HeaderViewListener;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class RefreshableBaseFragment extends BaseFragment implements OnRefreshListener, HeaderViewListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ViewGroup viewGroup = (ViewGroup)view;

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity()).insertLayoutInto(viewGroup)
        .allChildrenArePullable()
        .listener(this)
        .options(Options.create()
                .refreshOnUp(true)
                .build())
                .setup(mPullToRefreshLayout);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPullToRefreshLayout.setRefreshing(true);
        obtainData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mPullToRefreshLayout.setRefreshing(true);
                obtainData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public abstract void obtainData();

    public final void updateView() {
        if (getView() != null) {
            onUpdateView();
        }
        if (mPullToRefreshLayout.isRefreshing())
            mPullToRefreshLayout.setRefreshComplete();
    }

    protected abstract void onUpdateView();

    @Override
    public void onRefreshStarted(View view) {
        // TODO implement method stub if needed
    }

    @Override
    public void onStateChanged(View headerView, int state) {
        // TODO implement method stub if needed
    }

}
