
package com.iac.dlnaproject.adapter;

import com.iac.dlnaproject.R;
import com.iac.dlnaproject.nowplay.ContainerItem;
import com.iac.dlnaproject.nowplay.Item;
import com.iac.dlnaproject.nowplay.MediaItem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PlayQueueAdapter extends BaseAdapterWithViewHolder<Item> {

    private List<Item> contentItem;
    private LayoutInflater mInflater;
    private Context mContext;

    private Drawable folderIcon;
    private Drawable musicIcon;
    private Drawable picIcon;
    private Drawable videoIcon;

    public PlayQueueAdapter(Context context, List<Item> contentItem) {
        super(context, contentItem);

        Resources res = context.getResources();
        folderIcon = res.getDrawable(R.drawable.ic_action_collection);
        musicIcon = res.getDrawable(R.drawable.ic_media_audio);
        // picIcon = res.getDrawable(R.drawable.tab_icon_pic);
        // videoIcon = res.getDrawable(R.drawable.tab_icon_video);
    }

    @Override
    public void onGetView(ItemViewHolder viewHolder, int position, View convertView,
            ViewGroup parent) {
        Item dataItem = getItem(position);
        viewHolder.itemTitle.setText(dataItem.getTitle());
        if (dataItem instanceof ContainerItem) {
            viewHolder.iconImage.setImageResource(R.drawable.ic_action_computer);
        } else if (dataItem instanceof MediaItem) {
            viewHolder.iconImage.setImageResource(R.drawable.ic_media_audio);
        }
        viewHolder.itemActionImage1.setOnClickListener(null);
    }
}
