
package com.iac.dlnaproject.model;

import android.net.Uri;

public class UIEvent {

    public static final String AUTHORITY = "com.iac.dlnaproject";

    public static final String TYPE_DLNA_HOME = "dlna_home";
    public static final String TYPE_IAC_CLOUD = "iac_cloud";

    public static final String TYPE_ITEM_SELECTED = "item_selected";
    public static final String TYPE_ITEM_SELECTED_AND_COMPLETED = "item_selected_and_completed";
    public static final String TYPE_ON_BACK_PRESSED = "on_back_pressed";

    private Uri action;
    private int arg1;
    private Object object;

    private static Uri buildActionUri(String str) {
        return Uri.parse(UIEventHelper.getAuthorityURI() + str);
    }

    public static UIEvent create(String action) {
        Uri uri = buildActionUri(action);
        return new UIEvent(uri);
    }

    private UIEvent(Uri action) {
        setAction(action);
    }

    public UIEvent() {
    }

    public Uri getAction() {
        return action;
    }

    public void setAction(Uri action) {
        this.action = action;
    }

    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
