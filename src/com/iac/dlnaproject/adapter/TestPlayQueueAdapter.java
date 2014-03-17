package com.iac.dlnaproject.adapter;

import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import android.content.Context;
import android.database.Cursor;

public class TestPlayQueueAdapter extends SimpleDragSortCursorAdapter {

    public TestPlayQueueAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
            int flags) {
        super(context, layout, c, from, to, flags);
    }

}
