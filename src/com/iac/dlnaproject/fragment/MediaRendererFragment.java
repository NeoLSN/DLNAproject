
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.ControllerProxy;
import com.iac.dlnaproject.MediaControllerService;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.adapter.DeviceAdapter;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.patterns.Observable;
import com.iac.dlnaproject.patterns.Observer;

import org.cybergarage.upnp.Device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

public class MediaRendererFragment extends RefreshableBaseFragment implements OnItemClickListener, Observer {

    private DeviceAdapter mDeviceAdapter;
    private ListView mListView;
    private View mEmptyView;

    private ControllerProxy mCtrlProxy;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateView();
        }
    };

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new MediaRendererFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mCtrlProxy = ControllerProxy.getInstance();
        mCtrlProxy.regesiterObserver(this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mCtrlProxy.unregesiterObserver(this);
        mHandler.removeMessages(0);
        super.onDestroyView();
    }

    @Override
    public void obtainData() {
        mHandler.removeMessages(0);
        getActivity().startService(new Intent(MediaControllerService.RESET_SEARCH_DEVICES));
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onUpdateView() {
        List<Device> browseList = mCtrlProxy.getRendererDeviceList();

        if (!browseList.isEmpty()) {
            if (mDeviceAdapter == null) {
                mDeviceAdapter = new DeviceAdapter(getActivity(), browseList);
                mListView.setAdapter(mDeviceAdapter);
            } else {
                mDeviceAdapter.refreshData(browseList);
            }

        } else {
            if (mDeviceAdapter != null)
                mDeviceAdapter.refreshData(browseList);
        }
    }

    @Override
    public void update(Observable observable) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Device d = (Device)parent.getItemAtPosition(position);
        mCtrlProxy.setPreferedRenderer(d);
        UIEvent message = UIEvent.create(UIEvent.TYPE_ITEM_SELECTED_AND_COMPLETED);
        send(message);
    }

}
