
package com.iac.dlnaproject.fragment;

import com.devspark.progressfragment.ProgressFragment;
import com.iac.dlnaproject.DLNAapp;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.model.ActivityModel;
import com.iac.dlnaproject.model.Participant;
import com.iac.dlnaproject.model.UIEvent;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class RefreshableBaseFragment extends ProgressFragment implements
OnRefreshListener, Participant {

    private ActivityModel mActivityActionModel;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Host) {
            try {
                setActivityActionModel(((Host)activity).getActivityActionModel());
            } catch (ClassCastException e) {
                Log.i(DLNAapp.TAG, activity.toString() + " must implement Participant.Host");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setActivityActionModel(null);
    }

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup)view;

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity()).insertLayoutInto(viewGroup)
        .theseChildrenArePullable(getContentView().getId(), R.id.empty_area).listener(this)
        .options(Options.create().refreshOnUp(true).build()).setup(mPullToRefreshLayout);

        setContentShown(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                obtainData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public abstract void obtainData();

    public final void updateView() {
        onUpdateView();
        // Notify PullToRefreshLayout that the refresh has finished
        mPullToRefreshLayout.setRefreshComplete();
        setContentShown(true);
    }

    public abstract void onUpdateView();

    @Override
    public void send(UIEvent message) {
        getActivityActionModel().send(this, message);
    }

    @Override
    public void onRefreshStarted(View view) {
        setContentShown(false);
        obtainData();
    }

    public ActivityModel getActivityActionModel() {
        return mActivityActionModel;
    }

    private void setActivityActionModel(ActivityModel model) {
        if (mActivityActionModel != null) {
            mActivityActionModel.Unregister(this);
            mActivityActionModel = null;
        }
        if (model != null) {
            mActivityActionModel = model;
            mActivityActionModel.Register(this);
        }
    }

}
