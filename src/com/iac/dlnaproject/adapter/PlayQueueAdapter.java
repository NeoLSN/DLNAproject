
package com.iac.dlnaproject.adapter;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.nowplay.Item;
import com.iac.dlnaproject.nowplay.MediaItem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PlayQueueAdapter extends BaseAdapterWithViewHolder<MediaItem> {

    private List<MediaItem> contentItem;
    private LayoutInflater mInflater;
    private Context mContext;

    private Drawable folderIcon;
    private Drawable musicIcon;
    private Drawable picIcon;
    private Drawable videoIcon;

    public PlayQueueAdapter(Context context, List<MediaItem> contentItem) {
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
        Item dataItem = getItem(position);
        viewHolder.itemTitle.setText(dataItem.getTitle());
        viewHolder.iconImage.setImageResource(R.drawable.icon_schedule_mark);
        viewHolder.itemActionImage1.setImageResource(R.drawable.ic_action_cancel);
        viewHolder.itemActionImage1.setVisibility(View.VISIBLE);
    }
}
