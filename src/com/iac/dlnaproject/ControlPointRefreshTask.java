package com.iac.dlnaproject;

import org.cybergarage.upnp.ControlPoint;

import android.content.Context;

public class ControlPointRefreshTask extends RefreshTask {

    private boolean mStartComplete = false;
    private ControlPoint mControlPoint;

    public ControlPointRefreshTask(Context context, ControlPoint contorlPoint) {
        super(context);
        mControlPoint = contorlPoint;
    }

    public void setCompleteFlag(boolean flag) {
        mStartComplete = flag;
    }

    @Override
    public void reset() {
        setCompleteFlag(false);
        super.reset();
    }

    @Override
    protected void onStop() {
        mControlPoint.stop();
    }

    @Override
    protected void onRefresh() {
        try {
            if (mStartComplete) {
                mControlPoint.search();
            } else {
                boolean startRet = mControlPoint.start();
                if (startRet) {
                    mStartComplete = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
