/******************************************************************
 *
 *	MediaServer for CyberLink
 *
 *	Copyright (C) Satoshi Konno 2003
 *
 *	File : DIDLLite.java
 *
 *	Revision:
 *
 *	10/22/03
 *		- first revision.
 *	10/26/04
 *		- Brent Hills <bhills@openshores.com>
 *		- Removed a SOAP header from output().
 *	10/28/04
 *		- Brent Hills <bhills@openshores.com>
 *		- Removed a SOAP header from output().
 *	04/18/05
 *		- Matt <matthias@streams.ch>
 *		- Changed toString() using UTF-8 OutputStreamWriter.
 *
 ******************************************************************/

package com.iac.dlnaproject.constant;


public class DIDLLite
{
    ////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////

    public final static String NAME = "DIDL-Lite";
    public final static String XMLNS = "xmlns";
    public final static String XMLNS_URL = "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/";
    public final static String XMLNS_DC = "xmlns:dc";
    public final static String XMLNS_DC_URL = "http://purl.org/dc/elements/1.1/";
    public final static String XMLNS_UPNP = "xmlns:upnp";
    public final static String XMLNS_UPNP_URL = "urn:schemas-upnp-org:metadata-1-0/upnp/";

    public final static String CONTAINER = "container";
    public final static String ID = "id";
    public final static String SEARCHABLE = "searchable";
    public final static String PARENTID = "parentID";
    public final static String RESTICTED = "restricted";

    public final static String OBJECT_CONTAINER = "object.container";

    public final static String RES = "res";
    public final static String RES_PROTOCOLINFO = "protocolInfo";

}

