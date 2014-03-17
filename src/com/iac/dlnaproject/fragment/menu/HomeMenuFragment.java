
package com.iac.dlnaproject.fragment.menu;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.adapter.MenuAdapter;
import com.iac.dlnaproject.fragment.BaseFragment;
import com.iac.dlnaproject.model.UIEvent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class HomeMenuFragment extends BaseFragment implements OnItemClickListener {
    private View mContentView;
    private ListView mMenuListView;
    private String[] MENU_ITEMS;

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new HomeMenuFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.menu_home_left, null);
        mMenuListView = (ListView)mContentView.findViewById(R.id.drawer_menu);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Setup content view
        setContentView(mContentView);
        // same as restoreInstanceState on activity
        super.onActivityCreated(savedInstanceState);
        setDrawerMenu();
        setContentShown(true);
    }

    private void setDrawerMenu() {
        mMenuListView.setOnItemClickListener(this);
        MENU_ITEMS = getActivity().getResources().getStringArray(R.array.navigator_dlna_home);
        List<String> menuArray = Arrays.asList(MENU_ITEMS);
        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), menuArray);
        mMenuListView.setAdapter(menuAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UIEvent message = null;
        switch (position) {
            case 1:
                message = UIEvent.create(UIEvent.TYPE_IAC_CLOUD);
                break;
            case 0:
            default:
                message = UIEvent.create(UIEvent.TYPE_DLNA_HOME);
        }
        send(message);
    }

    @Override
    public void receive(UIEvent message) {

    }

}
