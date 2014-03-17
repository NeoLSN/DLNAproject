package com.iac.dlnaproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

public class NetworkStatusReceiver extends BroadcastReceiver {

    public static interface OnNetworkChangedListener {
        public void onNetworkChanged();
    }

    private List<OnNetworkChangedListener> listeners = new ArrayList<OnNetworkChangedListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (ConnectivityManager.CONNECTIVITY_ACTION
                        .equalsIgnoreCase(action)) {
                    for (OnNetworkChangedListener listener : listeners) {
                        listener.onNetworkChanged();
                    }
                }
            }
        }
    }

    public void addOnNetworkChangedListener(OnNetworkChangedListener listener) {
        listeners.remove(listener);
        listeners.add(listener);
    }

    public void removeOnNetworkChangedListener(OnNetworkChangedListener listener) {
        listeners.remove(listener);
    }

}
