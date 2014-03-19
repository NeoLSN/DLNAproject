
package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.adapter.PlayQueueAdapter;
import com.iac.dlnaproject.fragment.BaseFragment;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.model.UIEventHelper;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.cybergarage.xml.Node;
import org.cybergarage.xml.ParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PlayQueueFragment extends BaseFragment implements OnItemClickListener {

    private View mContentView;

    private DragSortListView mQueueList;
    private DragSortController mController;
    private PlayQueueAdapter mBrowseAdapter;
    private List<Item> mPlayQueue;
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
        mPlayQueue = new ArrayList<Item>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.content_play_queue, null);
        mQueueList = (DragSortListView)mContentView.findViewById(R.id.queue_list);
        mQueueList.setDropListener(onDrop);
        mQueueList.setRemoveListener(onRemove);

        mQueueList.setOnItemClickListener(this);

        mEmptyView = inflater.inflate(R.layout.empty_view, null);

        mController = buildController(mQueueList);
        mQueueList.setFloatViewManager(mController);
        mQueueList.setOnTouchListener(mController);
        mQueueList.setDragEnabled(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mQueueList.setEmptyView(mEmptyView);
        ((ViewGroup)mContentView).addView(mEmptyView);
        // Setup content view
        setContentView(mContentView);

        // same as restoreInstanceState on activity
        super.onActivityCreated(savedInstanceState);
        updateQueueData();
        setContentShown(true);
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
                Item item = (Item)message.getObject();
                mPlayQueue.add(item);
                updateQueueData();
                break;
        }

    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            Item item=mBrowseAdapter.getItem(from);

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
        return controller;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

    }

    public static class XmlParser extends org.cybergarage.xml.Parser {

        public Node parse(org.xmlpull.v1.XmlPullParser xpp, InputStream inStream)
                throws ParserException {
            Node rootNode = null;
            Node currNode = null;

            try {
                xpp.setInput(inStream, null);
                int eventType = xpp.getEventType();
                while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case org.xmlpull.v1.XmlPullParser.START_TAG: {
                            Node node = new Node();
                            String namePrefix = xpp.getPrefix();
                            String name = xpp.getName();
                            StringBuffer nodeName = new StringBuffer();
                            if (namePrefix != null && 0 < namePrefix.length()) {
                                nodeName.append(namePrefix);
                                nodeName.append(":");
                            }
                            if (name != null && 0 < name.length())
                                nodeName.append(name);
                            node.setName(nodeName.toString());
                            int attrsLen = xpp.getAttributeCount();
                            for (int n = 0; n < attrsLen; n++) {
                                String attrName = xpp.getAttributeName(n);
                                String attrValue = xpp.getAttributeValue(n);
                                node.setAttribute(attrName, attrValue);
                            }

                            if (currNode != null)
                                currNode.addNode(node);
                            currNode = node;
                            if (rootNode == null)
                                rootNode = node;
                        }
                        break;
                        case org.xmlpull.v1.XmlPullParser.TEXT: {
                            String value = xpp.getText();
                            if (value != null && currNode != null)
                                currNode.setValue(value);
                        }
                        break;
                        case org.xmlpull.v1.XmlPullParser.END_TAG: {
                            currNode = currNode.getParentNode();
                        }
                        break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                throw new ParserException(e);
            }

            return rootNode;
        }

        @Override
        public Node parse(InputStream inStream) throws ParserException {
            Node rootNode = null;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // factory.setNamespaceAware(true);
                org.xmlpull.v1.XmlPullParser xpp = factory.newPullParser();
                rootNode = parse(xpp, inStream);
            } catch (Exception e) {
                throw new ParserException(e);
            }

            return rootNode;
        }

    }

}
