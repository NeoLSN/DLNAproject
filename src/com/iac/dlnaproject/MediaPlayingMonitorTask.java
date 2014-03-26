package com.iac.dlnaproject;

import org.cybergarage.upnp.ControlPoint;

import android.content.Context;

public class MediaPlayingMonitorTask extends RefreshTask {

    private static final int REFRESH_INTERVAL = 1 * 1000;

    private ControlPoint mControlPoint;

    public MediaPlayingMonitorTask(Context context, ControlPoint contorlPoint) {
        super(context);
        mControlPoint = contorlPoint;
        setRefreshInterval(REFRESH_INTERVAL);
    }

    public void resetControlPoint(ControlPoint contorlPoint) {
        if (contorlPoint != null)
            mControlPoint = contorlPoint;
    }

    @Override
    protected void onStop() {}

    @Override
    protected void onRefresh() {
        try {
            if (mControlPoint != null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
