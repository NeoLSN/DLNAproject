package com.iac.dlnaproject;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.std.av.renderer.AVTransport;

import android.content.Context;
import android.text.TextUtils;

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
                ControllerProxy ctrlProxy = ControllerProxy.getInstance();
                Device dev = ctrlProxy.getSelectedRenderer();
                if (dev == null)
                    return;

                Service conDir = dev.getService(AVTransport.SERVICE_TYPE);
                if (conDir == null)
                    return;
                Action action = conDir.getAction(AVTransport.GETPOSITIONINFO);
                if (action == null)
                    return;

                action.setArgumentValue(AVTransport.INSTANCEID, "0");
                if (action.postControlAction())
                    return;

                Argument track = action.getArgument(AVTransport.TRACK);
                if (track == null)
                    return;

                String trackStr = track.getValue();
                if (!TextUtils.isEmpty(trackStr))
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
