/******************************************************************
 *
 *	MediaServer for CyberLink
 *
 *	Copyright (C) Satoshi Konno 2003-2004
 *
 *	File : ConnectionInfo.java
 *
 *	Revision:
 *
 *	02/22/08
 *		- first revision.
 *
 ******************************************************************/

package org.cybergarage.upnp.std.av.data;

import java.util.ArrayList;
import java.util.List;


public class AVTransportInfo
{
    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    public AVTransportInfo()
    {
        /*Jason*/
        this(0, "", "");
        /*Jason*/
    }

    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    private int instanceID;

    public int getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(int instanceID) {
        this.instanceID = instanceID;
    }

    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    private String uri;

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        /*Jason*/
        if (uri == null || uri.isEmpty()) throw new IllegalArgumentException();
        /*Jason*/
        this.uri = uri;
    }

    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    private String uriMetaData;

    public String getURIMetaData() {
        return uriMetaData;
    }

    public void setURIMetaData(String uriMetaData) {
        this.uriMetaData = uriMetaData;
        /*Jason*/
        parseMetaData();
        /*Jason*/
    }

    /*Jason*/
    public AVTransportInfo(int instanceId, String uri, String metaData) {
        setInstanceID(instanceId);
        setURI(uri);
        setURIMetaData(metaData);
        mPlaylist = new ArrayList<Track>();
    }

    private void parseMetaData() {
        String metaData = getURIMetaData();
        if (metaData != null && !metaData.equals("")) {
            // TODO: parse meta data for nrTracks & media duration
        } else {
            mPlaylist.add(new Track(uri));
        }
    }

    private List<Track> mPlaylist;
    private int mCurrentTrackPosition = 0;

    public int getNrTracks() {
        return mPlaylist.size();
    }

    public List<Track> getPlaylist() {
        return mPlaylist;
    }

    public String getMediaDuration() {
        long mediaDuration = 0;
        return String.valueOf(mediaDuration);// must translate to "000:00:00"
    }

    public Track getCurrentTrack() {
        return mPlaylist.get(mCurrentTrackPosition);
    }

    public int getCurrentTrackPosition() {
        return mCurrentTrackPosition;
    }

    public Track next() {
        if (++mCurrentTrackPosition >= getNrTracks())
            mCurrentTrackPosition--;
        return mPlaylist.get(mCurrentTrackPosition);
    }

    public Track previous() {
        if (--mCurrentTrackPosition < 0)
            mCurrentTrackPosition = 0;
        return mPlaylist.get(mCurrentTrackPosition);
    }

    public static class Track {
        public int duration;
        public String metaData;
        public String uri;

        public Track() {}
        public Track(String uri) {
            this.uri = uri;
        }
    }
    /*Jason*/
}

