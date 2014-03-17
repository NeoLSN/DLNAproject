
package com.iac.dlnaproject;

import com.iac.dlnaproject.BrowseTask.BrowseParams;
import com.iac.dlnaproject.nowplay.Item;

import android.os.AsyncTask;

import java.util.List;

public class BrowseTask extends AsyncTask<BrowseParams, Void, BrowseResult> {

    public static interface BrowseCallback {
        public void onGetItems(final BrowseResult content);
    }

    public static class BrowseParams {
        public String objectId = "0";
    }

    private BrowseCallback mCallback;
    private BrowseParams param;

    public void setCallback(BrowseCallback callback) {
        mCallback = callback;
    }

    @Override
    protected BrowseResult doInBackground(BrowseParams... params) {
        param = params[0];
        List<Item> contentNode = null;
        BrowseResult result = new BrowseResult(param, null);
        if (param != null) {
            try {
                ControllerProxy ctrlProxy = ControllerProxy.getInstance();
                result = ctrlProxy.getContentDirectory(param);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    protected void onPostExecute(BrowseResult result) {
        if (mCallback != null) {
            mCallback.onGetItems(result);
        }
        super.onPostExecute(result);
    }
}
