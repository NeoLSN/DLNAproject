
package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.constant.DC;
import com.iac.dlnaproject.constant.DIDLLite;
import com.iac.dlnaproject.constant.UPnP;

import org.cybergarage.xml.Attribute;
import org.cybergarage.xml.Node;
import org.json.JSONObject;

public class ContainerItem implements Item {

    private String id = "";
    private String parentId = "";
    private String title = "";
    private String objectClass = "";
    private String childCount = "";
    //private long date = 0;
    private String date = "";
    private String restricted = "";

    private Node node;

    public ContainerItem(JSONObject jsonObject) {

    }

    public ContainerItem(Node node) {
        if (node.getName().equals(DIDLLite.CONTAINER)) {

            this.node = node;

            int attrLength = node.getNAttributes();
            for (int i = 0; i < attrLength; i++) {
                Attribute attr = node.getAttribute(i);

                if (attr.getName().equals("id")) {
                    id = attr.getValue();
                } else if (attr.getName().equals("parentID")) {
                    parentId = attr.getValue();
                } else if (attr.getName().equals("childCount")) {
                    childCount = attr.getValue();
                } else if (attr.getName().equals("restricted")) {
                    setRestricted(attr.getValue());
                }
            }

            int length = node.getNNodes();
            for (int l = 0; l < length; l++) {
                Node childNode = node.getNode(l);

                if (childNode.getName().equals(DC.TITLE)) {
                    title = childNode.getValue();
                } else if (childNode.getName().equals(UPnP.CLASS)) {
                    objectClass = childNode.getValue();
                } else if (childNode.getName().equals(DC.DATE)) {
                    date = childNode.getValue();
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getObjectClass() {
        return objectClass;
    }

    @Override
    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getChildCount() {
        return childCount;
    }

    public void setChildCount(String childCount) {
        this.childCount = childCount;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getRestricted() {
        return restricted;
    }

    @Override
    public void setRestricted(String restricted) {
        this.restricted = restricted;
    }

    @Override
    public Node getNode() {
        return node;
    }

}
