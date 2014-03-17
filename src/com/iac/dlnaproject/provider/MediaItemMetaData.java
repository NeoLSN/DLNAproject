
package com.iac.dlnaproject.provider;

import android.net.Uri;

public class MediaItemMetaData {

    public static final String TABLE_NAME = "media_item";

    public static final Uri CONTENT_URI = Uri.parse("content://" + IACContentProvider.AUTHORITY
            + "/media_item");

    public static final String _ID = "_id";
    public static final String PARENT_ID = "parent_id";
    public static final String OBJECT_ID = "object_id";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String OBJECT_CLASS = "objectClass";
    public static final String ALBUM_ART_URI = "albumArtUri";
    public static final String DATE = "date";
    public static final String RESTRICTED = "restricted";

    public static class ResMetaData {

        public static final String TABLE_NAME = "res_info";

        public static final Uri CONTENT_URI = Uri.parse("content://" + IACContentProvider.AUTHORITY
                + "/res_info");

        public static final String _ID = "_id";
        public static final String MEDIA_ID = "media_id";
        public static final String PROTOCOL_INFO = "protocolInfo";
        public static final String RESOLUTION = "resolution";
        public static final String SIZE = "size";
        public static final String RES = "res";
        public static final String DURATION = "duration";
    }
}
