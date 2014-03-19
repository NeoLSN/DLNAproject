
package com.iac.dlnaproject.loader;

import com.iac.dlnaproject.nowplay.Item;

import java.util.Collection;

public class BrowseResult {
    private BrowseParams params;
    private Collection<? extends Item> result;

    public BrowseResult(BrowseParams params, Collection<? extends Item> result) {
        this.setParams(params);
        this.setResult(result);
    }

    public BrowseParams getParams() {
        return params;
    }

    public void setParams(BrowseParams params) {
        this.params = params;
    }

    public Collection<? extends Item> getResult() {
        return result;
    }

    public void setResult(Collection<? extends Item> result) {
        this.result = result;
    }

}
