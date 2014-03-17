
package com.iac.dlnaproject;

import android.app.Application;
import android.content.Intent;

public class DLNAapp extends Application {
    public static final String TAG = DLNAapp.class.toString();
    private static DLNAapp sDLNAapp = null;

    private ControllerProxy mControllerProxy;

    @Override
    public void onCreate() {
        super.onCreate();
        sDLNAapp = this;

        // init
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mControllerProxy = ControllerProxy.init(this);
    }

    @Override
    public void onTerminate() {
        mControllerProxy.terminate();
        stopService(new Intent(this,
                MediaControllerService.class));
        super.onTerminate();
    }

    public static synchronized DLNAapp getApplication() {
        return sDLNAapp;
    }
}
