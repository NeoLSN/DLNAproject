package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.constant.DIDLLite;

import org.cybergarage.xml.Node;

public class ItemFactory {

    public static Item create(Node node) {
        if (node.getName().equals(DIDLLite.CONTAINER)) {
            return new ContainerItem(node);
        } else if (node.getName().equals("item")) {
            return new MediaItem(node);
        }
        return null;
    }
}
