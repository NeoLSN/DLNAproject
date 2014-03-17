package com.iac.dlnaproject.model;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.Log;

public class UIEventHelper {
    private static final UriMatcher sUriMatcher;

    public static final int REQUEST_FRAGMENT_DLNA_HOME = 0;
    public static final int REQUEST_FRAGMENT_IAC_CLOUD = 1;

    public static final int ON_BACK_PRESSED = 50;
    public static final int ITEM_SELECTED = 1000;
    public static final int ITEM_SELECTED_AND_COMPLETED = 2000;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(UIEvent.AUTHORITY, UIEvent.TYPE_DLNA_HOME, REQUEST_FRAGMENT_DLNA_HOME);
        sUriMatcher.addURI(UIEvent.AUTHORITY, UIEvent.TYPE_IAC_CLOUD, REQUEST_FRAGMENT_IAC_CLOUD);

        sUriMatcher.addURI(UIEvent.AUTHORITY, UIEvent.TYPE_ON_BACK_PRESSED, ON_BACK_PRESSED);
        sUriMatcher.addURI(UIEvent.AUTHORITY, UIEvent.TYPE_ITEM_SELECTED, ITEM_SELECTED);
        sUriMatcher.addURI(UIEvent.AUTHORITY, UIEvent.TYPE_ITEM_SELECTED_AND_COMPLETED,
                ITEM_SELECTED_AND_COMPLETED);
    }

    public static Uri getAuthorityURI() {
        return Uri.parse("content://" + UIEvent.AUTHORITY + "/");
    }

    private static int match(Uri uri) {
        Log.i("Jason", uri + "");
        return sUriMatcher.match(uri);
    }

    public static int match(UIEvent event) {
        Uri action = event.getAction();
        return match(action);
    }
}
