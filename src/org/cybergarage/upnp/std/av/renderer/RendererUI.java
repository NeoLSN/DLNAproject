
package org.cybergarage.upnp.std.av.renderer;

import java.util.Observable;

public abstract class RendererUI extends Observable {

    private State mState;

    public RendererUI() {
        mState = new State();
    }

    protected State getState() {
        return mState;
    }

    public int getUIState() {
        return getState().getState();
    }

    public void notifyErrorOccurred() {
        notifyStateChanged(State.ERROR);
    }

    public void notifyPlayCompleted() {
        notifyStateChanged(State.STOPPED);
    }

    protected void notifyStateChanged(int State) {
        getState().setState(State);
        setChanged();
    }

    public abstract boolean isPlaying();
    public abstract int getCurrentPosition();
    public abstract int getDuration();
}
