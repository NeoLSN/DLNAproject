package com.iac.dlnaproject.nowplay;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerInfo {
    private Map<String, String> properties = new LinkedHashMap<String, String>();

    public void setLocation(String location) {
        properties.put("Location", location);
    }

    public void setModelName(String modelName) {
        properties.put("ModelName", modelName);
    }

    public void setInfo(Action info) {
        if (info == null) return;

        ArgumentList al = info.getArgumentList();
        for (int i = 0; i < al.size(); i++) {
            Argument arg = al.getArgument(i);
            properties.put(arg.getName(), arg.getValue());
        }
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}