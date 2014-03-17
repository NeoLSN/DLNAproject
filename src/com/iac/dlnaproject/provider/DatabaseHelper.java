package com.iac.dlnaproject.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "iacdlna.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE media_item "
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, parent_id TEXT, object_id TEXT, title TEXT, artist TEXT, album TEXT, objectClass TEXT, albumArtUri TEXT, date TEXT, RESTRICTED TEXT);");
        db.execSQL("CREATE TABLE play_queue"
                + " (_id INTEGER PRIMARY KEY,"
                + "item_id INTEGER NOT NULL REFERENCES media_item(_id) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "play_order INTEGER);");
        db.execSQL("CREATE TABLE res_info "
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, media_id INTEGER NOT NULL REFERENCES media_item(_id) ON UPDATE CASCADE ON DELETE CASCADE, protocolInfo TEXT, resolution TEXT, size INTEGER, res TEXT NOT NULL, duration TEXT);");
        db.execSQL("CREATE VIEW play_queue_view AS SELECT play_queue.*, media_item.* FROM play_queue, media_item ON (play_queue.item_id = media_item._id);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

}
