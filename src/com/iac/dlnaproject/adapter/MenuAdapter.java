
package com.iac.dlnaproject.adapter;

import com.iac.dlnaproject.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MenuAdapter extends BaseAdapterWithViewHolder<String> {

    private List<String> contentItem;
    private LayoutInflater mInflater;
    private Context mContext;

    private Drawable folderIcon;
    private Drawable musicIcon;
    private Drawable picIcon;
    private Drawable videoIcon;

    public MenuAdapter(Context context, List<String> contentItem) {
        super(context, contentItem);

        Resources res = context.getResources();
        folderIcon = res.getDrawable(R.drawable.ic_action_collection);
        musicIcon = res.getDrawable(R.drawable.icon_schedule_mark);
        // picIcon = res.getDrawable(R.drawable.tab_icon_pic);
        // videoIcon = res.getDrawable(R.drawable.tab_icon_video);
    }

    @Override
    public void onGetView(ItemViewHolder viewHolder, int position, View convertView,
            ViewGroup parent) {
        String name = getItem(position);
        viewHolder.itemTitle.setText(name);
        viewHolder.iconImage.setVisibility(View.GONE);

    }
}
