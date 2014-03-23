package com.iac.dlnaproject.patterns.impl;

import com.iac.dlnaproject.patterns.Observer;

import java.util.ArrayList;
import java.util.Iterator;

public class Observable implements com.iac.dlnaproject.patterns.Observable {
    private final ArrayList<Observer> mObservers;
    private Iterator<Observer> mIterator;

    public Observable() {
        mObservers = new ArrayList<Observer>();
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
