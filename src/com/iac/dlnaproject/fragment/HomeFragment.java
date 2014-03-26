
package com.iac.dlnaproject.fragment;

import com.iac.dlnaproject.ControllerProxy;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.activity.BrowserActivity;
import com.iac.dlnaproject.patterns.Observable;
import com.iac.dlnaproject.patterns.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends BaseFragment implements Observer {
    private View mContentView;
    private ControllerProxy mCtrlProxy;

    private TextView sourceBtn;
    private TextView sinkBtn;
    private TextView mediaBtn;

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new HomeFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mCtrlProxy = ControllerProxy.getInstance();
        mCtrlProxy.regesiterObserver(this);

        initMainButtons();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ensureMainButtons();
    }

    @Override
    public void onDestroyView() {
        mCtrlProxy.unregesiterObserver(this);
        super.onDestroyView();
    }

    private void initMainButtons() {
        sourceBtn = (TextView)getView().findViewById(R.id.source_browse_button);
        sourceBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(BrowserActivity.BROWSE_TYPE, BrowserActivity.BROWSE_TYPE_SOURCE);
                startActivity(intent);
            }

        });
        sinkBtn = (TextView)getView().findViewById(R.id.sink_browse_button);
        sinkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(BrowserActivity.BROWSE_TYPE, BrowserActivity.BROWSE_TYPE_SINK);
                startActivity(intent);
            }

        });
        mediaBtn = (TextView)getView().findViewById(R.id.media_browse_button);
        mediaBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(BrowserActivity.BROWSE_TYPE, BrowserActivity.BROWSE_TYPE_MEDIA);
                startActivity(intent);
            }

        });
    }

    public void ensureMainButtons() {
        if (mCtrlProxy.isMeidaPlayerWorking()) {
            sourceBtn.setEnabled(true);
        } else {
            sourceBtn.setEnabled(false);
        }
        if (mCtrlProxy.isMeidaServerWorking() && mCtrlProxy.isMeidaPlayerWorking()) {
            mediaBtn.setEnabled(true);
        } else {
            mediaBtn.setEnabled(false);
        }
    }

    @Override
    public void update(Observable observable) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ensureMainButtons();
            }
        };
        getActivity().runOnUiThread(r);
    }

}
