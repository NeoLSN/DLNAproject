
package com.iac.dlnaproject.provider;

import com.iac.dlnaproject.provider.MediaItemMetaData.ResMetaData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore.Audio.Playlists;
import android.text.TextUtils;
import android.util.Log;

public class IACContentProvider extends ContentProvider {
    private static DatabaseHelper dbh;

    public static final String AUTHORITY = "com.iac.dlnaproject";

    private static final int PLAY_QUEUE = 0;
    private static final int PLAY_QUEUE_ITEM = 1;
    private static final int MEDIA_ITEM = 2;
    private static final int MEDIA_ITEM_ID = 3;
    private static final int RES_INFO = 4;
    private static final int RES_INFO_ID = 5;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "play_queue", PLAY_QUEUE);
        sUriMatcher.addURI(AUTHORITY, "play_queue/#", PLAY_QUEUE_ITEM);
        sUriMatcher.addURI(AUTHORITY, "media_item", MEDIA_ITEM);
        sUriMatcher.addURI(AUTHORITY, "media_item/#", MEDIA_ITEM_ID);
        sUriMatcher.addURI(AUTHORITY, "res_info", RES_INFO);
        sUriMatcher.addURI(AUTHORITY, "res_info/#", RES_INFO_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int count;
        String segment1;
        switch (sUriMatcher.match(uri)) {
            case PLAY_QUEUE:
                count = db.delete(PlayQueueMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case PLAY_QUEUE_ITEM:
                segment1 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    selection = PlayQueueMetaData._ID + "=" + segment1;
                } else {
                    selection = PlayQueueMetaData._ID + "=" + segment1 + " AND (" + selection
                            + ")";
                }
                count = db.delete(PlayQueueMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDIA_ITEM:
                segment1 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    selection = MediaItemMetaData._ID + "=" + segment1;
                } else {
                    selection = MediaItemMetaData._ID + "=" + segment1 + " AND (" + selection + ")";
                }
                count = db.delete(MediaItemMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAY_QUEUE:
                return Playlists.CONTENT_TYPE;
            case PLAY_QUEUE_ITEM:
                return Playlists.ENTRY_CONTENT_TYPE;
            case MEDIA_ITEM:
                return Playlists.ENTRY_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues givinValues) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        final ContentValues values = new ContentValues(givinValues);

        long rowId = 0;
        switch (sUriMatcher.match(uri)) {
            case PLAY_QUEUE:
                rowId = db.insert(PlayQueueMetaData.TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri playlistUri = ContentUris.withAppendedId(PlayQueueMetaData.CONTENT_URI,
                            rowId);
                    getContext().getContentResolver().notifyChange(playlistUri, null);
                    return playlistUri;
                }
                throw new SQLException("Failed to insert row into" + uri);
            case MEDIA_ITEM:
                long now = Long.valueOf(System.currentTimeMillis());
                if (values.containsKey(MediaItemMetaData.DATE) == false) {
                    values.put(MediaItemMetaData.DATE, now);
                }
                if (values.containsKey(MediaItemMetaData.TITLE) == false) {
                    Resources r = Resources.getSystem();
                    values.put(MediaItemMetaData.TITLE, r.getString(android.R.string.unknownName));
                }
                rowId = db.insert(MediaItemMetaData.TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri playlistUri = ContentUris.withAppendedId(MediaItemMetaData.CONTENT_URI,
                            rowId);
                    getContext().getContentResolver().notifyChange(playlistUri, null);
                    return playlistUri;
                }
                throw new SQLException("Failed to insert row into" + uri);
            case RES_INFO:
                if (values.containsKey(ResMetaData.RES) == false) {
                    throw new SQLException("res can NOT be null");
                }
                rowId = db.insert(ResMetaData.TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri playlistUri = ContentUris.withAppendedId(ResMetaData.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(playlistUri, null);
                    return playlistUri;
                }
                throw new SQLException("Failed to insert row into" + uri);
            default:
                throw new UnsupportedOperationException("Cannot update URL: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        dbh = new DatabaseHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c;
        String segment1;
        switch (sUriMatcher.match(uri)) {
            case PLAY_QUEUE:
                c = db.query("play_queue_view", projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case PLAY_QUEUE_ITEM:
                segment1 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    selection = PlayQueueMetaData._ID + "=" + segment1;
                } else {
                    selection = PlayQueueMetaData._ID + "=" + segment1 + " AND (" + selection + ")";
                }
                c = db.query("play_queue_view", projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case MEDIA_ITEM:
                c = db.query(MediaItemMetaData.TABLE_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case MEDIA_ITEM_ID:
                segment1 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    selection = MediaItemMetaData._ID + "=" + segment1;
                } else {
                    selection = MediaItemMetaData._ID + "=" + segment1 + " AND (" + selection + ")";
                }
                c = db.query(MediaItemMetaData.TABLE_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case RES_INFO:
                c = db.query(ResMetaData.TABLE_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case RES_INFO_ID:
                segment1 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    selection = ResMetaData._ID + "=" + segment1;
                } else {
                    selection = ResMetaData._ID + "=" + segment1 + " AND (" + selection + ")";
                }
                c = db.query(ResMetaData.TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        if (c == null) {
            Log.i("IACContentProvider", "query: failed");
        } else {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int count;
        long rowId = 0;
        final String segment;
        switch (sUriMatcher.match(uri)) {
            case PLAY_QUEUE_ITEM:
                segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update(PlayQueueMetaData.TABLE_NAME, values, "_id=" + rowId, null);
                break;
            case MEDIA_ITEM_ID:
                segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update(MediaItemMetaData.TABLE_NAME, values, "_id=" + rowId, null);
                break;
            case RES_INFO_ID:
                segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update(ResMetaData.TABLE_NAME, values, "_id=" + rowId, null);
                break;
            default:
                throw new UnsupportedOperationException("Cannot update URL: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
