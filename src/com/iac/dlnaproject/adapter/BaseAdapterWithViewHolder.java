
package com.iac.dlnaproject.adapter;

import com.iac.dlnaproject.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public abstract class BaseAdapterWithViewHolder<T> extends ArrayAdapter<T> {

    private LayoutInflater mInflater;
    private List<T> contentItems;

    public BaseAdapterWithViewHolder(Context context, List<T> contentItem) {
        super(context, R.layout.row, contentItem);
        mInflater = LayoutInflater.from(context);
        contentItems = contentItem;
    }

    public void refreshData(List<T> contentItem) {
        if (contentItems != null && !contentItems.equals(contentItem)) {
            clear();
            for (T item : contentItem) {
                this.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.iconImage = (ImageView)convertView.findViewById(R.id.row_icon);
            viewHolder.itemTitle = (TextView)convertView.findViewById(R.id.row_title);
            viewHolder.itemSubtitle = (TextView)convertView.findViewById(R.id.row_subtitle);
            viewHolder.itemActionImage1 = (ImageView)convertView
                    .findViewById(R.id.row_icon_action_1);
            viewHolder.itemActionImage2 = (ImageView)convertView
                    .findViewById(R.id.row_icon_action_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)convertView.getTag();
        }

        onGetView(viewHolder, position, convertView, parent);

        return convertView;
    }

    public abstract void onGetView(ItemViewHolder viewHolder, int position, View convertView,
            ViewGroup parent);

}
