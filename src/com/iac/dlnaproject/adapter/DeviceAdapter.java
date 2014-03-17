
package com.iac.dlnaproject.adapter;

import org.cybergarage.upnp.Device;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class DeviceAdapter extends BaseAdapterWithViewHolder<Device> {

    public DeviceAdapter(Context context, List<Device> devices) {
        super(context, devices);
    }

    @Override
    public void onGetView(ItemViewHolder viewHolder, int position, View convertView,
            ViewGroup parent) {
        Device device = getItem(position);
        viewHolder.iconImage.setImageResource(android.R.drawable.ic_menu_upload_you_tube);
        viewHolder.itemTitle.setText(device.getFriendlyName());
    }

}
