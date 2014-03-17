package com.iac.dlnaproject;

import com.iac.dlnaproject.NetworkStatusReceiver.OnNetworkChangedListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MediaControllerService extends Service {

    private static final String TAG = "MediaControllerService";

    public static final String SEARCH_DEVICES = "com.iac.dlnaproject.search_device";
    public static final String RESET_SEARCH_DEVICES = "com.iac.dlnaproject.reset_search_device";

    private IACMediaController mMediaController;
    private ControlPointRefreshTask mRefreshTask;
    private OnNetworkChangedListener mOnNetworkChangedListener;
    private ControllerProxy mControllerProxy;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null){
            String action = intent.getAction();
            if (SEARCH_DEVICES.equals(action)) {
                startEngine();
            }else if (RESET_SEARCH_DEVICES.equals(action)){
                restartEngine();
            }
        }else{
            Log.i(TAG, "intent = " + intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        mControllerProxy = ControllerProxy.getInstance();
        mMediaController = new IACMediaController();
        mRefreshTask = new ControlPointRefreshTask(this, mMediaController);

        mOnNetworkChangedListener = new OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged() {
                restartEngine();
            }
        };
        mControllerProxy.setControlPoint(mMediaController);
        mControllerProxy.addOnNetworkChangedListener(mOnNetworkChangedListener);
        startEngine();
    }

    public boolean startEngine() {
        if (mRefreshTask.isAlive()) {
            mRefreshTask.awakeThread();
        } else {
            mRefreshTask.start();
        }
        return true;
    }

    public boolean stopEngine() {
        if (mRefreshTask != null && mRefreshTask.isAlive()) {
            mRefreshTask.exit();
            while (mRefreshTask.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mRefreshTask = null;
        }
        return true;
    }

    public boolean restartEngine() {
        mRefreshTask.reset();
        return true;
    }

    @Override
    public void onDestroy() {
        stopEngine();
        mControllerProxy.setControlPoint(null);
        mControllerProxy.removeOnNetworkChangedListener(mOnNetworkChangedListener);
        super.onDestroy();
    }
}
