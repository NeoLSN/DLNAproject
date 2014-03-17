/******************************************************************
 *
 *	MediaServer for CyberLink
 *
 *	Copyright (C) Satoshi Konno 2003-2004
 *
 *	File: ConnectionInfoList.java
 *
 *	Revision;
 *
 *	02/22/08
 *		- first revision.
 *
 ******************************************************************/

package org.cybergarage.upnp.std.av.data;


import java.util.Vector;

public class AVTransportInfoList extends Vector
{
    ////////////////////////////////////////////////
    // Constrictor
    ////////////////////////////////////////////////

    public AVTransportInfoList()
    {
    }

    ////////////////////////////////////////////////
    // getConnectionInfo
    ////////////////////////////////////////////////

    public AVTransportInfo getAVTransportInfo(int n)
    {
        return (AVTransportInfo)get(n);
    }

    /*Jason*/
    public synchronized void setCurrentAvTransInfo(AVTransportInfo avTransInfo) {
        if (size() >= 1)
            remove(0);
        insertElementAt(avTransInfo, 0);
    }

    public synchronized AVTransportInfo getCurrentAvTransInfo() {
        AVTransportInfo avTransInfo = null;
        if (size() < 1)
            return null;
        avTransInfo = getAVTransportInfo(0);
        return avTransInfo;
    }

    public synchronized void setNextAvTransInfo(AVTransportInfo avTransInfo) {
        if (size() >= 2)
            remove(0);
        insertElementAt(avTransInfo, 1);
    }

    public synchronized AVTransportInfo getNextAvTransInfo() {
        AVTransportInfo avTransInfo = null;
        if (size() < 2)
            return null;
        avTransInfo = getAVTransportInfo(1);
        return avTransInfo;
    }

    public synchronized AVTransportInfo getAVTransportInfoById(int instanceId) {
        AVTransportInfo avTransInfo = null;
        int avTransInfoCnt = size();
        for (int n=0; n<avTransInfoCnt; n++) {
            AVTransportInfo tempAvTransInfo = getAVTransportInfo(n);
            if (tempAvTransInfo.getInstanceID() == instanceId) {
                avTransInfo = tempAvTransInfo;
                break;
            }
        }
        return avTransInfo;
    }
    /*Jason*/
}

