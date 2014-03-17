
package com.iac.dlnaproject;

import com.iac.dlnaproject.BrowseTask.BrowseParams;
import com.iac.dlnaproject.NetworkStatusReceiver.OnNetworkChangedListener;
import com.iac.dlnaproject.nowplay.Item;
import com.iac.dlnaproject.patterns.Observable;
import com.iac.dlnaproject.patterns.Observer;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ControllerProxy {

    public static final String KEY_PREFERED_SOURCE = "key_perfered_source";
    public static final String KEY_PREFERED_RENDERER = "key_perfered_sink";

    private static ControllerProxy sInstance = null;

    private Context mContext;
    private Device mServer;
    private Device mRenderer;
    private Stack<BrowseResult> mStack;

    private IACMediaController mIACMediaController;
    private NetworkStatusReceiver mNetworkStatusReceiver;

    private ControlPointDeviceNotifier mCPDeviceNotifier;

    public static synchronized ControllerProxy getInstance() {
        return sInstance;
    }

    public static ControllerProxy init(Context context) {
        UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);
        if (sInstance == null)
            sInstance = new ControllerProxy(context);
        sInstance.registerNetworkStatusReceiver();
        return sInstance;
    }

    private ControllerProxy(Context context) {
        mContext = context;
        mCPDeviceNotifier = new ControlPointDeviceNotifier();
        mStack = new Stack<BrowseResult>();
    }

    public void terminate() {
        mStack.clear();
        setControlPoint(null);
        mCPDeviceNotifier = null;
        unRegisterNetworkStatusReceiver();
    }

    public void setControlPoint(IACMediaController controlPoint) {
        if (controlPoint != null) {
            controlPoint.search(getPerferedSourceUDN());
            controlPoint.search(getPerferedRendererUDN());
            controlPoint.addDeviceChangeListener(mCPDeviceNotifier);
            controlPoint.addNotifyListener(mCPDeviceNotifier);
            controlPoint.addSearchResponseListener(mCPDeviceNotifier);
        } else if (mIACMediaController != null) {
            mIACMediaController.removeDeviceChangeListener(mCPDeviceNotifier);
            mIACMediaController.removeNotifyListener(mCPDeviceNotifier);
            mIACMediaController.removeSearchResponseListener(mCPDeviceNotifier);
            mIACMediaController.stop();
        }
        mIACMediaController = controlPoint;
    }

    public IACMediaController getControlPoint() {
        return mIACMediaController;
    }

    public void addOnNetworkChangedListener(OnNetworkChangedListener listener) {
        mNetworkStatusReceiver.addOnNetworkChangedListener(listener);
    }

    public void removeOnNetworkChangedListener(OnNetworkChangedListener listener) {
        mNetworkStatusReceiver.removeOnNetworkChangedListener(listener);
    }

    private void registerNetworkStatusReceiver() {
        if (mNetworkStatusReceiver == null) {
            mNetworkStatusReceiver = new NetworkStatusReceiver();
            mContext.registerReceiver(mNetworkStatusReceiver, new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void unRegisterNetworkStatusReceiver() {
        if (mNetworkStatusReceiver != null) {
            mContext.unregisterReceiver(mNetworkStatusReceiver);
            mNetworkStatusReceiver = null;
        }
    }

    public Device getSelectedServer() {
        return mServer;
    }

    public boolean isMeidaServerWorking() {
        return (mServer != null) ? true : false;
    }

    public void setSelectedServer(Device server) {
        mServer = server;
        savePreference(KEY_PREFERED_SOURCE, (server != null) ? server.getUDN() : "");
    }

    public Device getSelectedRenderer() {
        return mRenderer;
    }

    public boolean isMeidaRendererWorking() {
        return (mRenderer != null) ? true : false;
    }

    public void setSelectedRenderer(Device renderer) {
        mRenderer = renderer;
        savePreference(KEY_PREFERED_RENDERER, (renderer != null) ? renderer.getUDN() : "");
    }

    public String getPerferedSourceUDN() {
        return getPreference(KEY_PREFERED_SOURCE);
    }

    public String getPerferedRendererUDN() {
        return getPreference(KEY_PREFERED_RENDERER);
    }

    public void savePreference(String key, int value) {
        savePreference(key, String.valueOf(value));
    }

    public void savePreference(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(key, value);
        spe.commit();
    }

    public String getPreference(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getString(key, "");
    }

    public BrowseResult getContentDirectory(BrowseParams params) {
        if (mServer == null)
            return null;
        List<Item> node = mIACMediaController.browse(mServer, params.objectId);
        BrowseResult result = new BrowseResult(params, node);
        BrowseResult lastResult = peekItems();
        if (lastResult == null || !lastResult.getParams().objectId.equals(params.objectId))
            pushItems(result);
        return result;
    }

    public void pushItems(BrowseResult result) {
        if (result != null) {
            mStack.add(result);
        }
    }

    public BrowseResult peekItems() {
        if (mStack.empty()) {
            return null;
        }
        return mStack.peek();
    }

    public BrowseResult popItems() {
        if (mStack.empty()) {
            return null;
        }
        return mStack.pop();
    }

    public List<Device> getRendererDeviceList() {
        List<Device> browseList = new ArrayList<Device>();
        browseList.addAll(mIACMediaController.getRendererDeviceList());
        return browseList;
    }

    public List<Device> getServerDeviceList() {
        List<Device> browseList = new ArrayList<Device>();
        browseList.addAll(mIACMediaController.getServerDeviceList());
        return browseList;
    }

    public void attach(Observer observer) {
        mCPDeviceNotifier.attach(observer);
    }

    public void detach(Observer observer) {
        mCPDeviceNotifier.detach(observer);
    }

    public class ControlPointDeviceNotifier extends Observable implements DeviceChangeListener, NotifyListener, SearchResponseListener {

        @Override
        public void deviceAdded(Device dev) {
            String sourceUDN = getPerferedSourceUDN();
            if (dev.getUDN().equalsIgnoreCase(sourceUDN)) {
                mServer = dev;
                notifyObservers();
            }

            String rendererUDN = getPerferedRendererUDN();
            if (dev.getUDN().equalsIgnoreCase(rendererUDN)) {
                mRenderer = dev;
                notifyObservers();
            }
        }

        @Override
        public void deviceRemoved(Device dev) {
            String sourceUDN = getPerferedSourceUDN();
            if (dev.equals(mServer) || dev.getUDN().equalsIgnoreCase(sourceUDN)) {
                mServer = null;
                notifyObservers();
            }

            String rendererUDN = getPerferedRendererUDN();
            if (dev.equals(mRenderer) || dev.getUDN().equalsIgnoreCase(rendererUDN)) {
                mRenderer = null;
                notifyObservers();
            }
        }

        @Override
        public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
            notifyObservers();
        }

        @Override
        public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
            notifyObservers();
        }

    }
}
