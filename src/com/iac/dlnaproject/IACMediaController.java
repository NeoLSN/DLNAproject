package com.iac.dlnaproject;

import com.iac.dlnaproject.nowplay.Item;
import com.iac.dlnaproject.nowplay.ItemFactory;
import com.iac.dlnaproject.nowplay.MediaItem;
import com.iac.dlnaproject.nowplay.MediaItem.ResInfo;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.std.av.player.action.BrowseAction;
import org.cybergarage.upnp.std.av.player.action.BrowseResult;
import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

import java.util.ArrayList;
import java.util.List;


public class IACMediaController extends ControlPoint
{
    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    public IACMediaController()
    {
    }

    ////////////////////////////////////////////////
    // DeviceList
    ////////////////////////////////////////////////

    private DeviceList getDeviceList(String deviceType)
    {
        DeviceList devList = new DeviceList();

        DeviceList allDevList = getDeviceList();
        int allDevCnt = allDevList.size();
        for (int n=0; n<allDevCnt; n++) {
            Device dev = allDevList.getDevice(n);
            if (dev.isDeviceType(deviceType) == false)
                continue;
            devList.add(dev);
        }
        return devList;
    }

    public DeviceList getServerDeviceList()
    {
        return getDeviceList("urn:schemas-upnp-org:device:MediaServer:1");
    }

    public DeviceList getRendererDeviceList()
    {
        return getDeviceList(MediaRenderer.DEVICE_TYPE);
    }

    ////////////////////////////////////////////////
    // get*Device
    ////////////////////////////////////////////////

    public Device getServerDevice(String name)
    {
        Device foundDev = getDevice(name);
        if (foundDev.isDeviceType("urn:schemas-upnp-org:device:MediaServer:1"))
            return foundDev;
        return null;
    }

    public Device getRendererDevice(String name)
    {
        Device foundDev = getDevice(name);
        if (foundDev.isDeviceType(MediaRenderer.DEVICE_TYPE))
            return foundDev;
        return null;
    }

    ////////////////////////////////////////////////
    // browse
    ////////////////////////////////////////////////

    public Node browse(
            Device dev,
            String objectID,
            String browseFlag,
            String filter,
            int startIndex,
            int requestedCount,
            String sortCaiteria)
    {
        System.out.println("browse " + objectID + ", " + browseFlag + ", " + startIndex + ", " + requestedCount);

        if (dev == null)
            return null;

        Service conDir = dev.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
        if (conDir == null)
            return null;
        Action action = conDir.getAction("Browse");
        if (action == null)
            return null;

        BrowseAction browseAction = new BrowseAction(action);
        browseAction.setObjectID(objectID);
        browseAction.setBrowseFlag(browseFlag);
        browseAction.setStartingIndex(startIndex);
        browseAction.setRequestedCount(requestedCount);
        //browseAction.setFilter(filter);
        browseAction.setFilter("");
        browseAction.setSortCriteria(sortCaiteria);
        if (browseAction.postControlAction() == false)
            return null;

        /*
         * ContentDirectory:1 Service Template Version 1.01
         * 2.7.4.2. Argument Descriptions
         *  RequestedCount ui4 Requested number of entries under the object specified by ObjectID.
         *  RequestedCount =0 indicates request all entries.
         * Added to set the RequestedCount parameter using the NumberReturned result when the specified parameter is zero and
         * the NumberReturned parameter is less than the TotalMatches parameter for XMBC.
         */
        if (requestedCount == 0) {
            int numberReturned = browseAction.getNumberReturned();
            int totalMatches = browseAction.getTotalMatches();
            if (numberReturned == 0) {
                if (0 < totalMatches) {
                    browseAction.setRequestedCount(totalMatches);
                    if (browseAction.postControlAction() == false)
                        return null;
                }
                else {
                    browseAction.setRequestedCount(9999);
                    if (browseAction.postControlAction() == false)
                        return null;
                }
            }
        }

        Argument resultArg = browseAction.getArgument(BrowseAction.RESULT);
        if (resultArg == null)
            return null;

        String resultStr = resultArg.getValue();
        if (resultStr == null)
            return null;

        Node node = null;

        Parser xmlParser = UPnP.getXMLParser();

        try {
            node = xmlParser.parse(resultStr);
        }
        catch (ParserException pe) {
            Debug.warning(pe);
            return null;
        };

        return node;
    }

    ////////////////////////////////////////////////
    // browse*
    ////////////////////////////////////////////////

    public Node browseMetaData(
            Device dev,
            String objectId,
            String filter,
            int startIndex,
            int requestedCount,
            String sortCaiteria)
    {
        return browse(dev, objectId, BrowseAction.BROWSE_METADATA, filter, startIndex, requestedCount, sortCaiteria);
    }

    public Node browseMetaData(
            Device dev,
            String objectId)
    {
        return browseMetaData(dev, objectId, "*", 0, 0, "");
    }

    public Node browseDirectChildren(
            Device dev,
            String objectID,
            String filter,
            int startIndex,
            int requestedCount,
            String sortCaiteria)
    {
        return browse(dev, objectID, BrowseAction.BROWSE_DIRECT_CHILDREN, filter, startIndex, requestedCount, sortCaiteria);
    }

    public Node browseDirectChildren(
            Device dev,
            String objectId)
    {
        return browseDirectChildren(dev, objectId, "*", 0, 0, "");
    }

    ////////////////////////////////////////////////
    // Content
    ////////////////////////////////////////////////

    public List<Item> browse(Device dev)
    {
        return browse(dev, "0");
    }

    public List<Item> browse(Device dev, String objectId)
    {
        return browse(dev, objectId, false);
    }

    public List<Item> browse(Device dev, String objectId, boolean hasBrowseChildNodes, boolean hasRootNodeMetadata)
    {
        List<Item> contentRootNode = new ArrayList<Item>();

        /*if (hasRootNodeMetadata) {
            Node rootNode = browseMetaData(dev, objectId, "*", 0, 0, "");
            if (rootNode != null)
                contentRootNode.set(rootNode);
        }*/

        browse(contentRootNode, dev, objectId, hasRootNodeMetadata);

        return contentRootNode;
    }

    public List<Item> browse(Device dev, String objectId, boolean hasBrowseChildNodes)
    {
        return browse(dev, objectId, hasBrowseChildNodes, false);
    }

    private int browse(List<Item> parentNode, Device dev, String objectID, boolean hasBrowseChildNodes)
    {
        if (objectID == null)
            return 0;

        Node resultNode = browseDirectChildren(dev, objectID, "*", 0, 0, "");
        if (resultNode == null)
            return 0;

        BrowseResult browseResult = new BrowseResult(resultNode);
        int nResultNode = 0;
        int nContents = browseResult.getNContentNodes();
        for (int n=0; n<nContents; n++) {
            Node xmlNode = browseResult.getContentNode(n);
            Item item = ItemFactory.create(xmlNode);
            if (item == null)
                continue;
            parentNode.add(item);
            nResultNode++;
        }

        return nResultNode;
    }

    ////////////////////////////////////////////////
    // Content
    ////////////////////////////////////////////////

    public List<Item> getContentDirectory(Device dev)
    {
        return getContentDirectory(dev, "0");
    }

    public List<Item> getContentDirectory(Device dev, String objectId)
    {
        return browse(dev, objectId, true);
    }

    ////////////////////////////////////////////////
    // play
    ////////////////////////////////////////////////

    public boolean setAVTransportURI(
            Device dev,
            MediaItem item)
    {
        if (dev == null)
            return false;

        ResInfo resInfo = item.getRes();
        if (resInfo == null)
            return false;
        String resURL = resInfo.res;
        if (resURL == null || resURL.length() <= 0)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.SETAVTRANSPORTURI);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");
        action.setArgumentValue(AVTransport.CURRENTURI, resURL);
        action.setArgumentValue(AVTransport.CURRENTURIMETADATA, item.getNode().toString());

        return action.postControlAction();
    }

    public boolean play(Device dev)
    {
        if (dev == null)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.PLAY);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");
        action.setArgumentValue(AVTransport.SPEED, "1");

        return action.postControlAction();
    }

    public boolean stop(Device dev)
    {
        if (dev == null)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.STOP);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");

        return action.postControlAction();
    }

    public boolean play(
            Device dev,
            MediaItem item)
    {
        stop(dev);
        if (setAVTransportURI(dev, item) == false)
            return false;
        return play(dev);
    }
}