
package com.iac.dlnaproject.nowplay;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.AllowedValueRange;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Icon;
import org.cybergarage.upnp.IconList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.renderer.RenderingControl;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Player {

    private boolean isRunning = false;
    private int track;
    private Device dev;
    private Service avtransportService;
    private Service renderControlService;
    private Timer timer;
    private Action transportInfo;
    private Action mediaInfo;
    private int volume;
    private int volumeMax;
    private int volumeMin;
    private int volumeStep;
    private boolean mute;
    private PlayMode playMode;
    private PlayerState state;
    private int duration;
    private int position;
    private boolean loudness;
    private boolean canNext = true;
    private boolean canPrev = true;
    private Drawable icon;
    private PlayerInfo playerInfo;

    private MediaItem mMediaItem;
    private final Set<ChangeListener> mListeners;
    private Iterator<ChangeListener> mIterator;
    private RequestQueue mRequestQueue;
    private Executer mExecuter;

    public Player() {
        mListeners = new HashSet<ChangeListener>();
        mRequestQueue = new RequestQueue();
        mExecuter = new Executer(mRequestQueue);
        mExecuter.start();
    }

    public void setDevice(Device dev) {
        if (dev == null)
            throw new IllegalArgumentException();

        this.dev = dev;

        ServiceList serviceList = dev.getServiceList();
        ArrayList<Service> sl = new ArrayList<Service>();
        sl.addAll(serviceList);
        int size = sl.size();
        for (int i = 0; i < size; i++) {
            Service service = sl.get(i);
            if (service.isService(AVTransport.SERVICE_TYPE)) {
                avtransportService = service;
            } else if (service.isService(RenderingControl.SERVICE_TYPE)) {
                renderControlService = service;
            }
        }
        Request r = new Request() {
            @Override
            public void execute() {
                AllowedValueRange avr = Player.this.dev.getStateVariable(RenderingControl.VOLUME)
                        .getAllowedValueRange();
                setVolumeMax(Integer.valueOf(avr.getMaximum()));
                setVolumeMin(Integer.valueOf(avr.getMinimum()));
                setVolumeStep(Integer.valueOf(avr.getStep()));
            }
        };
        mRequestQueue.put(r);
    }

    @Override
    protected void finalize() throws Throwable {
        mExecuter.interrupt();
        super.finalize();
    }

    public void startChecking() {
        stopChecking();
        Request r = new Request() {
            @Override
            public void execute() {
                Action ability = avtransportService
                        .getAction(AVTransport.GETCURRENTTRANSPORTACTIONS);
                ability.setArgumentValue(AVTransport.INSTANCEID, "0");
                ability.postControlAction();
                setActions(ability.getArgumentValue(AVTransport.ACTIONS));
                Log.i("Jason", ability.getArgumentValue(AVTransport.ACTIONS));
            }
        };
        mRequestQueue.put(r);
        timer = new Timer("Player updater", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 2000);
        isRunning = true;
    }

    public void stopChecking() {
        mRequestQueue.clear();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isRunning = false;
    }

    private void generatePlayerInfo() {
        playerInfo = new PlayerInfo();
        playerInfo.setLocation(dev.getLocation());
        playerInfo.setModelName(dev.getModelName());
        playerInfo.setInfo(mediaInfo);
        playerInfo.setInfo(transportInfo);
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void update() {

        if (avtransportService != null) {
            transportInfo = avtransportService.getAction(AVTransport.GETTRANSPORTINFO);
            transportInfo.setArgumentValue(AVTransport.INSTANCEID, "0");
            transportInfo.postControlAction();
            try {
                state = PlayerState.valueOf(transportInfo
                        .getArgumentValue(AVTransport.CURRENTTRANSPORTSTATE));
            } catch (IllegalArgumentException e) {
                state = PlayerState.UNKNOWN;
            }

            mediaInfo = avtransportService.getAction(AVTransport.GETMEDIAINFO);
            mediaInfo.setArgumentValue(AVTransport.INSTANCEID, "0");
            mediaInfo.postControlAction();
            setDuration(mediaInfo.getArgumentValue(AVTransport.MEDIADURATION));

            Action pos = avtransportService.getAction(AVTransport.GETPOSITIONINFO);
            pos.setArgumentValue(AVTransport.INSTANCEID, "0");
            pos.postControlAction();
            setPosition(pos.getArgumentValue(AVTransport.RELTIME));
            generatePlayerInfo();
        }

        if (renderControlService != null) {
            Action getVolume = renderControlService.getAction(RenderingControl.GETVOLUME);
            getVolume.setArgumentValue(RenderingControl.INSTANCEID, "0");
            getVolume.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
            getVolume.postControlAction();
            volume = getVolume.getArgumentIntegerValue(RenderingControl.CURRENTVOLUME);

            Action getMute = renderControlService.getAction(RenderingControl.GETMUTE);
            getMute.setArgumentValue(RenderingControl.INSTANCEID, "0");
            getMute.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
            getMute.postControlAction();
            mute = getMute.getArgumentIntegerValue(RenderingControl.CURRENTMUTE) == 1 ? true
                    : false;
        }

        notifyListener();
    }

    void notifyListener() {
        mIterator = mListeners.iterator();
        try {
            while (mIterator.hasNext()) {
                mIterator.next().playerChanged(this);
            }
        } finally {
            mIterator = null;
        }
    }

    public String getName() {
        return this.dev.getFriendlyName();
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public PlayerState getState() {
        return state;
    }

    public enum PlayerState {
        STOPPED, PLAYING, PAUSED_PLAYBACK, TRANSITIONING, UNKNOWN
    }

    public enum PlayMode {
        SHUFFLE_NOREPEAT, NORMAL, REPEAT_ALL, SHUFFLE
    }

    public int getVolume() {
        return volume;
    }

    public void addListener(ChangeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        mListeners.remove(listener);
    }

    public interface ChangeListener {
        public void playerChanged(Player player);
    }

    public boolean isPlaying() {
        return state == PlayerState.PLAYING || state == PlayerState.TRANSITIONING;
    }

    public void toggleState() {
        Request request = new Request() {
            @Override
            public void execute() {
                Action a;
                if (isPlaying()) {
                    a = avtransportService.getAction(AVTransport.PAUSE);
                } else {
                    a = avtransportService.getAction(AVTransport.PLAY);
                    a.setArgumentValue(AVTransport.SPEED, "1");
                }
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public void next() {
        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.NEXT);
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public void prev() {
        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.PREVIOUS);
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public void stop() {
        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.STOP);
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public boolean isMute() {
        return mute;
    }

    public void setVolume(int volume) {
        if (volume >= 0 && volume <= 100) {
            this.volume = volume;
            Request request = new Request() {
                @Override
                public void execute() {
                    Action a = renderControlService.getAction(RenderingControl.SETVOLUME);
                    a.setArgumentValue(RenderingControl.DESIREDVOLUME, getVolume());
                    a.setArgumentValue(RenderingControl.INSTANCEID, "0");
                    a.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
                    a.postControlAction();
                }
            };
            mRequestQueue.put(request);
        }
    }

    public void toggleMute() {
        Request request = new Request() {
            @Override
            public void execute() {
                Action mute = renderControlService.getAction(RenderingControl.SETMUTE);
                mute.setArgumentValue(RenderingControl.INSTANCEID, "0");
                mute.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
                mute.setArgumentValue(RenderingControl.DESIREDMUTE, isMute() ? "0"
                        : "1");
                mute.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public String getLocation() {
        return dev.getLocation();
    }

    @Override
    public String toString() {
        return "Player@" + getLocation();
    }

    public void changeTo(MediaItem item) {
        if (item == null)
            return;
        mMediaItem = item;
        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.SETAVTRANSPORTURI);
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.setArgumentValue(AVTransport.CURRENTURI, getMediaItem().getRes().res);
                a.setArgumentValue(AVTransport.CURRENTURIMETADATA, getMediaItem().getURIMetadata());
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);

        if (!isPlaying()) {
            play();
        }
    }

    public String getUDN() {
        return dev.getUDN();
    }

    public void play(MediaItem item) {
        if (item == null)
            return;
        mMediaItem = item;
        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.SETAVTRANSPORTURI);
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.setArgumentValue(AVTransport.CURRENTURI, mMediaItem.getRes().res);
                a.setArgumentValue(AVTransport.CURRENTURIMETADATA, mMediaItem.getURIMetadata());
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);

        play();
    }

    private void play() {
        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.PLAY);
                a.setArgumentValue(AVTransport.SPEED, "1");
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public void setPlayMode(boolean shuffle, boolean repeat) {
        if (shuffle && repeat)
            playMode = PlayMode.SHUFFLE;
        else if (shuffle)
            playMode = PlayMode.SHUFFLE_NOREPEAT;
        else if (repeat)
            playMode = PlayMode.REPEAT_ALL;
        else
            playMode = PlayMode.NORMAL;

        Request request = new Request() {
            @Override
            public void execute() {
                Action a = avtransportService.getAction(AVTransport.SETPLAYMODE);
                a.setArgumentValue(AVTransport.INSTANCEID, "0");
                a.setArgumentValue(AVTransport.NEWPLAYMODE, playMode.name());
                a.postControlAction();
            }
        };
        mRequestQueue.put(request);
    }

    public Device getDevice() {
        return dev;
    }

    public Service getAvtransportService() {
        return avtransportService;
    }

    public Service getRenderControlService() {
        return renderControlService;
    }

    public String getUri(MediaItem info) {
        String u = info.getAlbumArtUri();
        if (u == null)
            return null;

        if (!u.startsWith("http://")) {
            int pos = dev.getLocation().indexOf('/', 7);
            u = dev.getLocation().substring(0, pos) + u;
        }
        return u;
    }

    public boolean isRepeat() {
        return playMode == PlayMode.REPEAT_ALL || playMode == PlayMode.SHUFFLE;
    }

    public boolean isShuffle() {
        return playMode == PlayMode.SHUFFLE || playMode == PlayMode.SHUFFLE_NOREPEAT;
    }

    public void setDuration(String value) {
        this.duration = parseTime(value);
    }

    public void setPosition(String value) {
        this.position = parseTime(value);
    }

    public void setPosition(int value) {
        if (value >= 0 && value < duration) {
            this.position = value;
            Request request = new Request() {
                @Override
                public void execute() {
                    Action s = avtransportService.getAction(AVTransport.SEEK);
                    s.setArgumentValue(AVTransport.INSTANCEID, "0");
                    s.setArgumentValue(AVTransport.UNIT, AVTransport.REL_TIME);
                    s.setArgumentValue(AVTransport.TARGET, position);
                    s.postControlAction();
                }
            };
            mRequestQueue.put(request);
        }
    }

    private int parseTime(String value) {
        try {
            int d = 0;
            String[] parts = value.split(":");
            int c = parts.length;
            d = Integer.parseInt(parts[c - 1]);
            if (c > 1) {
                d += Integer.parseInt(parts[c - 2]) * 60;
            }
            if (c > 2) {
                d += Integer.parseInt(parts[c - 3]) * 60 * 60;
            }
            return d;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getDuration() {
        return formatTime(duration);
    }

    private String formatTime(int seconds) {
        String secs = "" + seconds % 60;
        if (secs.length() == 1)
            secs = "0" + secs;
        return (seconds / 60) + ":" + secs;
    }

    public int getDurationSeconds() {
        return duration;
    }

    public int getPositionSeconds() {
        return position;
    }

    public String getPosition() {
        return formatTime(position);
    }

    public String getTimeLeft() {
        return "- " + formatTime(duration - position);
    }

    public void tick() {
        if (isPlaying()) {
            position++;
        }
    }

    public void setLoudness(boolean loudness) {
        this.loudness = loudness;
    }

    public boolean isLoudness() {
        return loudness;
    }

    public void controlLoudness(boolean loudness) {
        this.loudness = loudness;
        Request r = new Request() {
            @Override
            public void execute() {
                Action a = renderControlService.getAction(RenderingControl.SETLOUDNESS);
                a.setArgumentValue(RenderingControl.INSTANCEID, "0");
                a.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
                a.setArgumentValue(RenderingControl.DESIREDLOUDNESS, isLoudness() ? "1" : "0");
                a.postControlAction();
            }
        };
        mRequestQueue.put(r);
    }

    public void setActions(String value) {
        canNext = value.indexOf("Next") > -1;
        canPrev = value.indexOf("Previous") > -1;
    }

    public boolean canNext() {
        return canNext;
    }

    public boolean canPrevious() {
        return canPrev;
    }

    public Drawable getIcon() {
        if (icon != null)
            return icon;
        String url = getIconUrl();
        if (url == null)
            return null;

        try {
            URLConnection connection = new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            try {
                Drawable d = Drawable.createFromStream(is, url);
                this.icon = d;
                return d;
            } finally {
                is.close();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String getIconUrl() {
        Device renderer = dev.getDevice(MediaRenderer.DEVICE_TYPE);
        if (renderer == null)
            return null;

        IconList il = renderer.getIconList();
        if (il == null)
            return null;

        if (il.size() > 0) {
            Icon icon = il.getIcon(0);
            int pos = dev.getLocation().indexOf('/', 7);
            return dev.getLocation().substring(0, pos) + icon.getURL();
        } else {
            return null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getVolumeMin() {
        return volumeMin;
    }

    public void setVolumeMin(int volumeMin) {
        this.volumeMin = volumeMin;
    }

    public int getVolumeMax() {
        return volumeMax;
    }

    public void setVolumeMax(int volumeMax) {
        this.volumeMax = volumeMax;
    }

    public int getVolumeStep() {
        return volumeStep;
    }

    public void setVolumeStep(int volumeStep) {
        this.volumeStep = volumeStep;
    }

    public MediaItem getMediaItem() {
        return mMediaItem;
    }

    interface Request {
        void execute();
    }

    class RequestQueue {
        private LinkedList<Request> requests;

        RequestQueue() {
            requests = new LinkedList<Request>();
        }

        synchronized Request get() {
            while (requests.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return requests.removeFirst();
        }

        synchronized void put(Request request) {
            requests.addLast(request);
            notifyAll();
        }

        synchronized void clear() {
            requests.clear();
        }
    }

    class Executer extends Thread {
        private RequestQueue queue;

        Executer(RequestQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                queue.get().execute();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
        }
    }
}
