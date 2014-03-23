
package com.iac.dlnaproject;

import com.iac.dlnaproject.nowplay.MediaItem;
import com.iac.dlnaproject.patterns.Observable;
import com.iac.dlnaproject.patterns.Observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PlayQueue extends ArrayList<MediaItem> implements Observable {

    private final ArrayList<Observer> mObservers;
    private Iterator<Observer> mIterator;

    public PlayQueue() {
        mObservers = new ArrayList<Observer>();
    }

    @Override
    public boolean add(MediaItem object) {
        boolean result = super.add(object);
        notifyObservers();
        return result;
    }

    @Override
    public void add(int index, MediaItem object) {
        super.add(index, object);
        notifyObservers();
    }

    @Override
    public boolean addAll(Collection<? extends MediaItem> collection) {
        boolean result = super.addAll(collection);
        notifyObservers();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends MediaItem> collection) {
        boolean result = super.addAll(index, collection);
        notifyObservers();
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        notifyObservers();
    }

    @Override
    public MediaItem remove(int index) {
        MediaItem result = super.remove(index);
        notifyObservers();
        return result;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = super.remove(object);
        notifyObservers();
        return result;
    }

    @Override
    public MediaItem set(int index, MediaItem object) {
        MediaItem result = super.set(index, object);
        notifyObservers();
        return result;
    }

    /**
     * Attach an observer to this object.
     *
     * @param observer The observer object to be attached to.
     */
    @Override
    public void regesiterObserver(Observer observer) {
        mObservers.add(observer);
    }

    /**
     * Detach an observer from this object.
     *
     * @param observer The observer object to be detached from.
     */
    @Override
    public void unregesiterObserver(Observer observer) {
        if (mIterator != null) {
            mIterator.remove();
        } else {
            mObservers.remove(observer);
        }
    }

    /**
     * Notify all observers that a status change has occurred.
     */
    @Override
    public void notifyObservers() {
        mIterator = mObservers.iterator();
        try {
            while (mIterator.hasNext()) {
                mIterator.next().update(this);
            }
        } finally {
            mIterator = null;
        }
    }
}
