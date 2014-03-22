
package com.iac.dlnaproject.loader;

import com.iac.dlnaproject.ControllerProxy;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class MediaContentLoader extends AsyncTaskLoader<BrowseResult> {

    private BrowseParams param;
    private BrowseResult result;

    public MediaContentLoader(Context context) {
        super(context);
    }

    public void setParameters(BrowseParams params) {
        this.param = params;
    }

    @Override
    public BrowseResult loadInBackground() {
        if (param == null)
            param = new BrowseParams();
        BrowseResult result = new BrowseResult(param, null);
        try {
            ControllerProxy ctrlProxy = ControllerProxy.getInstance();
            result = ctrlProxy.getContentDirectory(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged() || result == null) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(BrowseResult data) {
        if (isReset()) {
            if (data != null) {

            }
        }
        result = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(BrowseResult data) {
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        if (result != null) {
            result = null;
        }

    }
}
