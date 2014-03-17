
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

public class MediaServerFragment extends RefreshableBaseFragment implements OnItemClickListener, Observer {

    private DeviceAdapter mDeviceAdapter;
    private View mContentView;
    private ListView mListView;

    private ControllerProxy mCtrlProxy;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateView();
        }
    };

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new MediaServerFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCtrlProxy = ControllerProxy.getInstance();
        mCtrlProxy.attach(this);
    }

    @Override
    public void onDestroyView() {
        mCtrlProxy.detach(this);
        mHandler.removeMessages(0);
        super.onDestroyView();
    }

    @Override
    public void obtainData() {
        setContentShown(false);
        mHandler.removeMessages(0);
        getActivity().startService(new Intent(MediaControllerService.RESET_SEARCH_DEVICES));
        //最久讓畫面刷個兩秒
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onUpdateView() {
        List<Device> browseList = mCtrlProxy.getServerDeviceList();

        if (!browseList.isEmpty()) {
            if (mDeviceAdapter == null) {
                mDeviceAdapter = new DeviceAdapter(getActivity(), browseList);
                mListView.setAdapter(mDeviceAdapter);
            } else {
                mDeviceAdapter.refreshData(browseList);
            }
            setContentEmpty(false);
        } else {
            setContentEmpty(true);
            if (mDeviceAdapter != null)
                mDeviceAdapter.refreshData(browseList);
        }
    }

    @Override
    public void receive(UIEvent message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(Observable observable) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Device d = (Device)parent.getItemAtPosition(position);
        mCtrlProxy.setSelectedServer(d);
        Log.i("Jason", d.getFriendlyName() + " => " + d.getUDN());
        UIEvent message = UIEvent.create(UIEvent.TYPE_ITEM_SELECTED_AND_COMPLETED);
        send(message);
    }

}
