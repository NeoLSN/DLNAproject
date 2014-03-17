
package com.iac.dlnaproject.model;

import com.iac.dlnaproject.ControllerProxy;
import com.iac.dlnaproject.model.Participant.Host;

import java.util.HashSet;
import java.util.Set;

public class ActivityModel {

    private Host mHost;
    private Set<Participant> participants;

    private ObservablePool mObservablePool;
    private ControllerProxy mCtrlProxy;

    public static ActivityModel create(Host host) {
        return new ActivityModel(host);
    }

    private ActivityModel(Host host) {
        mHost = host;
        participants = new HashSet<Participant>();
        participants.add(host);
        mObservablePool = new ObservablePool();

        // 從檔案或是資料庫將資料同步
        load();
    }

    public void destroy() {
        participants.clear();
        save();
    }

    public void Register(Participant participant) {
        participants.add(participant);
    }

    public void Unregister(Participant participant) {
        if (mHost.equals(participant)) {
            mHost.saveModel(this);
        }
        participants.remove(participant);
    }

    public void send(Participant from, UIEvent message) {
        if (message == null)
            return;
        for (Participant p : participants) {
            if (!(p.equals(from))) {
                p.receive(message);
            }
        }
    }

    public void load() {
        // TODO: 載入資料庫或其他東西
    }

    public void save() {
        // TODO: 儲存回雲端和本地資料庫
    }
}
