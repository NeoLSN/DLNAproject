
package com.iac.dlnaproject.nowplay;

import org.cybergarage.xml.Node;

public interface Item {

    public Node getNode();

    public String getId();

    public void setId(String id);

    public String getTitle();

    public void setTitle(String title);

    public String getDate();

    public void setDate(String date);

    public String getObjectClass();

    public void setObjectClass(String objectClass);

    public String getParentId();

    public void setParentId(String parentId);

    public String getRestricted();

    public void setRestricted(String restricted);
}
