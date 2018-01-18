package com.test.yysleep.bluttoothtransmission.tool;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.yysleep.bluttoothtransmission.R;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.util.List;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class BlueToothBondDevicesAdapter extends BaseAdapter {

    private static final String TAG = "BlueToothDevicesAdapter";

    private List<BluetoothDevice> mList;

    public BlueToothBondDevicesAdapter(List<BluetoothDevice> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BlueViewHolder holder;
        final BluetoothDevice device = mList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            holder = new BlueViewHolder();
            holder.deviceNameTv = convertView.findViewById(R.id.item_bluetooth_device_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (BlueViewHolder) convertView.getTag();
        }
        if (device != null) {
            String name = device.getName();
            if (name == null) {
                name = device.getAddress() + "";
            }
            holder.deviceNameTv.setText(name);
        }
        return convertView;
    }

    private static class BlueViewHolder {
        private TextView deviceNameTv;
    }
}
