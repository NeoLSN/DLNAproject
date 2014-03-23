package com.iac.dlnaproject.patterns;

public interface Observable {
    public void regesiterObserver(Observer observer);
    public void unregesiterObserver(Observer observer);
    public void notifyObservers();
}
