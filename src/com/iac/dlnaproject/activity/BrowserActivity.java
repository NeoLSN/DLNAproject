
package com.iac.dlnaproject.activity;

import com.iac.dlnaproject.BrowseResult;
import com.iac.dlnaproject.ControllerProxy;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.fragment.MediaContentFragment;
import com.iac.dlnaproject.fragment.MediaRendererFragment;
import com.iac.dlnaproject.fragment.MediaServerFragment;
import com.iac.dlnaproject.model.UIEvent;
import com.iac.dlnaproject.model.UIEventHelper;
import com.iac.dlnaproject.nowplay.PlayQueueFragment;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

public class BrowserActivity extends FunctionBaseActivity {

    public static final String BROWSE_TYPE = "browse_type";
    public static final int BROWSE_TYPE_SOURCE = 0;
    public static final int BROWSE_TYPE_SINK = 1;
    public static final int BROWSE_TYPE_MEDIA = 2;

    private int browseType;

    protected Fragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null)
            browseType = getIntent().getIntExtra(BROWSE_TYPE, -1);
        if (savedInstanceState != null) {
            mFrag = getMainContentFragment();
            browseType = savedInstanceState.getInt(BROWSE_TYPE, -1);
        }
        setContent();
    }

    private void setContent() {
        switch (browseType) {
            case BROWSE_TYPE_SOURCE:
                ViewPager mPager = getViewPager();
                mPager.setVisibility(View.VISIBLE);
                getMainLayout().setVisibility(View.GONE);
                ViewAdapter mAdapter = new ViewAdapter(getSupportFragmentManager());
                mPager.setAdapter(mAdapter);
                PageIndicator mIndicator = getPageIndicator();
                mIndicator.setViewPager(mPager);
                ((View)mIndicator).setVisibility(View.VISIBLE);
                ((TitlePageIndicator)mIndicator).setFooterIndicatorStyle(IndicatorStyle.Triangle);
                break;
            case BROWSE_TYPE_SINK:
                getViewPager().setVisibility(View.GONE);
                getMainLayout().setVisibility(View.VISIBLE);
                setMainContentFragment(MediaRendererFragment.getInstance(null));
                break;
            case BROWSE_TYPE_MEDIA:
                getViewPager().setVisibility(View.GONE);
                getMainLayout().setVisibility(View.VISIBLE);
                setMainContentFragment(MediaContentFragment.getInstance(null));
                setRightDrawerMenuFragment(PlayQueueFragment.getInstance(null));
                break;
            default:
                finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BROWSE_TYPE, browseType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (browseType == BROWSE_TYPE_MEDIA) {
            ControllerProxy ctrlProxy = ControllerProxy.getInstance();
            ctrlProxy.popItems();
            BrowseResult cache = ctrlProxy.peekItems();
            if (cache != null) {
                UIEvent message = UIEvent.create(UIEvent.TYPE_ON_BACK_PRESSED);
                message.setObject(cache);
                send(message);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerMenuOpened(View drawerView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDrawerMenuClosed(View drawerView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void receive(UIEvent event) {
        switch (UIEventHelper.match(event)) {
            case UIEventHelper.ITEM_SELECTED_AND_COMPLETED:
                finish();
                break;
        }
    }

    private static final String[] CONTENT = new String[] {
        "Recent", "Artists"
    };

    public static class ViewAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public ViewAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MediaServerFragment.getInstance(null);
                case 1:
                    return MediaRendererFragment.getInstance(null);
                default:
                    return MediaServerFragment.getInstance(null);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getIconResId(int index) {
            return 0;
        }
    }
}
