
package com.iac.dlnaproject;

import com.iac.dlnaproject.NetworkStatusReceiver.OnNetworkChangedListener;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayingMonitorService extends Service {

    private static final String TAG = "MediaPlayingMonitorService";

    private IACMediaController mMediaController;
    private MediaPlayingMonitorTask mRefreshTask;
    private OnNetworkChangedListener mOnNetworkChangedListener;
    private ControllerProxy mControllerProxy;

    private List<Messenger> mClients = new ArrayList<Messenger>();
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_UPDATE_VIEW = 3;
    public static final int MSG_START_ENGINE = 4;
    public static final int MSG_STOP_EIGINE = 5;

    public static final String START_MONITOR = "com.iac.dlnaproject.start_monitor";
    public static final String STOP_MONITOR = "com.iac.dlnaproject.stop_monitor";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null){
            String action = intent.getAction();
            if (START_MONITOR.equals(action)) {
                startEngine();
            }else if (STOP_MONITOR.equals(action)){
                restartEngine();
            }
        }else{
            Log.i(TAG, "intent = " + intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mControllerProxy = ControllerProxy.getInstance();
        mMediaController = mControllerProxy.getControlPoint();
        mRefreshTask = new MediaPlayingMonitorTask(this, mMediaController);

        mOnNetworkChangedListener = new OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged() {
                restartEngine();
            }
        };

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
        mMediaController = mControllerProxy.getControlPoint();
        mRefreshTask.resetControlPoint(mMediaController);
        mRefreshTask.reset();
        return true;
    }

    @Override
    public void onDestroy() {
        stopEngine();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_UPDATE_VIEW:
                    for (int i = mClients.size() - 1; i >= 0; i--) {
                        try {
                            // TODO: send update message
                            mClients.get(i).send(Message.obtain(null, MSG_UPDATE_VIEW, 0, 0, null));
                        } catch (RemoteException e) {
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
