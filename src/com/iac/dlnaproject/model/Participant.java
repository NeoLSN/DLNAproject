
package com.iac.dlnaproject.model;


public interface Participant {

    public void send(UIEvent event);

    public void receive(UIEvent event);

    public static interface Host extends Participant {
        public ActivityModel getActivityActionModel();
        public void saveModel(ActivityModel model);
    }
}
