
package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.constant.DC;
import com.iac.dlnaproject.constant.DIDLLite;
import com.iac.dlnaproject.constant.UPnP;

import org.cybergarage.xml.Attribute;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.XML;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MediaItem implements Item, Parcelable {
    public final static String DLNA_OBJECTCLASS_MUSICID = "object.item.audioItem";
    public final static String DLNA_OBJECTCLASS_VIDEOID = "object.item.videoItem";
    public final static String DLNA_OBJECTCLASS_PHOTOID = "object.item.imageItem";

    public static boolean isAudioItem(MediaItem item) {
        String objectClass = item.getObjectClass();
        if (objectClass != null && objectClass.contains(DLNA_OBJECTCLASS_MUSICID)) {
            return true;
        }
        return false;
    }

    public static boolean isVideoItem(MediaItem item) {
        String objectClass = item.getObjectClass();
        if (objectClass != null && objectClass.contains(DLNA_OBJECTCLASS_VIDEOID)) {
            return true;
        }
        return false;
    }

    public static boolean isPictureItem(MediaItem item) {
        String objectClass = item.getObjectClass();
        if (objectClass != null && objectClass.contains(DLNA_OBJECTCLASS_PHOTOID)) {
            return true;
        }
        return false;
    }

    public static class ResInfo implements Parcelable {
        public String protocolInfo;
        public String resolution;
        public long size;
        public String res;
        public int duration;

        public ResInfo() {
            protocolInfo = "";
            resolution = "";
            size = 0;
            res = "";
            duration = 0;
        }

        public ResInfo(Parcel in) {
            protocolInfo = in.readString();
            resolution = in.readString();
            size = in.readLong();
            res = in.readString();
            duration = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(protocolInfo);
            dest.writeString(resolution);
            dest.writeLong(size);
            dest.writeString(res);
            dest.writeInt(duration);
        }

        public static final Parcelable.Creator<ResInfo> CREATOR = new Parcelable.Creator<ResInfo>() {
            @Override
            public ResInfo createFromParcel(Parcel in) {
                return new ResInfo(in);
            }

            @Override
            public ResInfo[] newArray(int size) {
                return new ResInfo[size];
            }
        };
    }

    private String parentId = "";
    private String id = "";
    private String title = "";
    private String artist = "";
    private String album = "";
    private String objectClass = "";
    private String albumArtUri = "";
    // private long date = 0;
    private String date = "";
    private String restricted = "";
    private ResInfo res = null;

    private Node node;

    public MediaItem() {

    }

    public MediaItem(JSONObject jsonObject) {

    }

    public MediaItem(Node node) {
        if (node.getName().equals("item")) {

            this.node = node;

            int attrLength = node.getNAttributes();
            for (int i = 0; i < attrLength; i++) {
                Attribute attr = node.getAttribute(i);
                if (attr.getName().equals(DIDLLite.ID)) {
                    id = attr.getValue();
                } else if (attr.getName().equals("parentID")) {
                    parentId = attr.getValue();
                }
            }

            List<MediaItem.ResInfo> resList = new ArrayList<MediaItem.ResInfo>();
            int length = node.getNNodes();
            for (int l = 0; l < length; l++) {
                Node childNode = node.getNode(l);

                if (childNode.getName().equals(DC.TITLE)) {
                    title = childNode.getValue();
                } else if (childNode.getName().equals(DC.DATE)) {
                    date = childNode.getValue();
                } else if (childNode.getName().equals(UPnP.ARTIST)) {
                    artist = childNode.getValue();
                } else if (childNode.getName().equals(UPnP.ALBUM)) {
                    album = childNode.getValue();
                } else if (childNode.getName().equals(UPnP.CLASS)) {
                    objectClass = childNode.getValue();
                } else if (childNode.getName().equals(DIDLLite.RES)) {
                    MediaItem.ResInfo resInfo = getResInfo(childNode);
                    if (resInfo != null) {
                        resList.add(resInfo);
                    }
                } else if (childNode.getName().equals(UPnP.ALBUMART_URI)
                        || childNode.getName().equals(UPnP.ICON)) {
                    albumArtUri = childNode.getValue();
                }
            }
            res = getBestResInfo(resList);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private MediaItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist= in.readString();
        album= in.readString();
        albumArtUri= in.readString();
        objectClass= in.readString();
        date= in.readString();
        parentId= in.readString();
        restricted = in.readString();
        res = in.readParcelable(ResInfo.class.getClassLoader());
    }

    private ResInfo getResInfo(Node node) {
        if (node == null || !node.getName().equals(DIDLLite.RES)) {
            return null;
        }

        MediaItem.ResInfo resInfo = new MediaItem.ResInfo();

        String res = node.getValue();
        if (res != null) {
            resInfo.res = res;
        }

        int length = node.getNAttributes();
        for (int i = 0; i < length; i++) {
            Attribute attr = node.getAttribute(i);
            if (attr.getName().equals("duration")) {
                resInfo.duration = 0;
                String durationString = attr.getValue();
                if (durationString == null || durationString.length() == 0) {
                    continue;
                }
                try {
                    String sArray[] = durationString.split(":");
                    double hour = Double.valueOf(sArray[0]);
                    double minute = Double.valueOf(sArray[1]);
                    double second = Double.valueOf(sArray[2]);
                    resInfo.duration = (int)((hour * 60 + minute) * 60 + second) * 1000;
                } catch (Exception e) {
                    resInfo.duration = 0;
                }
            } else if (attr.getName().equals("resolution")) {
                resInfo.resolution = attr.getValue();
            } else if (attr.getName().equals("size")) {
                resInfo.size = Integer.valueOf(attr.getValue());
            } else if (attr.getName().equals("protocolInfo")) {
                resInfo.protocolInfo = attr.getValue();
            }
        }

        return resInfo;
    }

    private MediaItem.ResInfo getBestResInfo(List<MediaItem.ResInfo> resList) {

        if (objectClass == null || resList == null || resList.size() == 0) {
            return null;
        }

        if (!objectClass.contains(DLNA_OBJECTCLASS_PHOTOID)) {
            return resList.get(0);
        }

        int maxIndex = 0;
        int size = resList.size();
        for (int i = 1; i < size; i++) {
            boolean ret = compareBetweenResolution(resList.get(maxIndex).resolution,
                    resList.get(i).resolution);
            if (!ret) {
                maxIndex = i;
            }
        }

        return resList.get(maxIndex);
    }

    private boolean compareBetweenResolution(String resolution1, String resolution2) {

        int resolutionInt1 = formatResolution(resolution1);
        int resolutionInt2 = formatResolution(resolution2);

        return resolutionInt1 >= resolutionInt2 ? true : false;
    }

    private int formatResolution(String resolutionString) {
        int value = 0;
        if (resolutionString == null || resolutionString.length() == 0) {
            return value;
        }

        try {
            String array[] = resolutionString.split("x");
            int v1 = Integer.valueOf(array[0]);
            int v2 = Integer.valueOf(array[1]);

            value = v1 * v2;
        } catch (Exception e) {

        }

        return value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumarturi(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    @Override
    public String getObjectClass() {
        return objectClass;
    }

    @Override
    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getRestricted() {
        return restricted;
    }

    @Override
    public void setRestricted(String restricted) {
        this.restricted = restricted;
    }

    public ResInfo getRes() {
        return res;
    }

    public void setRes(ResInfo res) {
        this.res = res;
    }

    @Override
    public Node getNode() {
        return node;
    }

    public String getURIMetadata() {
        if (getNode() != null) {
            return getNode().toString();
        } else {
            StringBuilder sb = new StringBuilder("<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:r=\"urn:schemas-rinconnetworks-com:metadata-1-0/\" xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\">");
            String id = this.id;
            String parentId = this.parentId;
            if (id == null) id = "-1";
            if (parentId == null) parentId = "-1";

            sb.append("<item id=\"").append(id).append("\" parentID=\"").append(parentId).append("\" restricted=\"").append(restricted).append("\">");
            sb.append("<dc:title>").append(XML.escapeXMLChars(title)).append("</dc:title>");
            sb.append("<upnp:class>").append(objectClass).append("</upnp:class>");
            /*if (encodedUri != null && encodedUri.startsWith("x-sonosapi-stream:")) {
            sb.append("<desc id=\"cdudn\" nameSpace=\"urn:schemas-rinconnetworks-com:metadata-1-0/\">SA_RINCON65031_</desc>");
            } else if (cdudn != null) {
                sb.append("<desc id=\"cdudn\" nameSpace=\"urn:schemas-rinconnetworks-com:metadata-1-0/\">").append(cdudn).append("</desc>");
            } else {
                sb.append("<desc id=\"cdudn\">RINCON_AssociatedZPUDN</desc>");
            }*/
            if (albumArtUri != null) {
                sb.append("<upnp:albumArtURI>").append(XML.escapeXMLChars(albumArtUri)).append("</upnp:albumArtURI>");
            }
            sb.append("</item>");
            sb.append("</DIDL-Lite>");
            return sb.toString();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(artist);
        out.writeString(album);
        out.writeString(albumArtUri);
        out.writeString(objectClass);
        out.writeString(date);
        out.writeString(parentId);
        out.writeString(restricted);
        out.writeParcelable(getRes(), 0);
    }

    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
}
