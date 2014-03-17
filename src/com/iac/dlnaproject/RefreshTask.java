package com.iac.dlnaproject;

import com.iac.dlnaproject.util.CommonUtil;

import android.content.Context;

public abstract class RefreshTask extends Thread {
    private static final int REFRESH_DEVICES_INTERVAL = 30 * 1000;

    private Context mContext;
    private boolean mIsExit = false;

    public RefreshTask(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void awakeThread() {
        synchronized (this) {
            notifyAll();
        }
    }

    public void reset() {
        awakeThread();
    }

    public void exit() {
        mIsExit = true;
        awakeThread();
    }

    @Override
    public void run() {

        while (true) {
            if (mIsExit) {
                onStop();
                break;
            }

            refresh();

            synchronized (this) {
                try {
                    wait(REFRESH_DEVICES_INTERVAL);
                } catch (InterruptedException e) {}
            }
        }
    }

    protected void refresh() {
        if (!CommonUtil.checkNetworkState(getContext())) {
            return;
        }
        onRefresh();
    }

    protected abstract void onRefresh();
    protected abstract void onStop();
}
