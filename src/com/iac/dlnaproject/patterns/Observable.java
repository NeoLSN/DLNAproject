package com.iac.dlnaproject.patterns;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * An interface to represent the state of an observable Transaction.
 */
public abstract class Observable {
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
    public void attach(Observer observer) {
        mObservers.add(observer);
    }

    /**
     * Detach an observer from this object.
     *
     * @param observer The observer object to be detached from.
     */
    public void detach(Observer observer) {
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
