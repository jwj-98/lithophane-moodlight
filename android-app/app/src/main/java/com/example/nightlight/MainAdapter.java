package com.example.nightlight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<MainDeviceData> mdata;

    public MainAdapter(Context context, ArrayList<MainDeviceData> data){
        mContext = context;
        mdata = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.main_list_item, null);

        TextView deviceNick = (TextView)view.findViewById(R.id.txt_name);

        deviceNick.setText(mdata.get(position).getNick());

        return view;
    }
}
