/******************************************************************
 *
 *	MediaServer for CyberLink
 *
 *	Copyright (C) Satoshi Konno 2003
 *
 *	File : AVTransport.java
 *
 *	Revision:
 *
 *	02/22/08
 *		- first revision.
 *
 ******************************************************************/

package org.cybergarage.upnp.std.av.renderer;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceStateTable;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.std.AbstractService;
import org.cybergarage.upnp.std.av.data.AVTransportInfo;
import org.cybergarage.upnp.std.av.data.AVTransportInfo.Track;
import org.cybergarage.upnp.std.av.data.AVTransportInfoList;
import org.cybergarage.xml.XML;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

/*Jason*/
public class AVTransport extends AbstractService implements Observer//implements ActionListener, QueryListener
/*Jason*/
{
    ////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////

    public final static String SERVICE_TYPE = "urn:schemas-upnp-org:service:AVTransport:1";

    // Browse Action

    public final static String TRANSPORTSTATE = "TransportState";
    public final static String TRANSPORTSTATUS = "TransportStatus";
    public final static String PLAYBACKSTORAGEMEDIUM = "PlaybackStorageMedium";
    public final static String RECORDSTORAGEMEDIUM = "RecordStorageMedium";
    public final static String POSSIBLEPLAYBACKSTORAGEMEDIA = "PossiblePlaybackStorageMedia";
    public final static String POSSIBLERECORDSTORAGEMEDIA = "PossibleRecordStorageMedia";
    public final static String CURRENTPLAYMODE = "CurrentPlayMode";
    public final static String TRANSPORTPLAYSPEED = "TransportPlaySpeed";
    public final static String RECORDMEDIUMWRITESTATUS = "RecordMediumWriteStatus";
    public final static String CURRENTRECORDQUALITYMODE = "CurrentRecordQualityMode";
    public final static String POSSIBLERECORDQUALITYMODES = "PossibleRecordQualityModes";
    public final static String NUMBEROFTRACKS = "NumberOfTracks";
    public final static String CURRENTTRACK = "CurrentTrack";
    public final static String CURRENTTRACKDURATION = "CurrentTrackDuration";
    public final static String CURRENTMEDIADURATION = "CurrentMediaDuration";
    public final static String CURRENTTRACKMETADATA = "CurrentTrackMetaData";
    public final static String CURRENTTRACKURI = "CurrentTrackURI";
    public final static String AVTRANSPORTURI = "AVTransportURI";
    public final static String AVTRANSPORTURIMETADATA = "AVTransportURIMetaData";
    public final static String NEXTAVTRANSPORTURI = "NextAVTransportURI";
    public final static String NEXTAVTRANSPORTURIMETADATA = "NextAVTransportURIMetaData";
    public final static String RELATIVETIMEPOSITION = "RelativeTimePosition";
    public final static String ABSOLUTETIMEPOSITION = "AbsoluteTimePosition";
    public final static String RELATIVECOUNTERPOSITION = "RelativeCounterPosition";
    public final static String ABSOLUTECOUNTERPOSITION = "AbsoluteCounterPosition";
    public final static String CURRENTTRANSPORTACTIONS = "CurrentTransportActions";
    public final static String LASTCHANGE = "LastChange";
    public final static String SETAVTRANSPORTURI = "SetAVTransportURI";
    public final static String INSTANCEID = "InstanceID";
    public final static String CURRENTURI = "CurrentURI";
    public final static String CURRENTURIMETADATA = "CurrentURIMetaData";
    public final static String SETNEXTAVTRANSPORTURI = "SetNextAVTransportURI";
    public final static String NEXTURI = "NextURI";
    public final static String NEXTURIMETADATA = "NextURIMetaData";
    public final static String GETMEDIAINFO = "GetMediaInfo";
    public final static String NRTRACKS = "NrTracks";
    public final static String MEDIADURATION = "MediaDuration";
    public final static String PLAYMEDIUM = "PlayMedium";
    public final static String RECORDMEDIUM = "RecordMedium";
    public final static String WRITESTATUS = "WriteStatus";
    public final static String GETTRANSPORTINFO = "GetTransportInfo";
    public final static String CURRENTTRANSPORTSTATE = "CurrentTransportState";
    public final static String CURRENTTRANSPORTSTATUS = "CurrentTransportStatus";
    public final static String CURRENTSPEED = "CurrentSpeed";
    public final static String GETPOSITIONINFO = "GetPositionInfo";
    public final static String TRACK = "Track";
    public final static String TRACKDURATION = "TrackDuration";
    public final static String TRACKMETADATA = "TrackMetaData";
    public final static String TRACKURI = "TrackURI";
    public final static String RELTIME = "RelTime";
    public final static String ABSTIME = "AbsTime";
    public final static String RELCOUNT = "RelCount";
    public final static String ABSCOUNT = "AbsCount";
    public final static String GETDEVICECAPABILITIES = "GetDeviceCapabilities";
    public final static String PLAYMEDIA = "PlayMedia";
    public final static String RECMEDIA = "RecMedia";
    public final static String RECQUALITYMODES = "RecQualityModes";
    public final static String GETTRANSPORTSETTINGS = "GetTransportSettings";
    public final static String PLAYMODE = "PlayMode";
    public final static String RECQUALITYMODE = "RecQualityMode";
    public final static String STOP = "Stop";
    public final static String PLAY = "Play";
    public final static String SPEED = "Speed";
    public final static String PAUSE = "Pause";
    public final static String RECORD = "Record";
    public final static String SEEK = "Seek";
    public final static String UNIT = "Unit";
    public final static String TARGET = "Target";
    public final static String NEXT = "Next";
    public final static String PREVIOUS = "Previous";
    public final static String SETPLAYMODE = "SetPlayMode";
    public final static String NEWPLAYMODE = "NewPlayMode";
    public final static String SETRECORDQUALITYMODE = "SetRecordQualityMode";
    public final static String NEWRECORDQUALITYMODE = "NewRecordQualityMode";
    public final static String GETCURRENTTRANSPORTACTIONS = "GetCurrentTransportActions";
    public final static String ACTIONS = "Actions";

    public final static String STOPPED = "STOPPED";
    public final static String PLAYING = "PLAYING";
    public final static String OK = "OK";
    public final static String ERROR_OCCURRED = "ERROR_OCCURRED";
    public final static String NORMAL = "NORMAL";
    public final static String TRACK_NR = "TRACK_NR";
    /*Jason*/
    public final static String NONE = "NONE";
    public final static String NETWORK = "NETWORK";
    public final static String PAUSED_PLAYBACK = "PAUSED_PLAYBACK";
    public final static String NO_MEDIA_PRESENT = "NO_MEDIA_PRESENT";
    public final static String TRANSITIONING = "TRANSITIONING";
    /*Jason*/

    public final static String SCPD =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">\n" +
                    "   <specVersion>\n" +
                    "      <major>1</major>\n" +
                    "      <minor>0</minor>\n" +
                    "	</specVersion>\n" +
                    "    <serviceStateTable>"+
                    "        <stateVariable>"+
                    "            <name>TransportState</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "            <allowedValueList>"+
                    "                <allowedValue>STOPPED</allowedValue>"+
                    "                <allowedValue>PLAYING</allowedValue>"+
                    "                <allowedValue>PAUSED_PLAYBACK</allowedValue>"+
                    "                <allowedValue>TRANSITIONING</allowedValue>"+
                    "                <allowedValue>NO_MEDIA_PRESENT</allowedValue>"+
                    "            </allowedValueList>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>TransportStatus</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "            <allowedValueList>"+
                    "                <allowedValue>OK</allowedValue>"+
                    "                <allowedValue>ERROR_OCCURRED</allowedValue>           "+
                    "            </allowedValueList>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>PlaybackStorageMedium</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "	     <stateVariable>"+
                    "            <name>RecordStorageMedium</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "              </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>PossiblePlaybackStorageMedia</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>PossibleRecordStorageMedia</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>CurrentPlayMode</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "            <allowedValueList>"+
                    "                <allowedValue>NORMAL</allowedValue>"+
                    "            </allowedValueList>"+
                    "            <defaultValue>NORMAL</defaultValue>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>TransportPlaySpeed</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "	         <allowedValueList>"+
                    "                <allowedValue>1</allowedValue>"+
                    "            </allowedValueList>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <name>RecordMediumWriteStatus </name>"+
                    "            <dataType>string</dataType>"+
                    "         </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>CurrentRecordQualityMode</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "          </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>PossibleRecordQualityModes</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>NumberOfTracks</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>ui4</dataType>"+
                    "		     <allowedValueRange>"+
                    "			     <minimum>0</minimum>"+
                    "		     </allowedValueRange>"+
                    "         </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>CurrentTrack</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>ui4</dataType>"+
                    "		     <allowedValueRange>"+
                    "			    <minimum>0</minimum>"+
                    "			    <step>1</step>"+
                    "		     </allowedValueRange>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>CurrentTrackDuration</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "	     <stateVariable>"+
                    "            <name>CurrentMediaDuration</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>CurrentTrackMetaData</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>CurrentTrackURI</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>AVTransportURI</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>AVTransportURIMetaData</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>NextAVTransportURI</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>NextAVTransportURIMetaData</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>RelativeTimePosition</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>AbsoluteTimePosition</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>RelativeCounterPosition</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>i4</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>AbsoluteCounterPosition</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>i4</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "		<Optional/>"+
                    "            <name>CurrentTransportActions</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>LastChange</name>"+
                    "            <sendEventsAttribute>yes</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>A_ARG_TYPE_SeekMode</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "            <allowedValueList>"+
                    "                 <allowedValue>TRACK_NR</allowedValue>"+
                    "            </allowedValueList>"+
                    "            <defaultValue>TRACK_NR</defaultValue>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>A_ARG_TYPE_SeekTarget</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>string</dataType>"+
                    "        </stateVariable>"+
                    "        <stateVariable>"+
                    "            <name>A_ARG_TYPE_InstanceID</name>"+
                    "            <sendEventsAttribute>no</sendEventsAttribute>"+
                    "            <dataType>ui4</dataType>"+
                    "        </stateVariable>"+
                    "    </serviceStateTable>"+
                    "    <actionList>"+
                    "        <action>"+
                    "            <name>SetAVTransportURI</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>CurrentURI</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>AVTransportURI</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>CurrentURIMetaData</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>AVTransportURIMetaData</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>	<Optional/>"+
                    "            <name>SetNextAVTransportURI</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>NextURI</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>NextAVTransportURI</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>NextURIMetaData</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>NextAVTransportURIMetaData</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>GetMediaInfo</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                 <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>NrTracks</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>NumberOfTracks</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>MediaDuration</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentMediaDuration</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>CurrentURI</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>AVTransportURI</relatedStateVariable>"+
                    "                </argument>"+
                    "		         <argument>"+
                    "                    <name>CurrentURIMetaData</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>AVTransportURIMetaData</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>NextURI</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>NextAVTransportURI</relatedStateVariable>"+
                    "                </argument>"+
                    "		         <argument>"+
                    "                    <name>NextURIMetaData</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>NextAVTransportURIMetaData</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>PlayMedium</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>PlaybackStorageMedium</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>RecordMedium</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>RecordStorageMedium</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>WriteStatus</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>RecordMediumWriteStatus </relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>GetTransportInfo</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>CurrentTransportState</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>TransportState</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>CurrentTransportStatus</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>TransportStatus</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>CurrentSpeed</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>TransportPlaySpeed</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>GetPositionInfo</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>Track</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentTrack</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>TrackDuration</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentTrackDuration</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>TrackMetaData</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentTrackMetaData</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>TrackURI</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentTrackURI</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>RelTime</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>RelativeTimePosition</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>AbsTime</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>AbsoluteTimePosition</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>RelCount</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>RelativeCounterPosition</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>AbsCount</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>AbsoluteCounterPosition</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>GetDeviceCapabilities</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>PlayMedia</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>PossiblePlaybackStorageMedia</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>RecMedia</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>PossibleRecordStorageMedia</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>RecQualityModes</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>PossibleRecordQualityModes</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>GetTransportSettings</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>PlayMode</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentPlayMode</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>RecQualityMode</name>"+
                    "                    <direction>out</direction>" +
                    "                 <relatedStateVariable>CurrentRecordQualityMode</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>Stop</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>Play</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>Speed</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>TransportPlaySpeed</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>	<Optional/>"+
                    "            <name>Pause</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>	<Optional/>"+
                    "            <name>Record</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>Seek</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>Unit</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_SeekMode</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>Target</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_SeekTarget</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>Next</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>"+
                    "            <name>Previous</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>	<Optional/>"+
                    "            <name>SetPlayMode</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>NewPlayMode</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>CurrentPlayMode</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>	<Optional/>"+
                    "            <name>SetRecordQualityMode</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>NewRecordQualityMode</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>CurrentRecordQualityMode</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "        <action>	<Optional/>"+
                    "            <name>GetCurrentTransportActions</name>"+
                    "            <argumentList>"+
                    "                <argument>"+
                    "                    <name>InstanceID</name>"+
                    "                    <direction>in</direction>" +
                    "                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
                    "                </argument>"+
                    "                <argument>"+
                    "                    <name>Actions</name>"+
                    "                    <direction>out</direction>" +
                    "                    <relatedStateVariable>CurrentTransportActions</relatedStateVariable>"+
                    "                </argument>"+
                    "            </argumentList>"+
                    "        </action>"+
                    "    </actionList>"+
                    "</scpd>";

    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////
    /* Jason */
    public AVTransport(MediaRenderer render) throws InvalidDescriptionException
    {
        super(render);
        setMediaRenderer(render);
        avTransInfoList = new AVTransportInfoList();
        setStateVariable(TRANSPORTSTATE, STOPPED);
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    protected void initializeStateVariables() {
        setStateVariable(TRANSPORTSTATE, NO_MEDIA_PRESENT);
        setStateVariable(TRANSPORTSTATUS, OK);
        setStateVariable(PLAYBACKSTORAGEMEDIUM, NONE);
        setStateVariable(RECORDSTORAGEMEDIUM, NOT_IMPLEMENTED);
        setStateVariable(POSSIBLEPLAYBACKSTORAGEMEDIA, "");
        setStateVariable(POSSIBLERECORDSTORAGEMEDIA, NOT_IMPLEMENTED);
        setStateVariable(CURRENTPLAYMODE, NORMAL);
        setStateVariable(TRANSPORTPLAYSPEED, "1");
        setStateVariable(RECORDMEDIUMWRITESTATUS, NOT_IMPLEMENTED);
        setStateVariable(CURRENTRECORDQUALITYMODE, NOT_IMPLEMENTED);
        setStateVariable(POSSIBLERECORDQUALITYMODES, "");
        setStateVariable(NUMBEROFTRACKS, "0");
        setStateVariable(CURRENTTRACK, "0");
        setStateVariable(CURRENTTRACKDURATION, "00:00:00");
        setStateVariable(CURRENTMEDIADURATION, "00:00:00");
        setStateVariable(CURRENTTRACKMETADATA, "");
        setStateVariable(CURRENTTRACKURI, "");
        setStateVariable(AVTRANSPORTURI, "");
        setStateVariable(AVTRANSPORTURIMETADATA, "");
        setStateVariable(NEXTAVTRANSPORTURI, NOT_IMPLEMENTED);
        setStateVariable(NEXTAVTRANSPORTURIMETADATA, NOT_IMPLEMENTED);
        setStateVariable(RELATIVETIMEPOSITION, "00:00:00");
        setStateVariable(ABSOLUTETIMEPOSITION, NOT_IMPLEMENTED);
        setStateVariable(RELATIVECOUNTERPOSITION, NOT_IMPLEMENTED_I4);
        setStateVariable(ABSOLUTECOUNTERPOSITION, NOT_IMPLEMENTED_I4);

        setStateVariable(CURRENTTRANSPORTACTIONS, PLAY+","+STOP);
        /*+","+PAUSE+","+NEXT+","+PREVIOUS*/
    }
    /*Jason*/

    ////////////////////////////////////////////////
    // MediaRender
    ////////////////////////////////////////////////

    private MediaRenderer mediaRenderer;

    private void setMediaRenderer(MediaRenderer render)
    {
        mediaRenderer = render;
    }

    public MediaRenderer getMediaRenderer()
    {
        return mediaRenderer;
    }

    ////////////////////////////////////////////////
    // AVTransportInfoList
    ////////////////////////////////////////////////

    private AVTransportInfoList avTransInfoList;

    public AVTransportInfoList getAvTransInfoList()
    {
        return avTransInfoList;
    }

    ////////////////////////////////////////////////
    // AVTransportInfo (Current)
    ////////////////////////////////////////////////

    public void setCurrentAvTransInfo(AVTransportInfo avTransInfo)
    {
        /* Jason */
        avTransInfoList.setCurrentAvTransInfo(avTransInfo);
        /* Jason */
    }

    public AVTransportInfo getCurrentAvTransInfo()
    {
        /*Jason*/
        return avTransInfoList.getCurrentAvTransInfo();
        /*Jason*/
    }

    ////////////////////////////////////////////////
    // AVTransportInfo (Current)
    ////////////////////////////////////////////////

    public void setNextAvTransInfo(AVTransportInfo avTransInfo)
    {
        /*Jason*/
        avTransInfoList.setNextAvTransInfo(avTransInfo);
        /*Jason*/
    }

    public AVTransportInfo getNextAvTransInfo()
    {
        /*Jason*/
        return avTransInfoList.getNextAvTransInfo();
        /*Jason*/
    }

    ////////////////////////////////////////////////
    // ActionListener
    ////////////////////////////////////////////////
    /*Jason*/
    private UIListener mUIListener;

    public void setListener(UIListener listener) {
        mUIListener = listener;
    }
    /*Jason*/

    @Override
    public boolean actionControlReceived(Action action)
    {
        boolean isActionSuccess;

        String actionName = action.getName();Log.i("Jason", "AVTransport - actionControlReceived=>"+actionName);

        if (actionName == null)
            return false;

        isActionSuccess = false;

        if (actionName.equals(SETAVTRANSPORTURI) == true) {
            /*Jason*/
            if (!setRelatedStateVariableFromArgument(action, CURRENTURI)) {
                return false;
            }
            if (!setRelatedStateVariableFromArgument(action, CURRENTURIMETADATA)) {
                return false;
            }
            String currentURI = action.getArgumentValue(CURRENTURI);
            if (currentURI != null && !currentURI.equals("")) {
                setStateVariable(PLAYBACKSTORAGEMEDIUM, NETWORK);
            } else {
                setStateVariable(PLAYBACKSTORAGEMEDIUM, NONE);
                return false;
            }

            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            String currentUriMetaData = action.getArgument(CURRENTURIMETADATA).getValue();
            AVTransportInfo avTransInfo = new AVTransportInfo(instanceID, currentURI, currentUriMetaData);
            setCurrentAvTransInfo(avTransInfo);
            setStateVariable(NUMBEROFTRACKS, String.valueOf(avTransInfo.getPlaylist().size()));
            setStateVariable(CURRENTMEDIADURATION, avTransInfo.getMediaDuration());
            if (mUIListener != null)
                mUIListener.onSetAVTransportInfo(avTransInfo);
            /*Jason*/
            isActionSuccess = true;
        }

        if (actionName.equals(SETNEXTAVTRANSPORTURI) == true) {
            /*Jason*/
            if(!setRelatedStateVariableFromArgument(action, NEXTURI))
                return false;
            if(!setRelatedStateVariableFromArgument(action, NEXTURIMETADATA))
                return false;
            /*Jason*/
            AVTransportInfo avTransInfo = new AVTransportInfo();
            avTransInfo.setInstanceID(action.getArgument(INSTANCEID).getIntegerValue());
            avTransInfo.setURI(action.getArgument(NEXTURI).getValue());
            avTransInfo.setURIMetaData(action.getArgument(NEXTURIMETADATA).getValue());
            setNextAvTransInfo(avTransInfo);
            isActionSuccess = true;
        }

        if (actionName.equals(GETMEDIAINFO) == true) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            synchronized (avTransInfoList) {
                AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
                /* Jason */
                if (avTransInfo != null) {
                    setArgumentValueFromRelatedStateVariable(action, CURRENTURI);
                    setArgumentValueFromRelatedStateVariable(action, CURRENTURIMETADATA);
                    setArgumentValueFromRelatedStateVariable(action, NEXTURI);
                    setArgumentValueFromRelatedStateVariable(action, NEXTURIMETADATA);
                    setArgumentValueFromRelatedStateVariable(action, NRTRACKS);
                    setArgumentValueFromRelatedStateVariable(action, MEDIADURATION);
                    setArgumentValueFromRelatedStateVariable(action, PLAYMEDIUM);
                    setArgumentValueFromRelatedStateVariable(action, RECORDMEDIUM);
                    setArgumentValueFromRelatedStateVariable(action, WRITESTATUS);
                    isActionSuccess = true;
                }
                /* Jason */
            }
        }

        if (actionName.equals(PLAY) == true) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            String speed = action.getArgument(SPEED).getValue();
            /*Jason*/
            synchronized (avTransInfoList) {
                AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
                if (avTransInfo != null) {
                    Track track = avTransInfo.getCurrentTrack();
                    if (mUIListener != null)
                        mUIListener.onPlay(track, speed);
                    setStateVariable(TRANSPORTPLAYSPEED, speed);
                    setStateVariable(CURRENTTRACK, String.valueOf(avTransInfo.getCurrentTrackPosition()));
                    setStateVariable(CURRENTTRACKDURATION, String.valueOf(track.duration));
                    setStateVariable(CURRENTTRACKMETADATA, track.metaData);
                    setStateVariable(CURRENTTRACKURI, track.uri);
                    updateStateVariable(action, TRANSPORTSTATE, PLAYING);
                    isActionSuccess = true;
                }
            }
            /*Jason*/
        }

        if (actionName.equals(STOP) == true) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            /*Jason*/
            synchronized (avTransInfoList) {
                AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
                if (avTransInfo != null) {
                    if (mUIListener != null)
                        mUIListener.onStop();
                    updateStateVariable(action, TRANSPORTSTATE, STOPPED);
                    isActionSuccess = true;
                }
            }
            /*Jason*/
        }

        if (actionName.equals(PAUSE) == true) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            /*Jason*/
            synchronized (avTransInfoList) {
                AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
                if (avTransInfo != null) {
                    if (mUIListener != null)
                        mUIListener.onPause();
                    updateStateVariable(action, TRANSPORTSTATE, PAUSED_PLAYBACK);
                    isActionSuccess = true;
                }
            }
            /*Jason*/
        }

        /*Jason*/
        if (actionName.equals(SEEK)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            if (!setRelatedStateVariableFromArgument(action, UNIT)) {
                return false;
            }
            String target = action.getArgumentValue(TARGET);
            int setTime = 0;
            try {
                setTime = Integer.parseInt(target);
            } catch (NumberFormatException e) {
                setTime = 0;
            }
            AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
            if (avTransInfo != null) {
                if (mUIListener != null)
                    mUIListener.onSeek(setTime);
                isActionSuccess = true;
            }
        }

        if (actionName.equals(NEXT)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
            if (avTransInfo != null) {
                Track track = avTransInfo.next();
                if (mUIListener != null)
                    mUIListener.onNext(track);
                setStateVariable(CURRENTTRACK, String.valueOf(avTransInfo.getCurrentTrackPosition()));
                setStateVariable(CURRENTTRACKDURATION, String.valueOf(track.duration));
                setStateVariable(CURRENTTRACKMETADATA, track.metaData);
                setStateVariable(CURRENTTRACKURI, track.uri);
                isActionSuccess = true;
            }
        }

        if (actionName.equals(PREVIOUS)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
            if (avTransInfo != null) {
                Track track = avTransInfo.previous();
                if (mUIListener != null)
                    mUIListener.onPrevious(track);
                setStateVariable(CURRENTTRACK, String.valueOf(avTransInfo.getCurrentTrackPosition()));
                setStateVariable(CURRENTTRACKDURATION, String.valueOf(track.duration));
                setStateVariable(CURRENTTRACKMETADATA, track.metaData);
                setStateVariable(CURRENTTRACKURI, track.uri);
                isActionSuccess = true;
            }
        }

        if (actionName.equals(GETPOSITIONINFO)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            synchronized (avTransInfoList) {
                AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
                if (avTransInfo != null) {
                    // TODO: need a listener
                    setArgumentValueFromRelatedStateVariable(action, TRACK);
                    setArgumentValueFromRelatedStateVariable(action, TRACKDURATION);
                    setArgumentValueFromRelatedStateVariable(action, TRACKMETADATA);
                    setArgumentValueFromRelatedStateVariable(action, TRACKURI);
                    setArgumentValueFromRelatedStateVariable(action, RELTIME);
                    setArgumentValueFromRelatedStateVariable(action, ABSTIME);
                    setArgumentValueFromRelatedStateVariable(action, RELCOUNT);
                    setArgumentValueFromRelatedStateVariable(action, ABSCOUNT);
                    isActionSuccess = true;
                }
            }
        }

        if (actionName.equals(GETTRANSPORTINFO)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            synchronized (avTransInfoList) {
                AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfoById(instanceID);
                if (avTransInfo != null) {
                    // TODO: need a listener
                    setArgumentValueFromRelatedStateVariable(action, CURRENTTRANSPORTSTATE);
                    setArgumentValueFromRelatedStateVariable(action, CURRENTTRANSPORTSTATUS);
                    setArgumentValueFromRelatedStateVariable(action, CURRENTSPEED);
                    isActionSuccess = true;
                }
            }
        }

        if (actionName.equals(GETDEVICECAPABILITIES)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            setArgumentValueFromRelatedStateVariable(action, PLAYMEDIA);
            setArgumentValueFromRelatedStateVariable(action, RECMEDIA);
            setArgumentValueFromRelatedStateVariable(action, RECQUALITYMODES);
            isActionSuccess = true;
        }

        if (actionName.equals(GETTRANSPORTSETTINGS)) {
            int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
            setArgumentValueFromRelatedStateVariable(action, PLAYMODE);
            setArgumentValueFromRelatedStateVariable(action, RECQUALITYMODE);
            isActionSuccess = true;
        }

        if (actionName.equals(SETPLAYMODE)) {
            if (!setRelatedStateVariableFromArgument(action, NEWPLAYMODE))
                return false;
            if (mUIListener != null)
                mUIListener.onSetPlayMode(action.getArgumentValue(NEWPLAYMODE));
            isActionSuccess = true;
        }

        if (action.equals(GETCURRENTTRANSPORTACTIONS)) {
            setArgumentValueFromRelatedStateVariable(action, ACTIONS);
            isActionSuccess = true;
        }
        /*Jason*/
        return isActionSuccess;
    }

    /*Jason*/
    private void play(String speed) {
        setStateVariable(TRANSPORTPLAYSPEED, speed);
        setStateVariable(TRANSPORTSTATE, PLAYING);
        updateStateVariable(getService());
    }

    private void pause() {
        setStateVariable(RELATIVETIMEPOSITION, "00:00:00");//TODO: need a value
        setStateVariable(TRANSPORTSTATE, PAUSED_PLAYBACK);
        updateStateVariable(getService());
    }

    private void stop() {
        setStateVariable(RELATIVETIMEPOSITION, "00:00:00");
        setStateVariable(TRANSPORTSTATE, STOPPED);
        updateStateVariable(getService());
    }

    private void error() {
        setStateVariable(TRANSPORTSTATUS, ERROR_OCCURRED);
        setStateVariable(TRANSPORTSTATE, STOPPED);
        updateStateVariable(getService());
    }

    public void updateStateVariable(Action action, String varname, String varvalue) {
        setStateVariable(varname, varvalue);
        updateStateVariable(action.getService());
    }

    @Override
    public void update(Observable observable, Object data) {
        RendererUI playerUI = (RendererUI) observable;
        int uiState = playerUI.getUIState();
        switch (uiState) {
            case State.ERROR:
                error();
                break;
            case State.STOPPED:
                stop();
                break;
            case State.PAUSED_PLAYBACK:
                pause();
                break;
        }
    }

    protected void updateStateVariable(Service service) {
        StateVariable lastChange = service.getStateVariable(LASTCHANGE);
        if(lastChange == null)
            return;

        ServiceStateTable stateTable = service.getServiceStateTable();
        StringBuffer buf = null;
        buf = new StringBuffer();
        buf.append("<Event>\n");
        buf.append("<InstanceID val=\"0\">\n");
        int tableSize = stateTable.size();
        for (int n = 0; n < tableSize; n++) {
            StateVariable var = stateTable.getStateVariable(n);
            String varName = var.getName();

            if (!var.isSendEvents() && !varName.startsWith("A_ARG")) {
                buf.append("<" + var.getName() + " val=\"" + XML.escapeXMLChars(var.getValue())
                        + "\"/>\n");
            }
        }
        buf.append("</InstanceID>\n");
        buf.append("</Event>");

        lastChange.setValue(buf.toString());
    }

    public static interface UIListener {
        public void onSetAVTransportInfo(AVTransportInfo avTransInfo);
        public void onPlay(Track track, String speed);
        public void onStop();
        public void onPause();
        public void onNext(Track track);
        public void onPrevious(Track track);
        public void onSeek(int sec);
        public void onSetPlayMode(String playMode);
    }
    /*Jason*/
}

