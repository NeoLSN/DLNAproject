
package com.iac.dlnaproject.fragment;

import com.devspark.progressfragment.ProgressFragment;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.activity.FunctionBaseActivity;
import com.iac.dlnaproject.model.UIEvent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends ProgressFragment {

    private Messenger hostManager;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof FunctionBaseActivity) {
            hostManager = new Messenger(((FunctionBaseActivity)activity).getBinder());
            try {
                Message msg = Message.obtain(null,
                        FunctionBaseActivity.MSG_REGISTER_CLIENT);
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
                Message msg = Message.obtain(null,
                        FunctionBaseActivity.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                hostManager.send(msg);
            } catch (RemoteException e) {
            }
        }
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_progress, container, false);
    }

    protected void send(UIEvent event) {
        try {
            Message msg = Message.obtain(null,
                    FunctionBaseActivity.MSG_UPDATE_VIEW, 0, 0, event);
            hostManager.send(msg);
        } catch (RemoteException e) {
        }
    }

    protected void receive(UIEvent message) {}

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
