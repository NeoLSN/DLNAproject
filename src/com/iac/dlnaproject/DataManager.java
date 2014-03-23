
package com.iac.dlnaproject;

import android.content.Context;

public class DataManager {

    private static DataManager sInstance;

    private Context mContext;
    private PlayQueue playQueue;

    public static void init(Context context) {
        if (sInstance == null)
            sInstance = new DataManager(context);
    }

    public static void terminate() {
        sInstance.playQueue.clear();
        sInstance = null;
    }

    public static DataManager getInstance() {
        return sInstance;
    }

    private DataManager(Context context) {
        this.mContext = context;
        this.playQueue = new PlayQueue();
    }

    public PlayQueue getPlayQueue() {
        return playQueue;
    }

}
