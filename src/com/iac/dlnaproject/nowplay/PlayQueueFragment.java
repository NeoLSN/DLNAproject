
package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.ControllerProxy;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.adapter.PlayQueueAdapter;
import com.iac.dlnaproject.fragment.BaseFragment;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.model.UIEventHelper;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PlayQueueFragment extends BaseFragment implements OnItemClickListener {

    private DragSortListView mQueueList;
    private DragSortController mController;
    private PlayQueueAdapter mBrowseAdapter;
    private List<MediaItem> mPlayQueue;
    private View mEmptyView;

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new PlayQueueFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    public PlayQueueFragment() {
        super();
        mPlayQueue = new ArrayList<MediaItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.content_play_queue, container, false);
        mQueueList = (DragSortListView)contentView.findViewById(R.id.queue_list);
        mQueueList.setDropListener(onDrop);
        mQueueList.setRemoveListener(onRemove);

        mQueueList.setOnItemClickListener(this);

        mEmptyView = inflater.inflate(R.layout.empty_view, null);

        mController = buildController(mQueueList);
        mQueueList.setFloatViewManager(mController);
        mQueueList.setOnTouchListener(mController);
        mQueueList.setDragEnabled(true);

        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ViewGroup)mQueueList.getParent()).addView(mEmptyView);
        mQueueList.setEmptyView(mEmptyView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // same as restoreInstanceState on activity
        super.onActivityCreated(savedInstanceState);
        updateQueueData();
    }

    public void updateQueueData() {
        if (!mPlayQueue.isEmpty()) {
            if (mBrowseAdapter == null) {
                mBrowseAdapter = new PlayQueueAdapter(getActivity(), mPlayQueue);
                mQueueList.setAdapter(mBrowseAdapter);
            } else {
                mBrowseAdapter.refreshData(mPlayQueue);
            }
        }
    }

    @Override
    public void receive(UIEvent message) {
        switch (UIEventHelper.match(message)) {
            case UIEventHelper.ITEM_SELECTED:
                MediaItem item = (MediaItem)message.getObject();
                mPlayQueue.add(item);
                updateQueueData();
                break;
        }

    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            MediaItem item=mBrowseAdapter.getItem(from);

            mBrowseAdapter.remove(item);
            mBrowseAdapter.insert(item, to);
            mBrowseAdapter.notifyDataSetChanged();
        }
    };

    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            mBrowseAdapter.remove(mBrowseAdapter.getItem(which));
        }
    };

    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.row_icon);
        controller.setClickRemoveId(R.id.row_icon_action_1);
        controller.setRemoveEnabled(true);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DRAG);
        controller.setRemoveMode(DragSortController.CLICK_REMOVE);
        controller.setBackgroundColor(android.graphics.Color.WHITE);
        return controller;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MediaItem item = (MediaItem)parent.getItemAtPosition(position);
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ControllerProxy ctrlProxy = ControllerProxy.getInstance();
                ctrlProxy.getControlPoint().play(ctrlProxy.getSelectedRenderer(), item);
            }

        };
        Thread t = new Thread(r);t.start();
    }

}
