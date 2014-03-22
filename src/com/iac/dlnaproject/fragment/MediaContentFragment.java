
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.adapter.ContentAdapter;
import com.iac.dlnaproject.loader.BrowseParams;
import com.iac.dlnaproject.loader.BrowseResult;
import com.iac.dlnaproject.loader.MediaContentLoader;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.model.UIEventHelper;
import com.iac.dlnaproject.nowplay.ContainerItem;
import com.iac.dlnaproject.nowplay.Item;
import com.iac.dlnaproject.nowplay.MediaItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MediaContentFragment extends RefreshableBaseFragment implements OnItemClickListener,
LoaderCallbacks<BrowseResult> {

    private ContentAdapter mBrowseAdapter;
    private ListView mListView;
    private View mEmptyView;

    private List<Item> browseList;
    private BrowseParams mBrowseParams;

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
        View contentView = inflater.inflate(R.layout.list, container, false);
        mListView = (ListView)contentView.findViewById(R.id.browse_list);
        mListView.setOnItemClickListener(this);
        mEmptyView = inflater.inflate(R.layout.empty_view, null);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((ViewGroup)mListView.getParent()).addView(mEmptyView);
        mListView.setEmptyView(mEmptyView);
        super.onViewCreated(view, savedInstanceState);
        browseList = new ArrayList<Item>();
        mBrowseAdapter = new ContentAdapter(getActivity(), browseList);
        mListView.setAdapter(mBrowseAdapter);
    }

    @Override
    public void onDestroyView() {
        Loader loader = getLoaderManager().getLoader(0);
        if (loader != null) {
            loader.stopLoading();
        }
        super.onDestroyView();
    }

    @Override
    public void obtainData() {
        Loader loader = getLoaderManager().getLoader(0);
        if (loader == null) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            loader.reset();
            ((MediaContentLoader)loader).setParameters(getBrowseParams());
            loader.startLoading();
        }
    }

    @Override
    public void onUpdateView() {
        mBrowseAdapter.refreshData(browseList);
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

            // ContentResolver r = getActivity().getContentResolver();
            //
            // MediaItem mediaItem = (MediaItem)item;
            // ContentValues cv = new ContentValues();
            // cv.put(MediaItemMetaData.ALBUM, mediaItem.getAlbum());
            // cv.put(MediaItemMetaData.ALBUM_ART_URI,
            // mediaItem.getAlbumarturi());
            // cv.put(MediaItemMetaData.OBJECT_CLASS,
            // mediaItem.getObjectClass());
            // cv.put(MediaItemMetaData.OBJECT_ID, mediaItem.getId());
            // cv.put(MediaItemMetaData.ARTIST, mediaItem.getArtist());
            // cv.put(MediaItemMetaData.PARENT_ID, mediaItem.getParentId());
            // cv.put(MediaItemMetaData.RESTRICTED, mediaItem.getRestricted());
            // cv.put(MediaItemMetaData.DATE, mediaItem.getDate());
            // cv.put(MediaItemMetaData.TITLE, mediaItem.getTitle());
            // Uri mediaItemUri = r.insert(MediaItemMetaData.CONTENT_URI, cv);
            // cv.clear();
            // ResInfo res = mediaItem.getRes();
            // String mediaId = mediaItemUri.getPathSegments().get(1);
            // cv.put(ResMetaData.RES, res.res);
            // cv.put(ResMetaData.MEDIA_ID, mediaId);
            // cv.put(ResMetaData.PROTOCOL_INFO, res.protocolInfo);
            // cv.put(ResMetaData.RESOLUTION, res.resolution);
            // cv.put(ResMetaData.SIZE, res.size);
            // cv.put(ResMetaData.DURATION, res.duration);
            // cv.clear();
            //
            // cv.put(PlayQueueMetaData.ITEM_ID, mediaId);
            //
            // String[] cols = new String[] {
            // "count(*)"
            // };
            // Uri uri = PlayQueueMetaData.CONTENT_URI;
            // Cursor cur = r.query(uri, cols, null, null, null);
            // cur.moveToFirst();
            // int count = cur.getInt(0);
            // cur.close();
            // cv.put(PlayQueueMetaData.PLAYER_ORDER, count);
            //
            // r.insert(PlayQueueMetaData.CONTENT_URI, cv);
        }
    }

    @Override
    public Loader<BrowseResult> onCreateLoader(int id, Bundle args) {
        MediaContentLoader loader = new MediaContentLoader(getActivity());
        loader.setParameters(getBrowseParams());
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<BrowseResult> loader, BrowseResult data) {
        browseList = new ArrayList<Item>();
        if (data.getResult() != null) {
            browseList.addAll(data.getResult());
        }
        updateView();
    }

    @Override
    public void onLoaderReset(Loader<BrowseResult> data) {
        browseList = new ArrayList<Item>();
    }

}
