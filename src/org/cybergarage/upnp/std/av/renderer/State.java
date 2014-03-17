/*Jason*/
package org.cybergarage.upnp.std.av.renderer;


public class State {
    public static final int ERROR  = -1;
    public static final int NO_MEDIA_PRESENT = 0;
    public static final int PLAYING = 1;
    public static final int PAUSED_PLAYBACK = 2;
    public static final int TRANSITIONING = 3;
    public static final int STOPPED  = 4;

    private int mState;

    public State() {
        mState = NO_MEDIA_PRESENT;
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void setState(int state) {
        if ((state < ERROR) && (state > STOPPED)) {
            throw new IllegalArgumentException("Bad state: " + state);
        }
        mState = state;
    }
}
