
package com.iac.dlnaproject.provider;

import android.net.Uri;

public class PlayQueueMetaData {

    public static final String TABLE_NAME = "play_queue";

    public static final String _ID = "_id";
    public static final String ITEM_ID = "item_id";
    public static final String PLAYER_ORDER = "play_order";

    public static final Uri CONTENT_URI  =
            Uri.parse("content://" + IACContentProvider.AUTHORITY + "/play_queue");

}
