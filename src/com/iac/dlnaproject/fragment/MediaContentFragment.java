
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.BrowseResult;
import com.iac.dlnaproject.BrowseTask;
import com.iac.dlnaproject.BrowseTask.BrowseCallback;
import com.iac.dlnaproject.BrowseTask.BrowseParams;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.adapter.ContentAdapter;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.model.UIEventHelper;
import com.iac.dlnaproject.nowplay.ContainerItem;
import com.iac.dlnaproject.nowplay.Item;
import com.iac.dlnaproject.nowplay.MediaItem;
import com.iac.dlnaproject.nowplay.MediaItem.ResInfo;
import com.iac.dlnaproject.provider.MediaItemMetaData;
import com.iac.dlnaproject.provider.MediaItemMetaData.ResMetaData;
import com.iac.dlnaproject.provider.PlayQueueMetaData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MediaContentFragment extends RefreshableBaseFragment implements OnItemClickListener,
BrowseCallback {

    private ContentAdapter mBrowseAdapter;
    private View mContentView;
    private ListView mListView;

    private List<Item> browseList;
    private BrowseParams mBrowseParams;
    private BrowseTask mBrowseTask;

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new MediaContentFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    public MediaContentFragment() {
        browseList = new ArrayList<Item>();
        setBrowseParams(new BrowseParams());
    }

    protected void setBrowseParams(BrowseParams browseParams) {
        mBrowseParams = browseParams;
    }

    protected BrowseParams getBrowseParams() {
        return mBrowseParams;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.list, null);
        mListView = (ListView)mContentView.findViewById(R.id.browse_list);
        mListView.setOnItemClickListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setContentView(mContentView);
        mListView.setEmptyView(view.findViewById(R.id.empty_area));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mBrowseTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public void obtainData() {
        // Hide the list
        setContentShown(false);
        mBrowseTask = new BrowseTask();
        mBrowseTask.setCallback(this);
        mBrowseTask.execute(getBrowseParams());
    }

    @Override
    public void onUpdateView() {
        if (!browseList.isEmpty()) {
            if (mBrowseAdapter == null) {
                mBrowseAdapter = new ContentAdapter(getActivity(), browseList);
                mListView.setAdapter(mBrowseAdapter);
            } else {
                mBrowseAdapter.refreshData(browseList);
            }
            setContentEmpty(false);
        } else {
            setContentEmpty(true);
            mBrowseAdapter.refreshData(browseList);
        }
    }

    @Override
    public void onGetItems(BrowseResult result) {
        browseList = new ArrayList<Item>();
        if (result.getResult() != null) {
            browseList.addAll(result.getResult());
        }
        updateView();
    }

    @Override
    public void receive(UIEvent message) {
        switch (UIEventHelper.match(message)) {
            case UIEventHelper.ON_BACK_PRESSED:
                BrowseResult content = (BrowseResult)message.getObject();
                browseList = new ArrayList<Item>();
                setBrowseParams(content.getParams());
                if (content != null) {
                    browseList.addAll(content.getResult());
                }
                updateView();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item)parent.getItemAtPosition(position);

        if (item instanceof ContainerItem) {
            BrowseParams b = new BrowseParams();
            b.objectId = item.getId();
            setBrowseParams(b);
            obtainData();
        } else if (item instanceof MediaItem) {
            UIEvent message = UIEvent.create(UIEvent.TYPE_ITEM_SELECTED);
            message.setObject(item);
            send(message);

            ContentResolver r = getActivity().getContentResolver();

            MediaItem mediaItem = (MediaItem)item;
            ContentValues cv = new ContentValues();
            cv.put(MediaItemMetaData.ALBUM, mediaItem.getAlbum());
            cv.put(MediaItemMetaData.ALBUM_ART_URI, mediaItem.getAlbumarturi());
            cv.put(MediaItemMetaData.OBJECT_CLASS, mediaItem.getObjectClass());
            cv.put(MediaItemMetaData.OBJECT_ID, mediaItem.getId());
            cv.put(MediaItemMetaData.ARTIST, mediaItem.getArtist());
            cv.put(MediaItemMetaData.PARENT_ID, mediaItem.getParentId());
            cv.put(MediaItemMetaData.RESTRICTED, mediaItem.getRestricted());
            cv.put(MediaItemMetaData.DATE, mediaItem.getDate());
            cv.put(MediaItemMetaData.TITLE, mediaItem.getTitle());
            Uri mediaItemUri = r.insert(MediaItemMetaData.CONTENT_URI, cv);
            cv.clear();
            ResInfo res = mediaItem.getRes();
            String mediaId = mediaItemUri.getPathSegments().get(1);
            cv.put(ResMetaData.RES, res.res);
            cv.put(ResMetaData.MEDIA_ID, mediaId);
            cv.put(ResMetaData.PROTOCOL_INFO, res.protocolInfo);
            cv.put(ResMetaData.RESOLUTION, res.resolution);
            cv.put(ResMetaData.SIZE, res.size);
            cv.put(ResMetaData.DURATION, res.duration);
            cv.clear();

            cv.put(PlayQueueMetaData.ITEM_ID, mediaId);
            r.insert(PlayQueueMetaData.CONTENT_URI, cv);
        }
    }

}
