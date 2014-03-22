
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.MediaPlayingMonitorService;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.activity.FunctionBaseActivity;
import com.iac.dlnaproject.model.UIEvent;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class RefreshableBaseFragment extends Fragment implements OnRefreshListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    private Messenger hostManager;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof FunctionBaseActivity) {
            hostManager = new Messenger(((FunctionBaseActivity)activity).getBinder());
            try {
                Message msg = Message.obtain(null, FunctionBaseActivity.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                hostManager.send(msg);
            } catch (RemoteException e) {

            }
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        if (hostManager != null) {
            try {
                Message msg = Message
                        .obtain(null, MediaPlayingMonitorService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                hostManager.send(msg);
            } catch (RemoteException e) {
            }
        }
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ViewGroup viewGroup = (ViewGroup)view;

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity()).insertLayoutInto(viewGroup)
        .allChildrenArePullable()
        .listener(this)
        .options(Options.create().refreshOnUp(true).build())
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
        mPullToRefreshLayout.setRefreshComplete();
    }

    protected abstract void onUpdateView();

    @Override
    public void onRefreshStarted(View view) {}

    protected boolean onHandleMessage(Message msg) {
        // update now playing bar
        switch (msg.what) {
            default:
                return false;
        }
    }

    protected void send(UIEvent event) {
        try {
            Message msg = Message.obtain(null, FunctionBaseActivity.MSG_UPDATE_VIEW, 0, 0, event);
            hostManager.send(msg);
        } catch (RemoteException e) {
        }
    }

    protected void receive(UIEvent message) {
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FunctionBaseActivity.MSG_UPDATE_VIEW:
                    if (msg.obj != null) {
                        if (msg.obj instanceof UIEvent)
                            receive((UIEvent)msg.obj);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
