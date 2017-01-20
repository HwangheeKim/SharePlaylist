package com.example.q.shareplaylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-20.
 */

public class GroupAdapter extends BaseAdapter {
    ArrayList<String> test;

    public GroupAdapter() {
        test = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return test.size();
    }

    @Override
    public Object getItem(int position) {
        return test.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_group, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.item_group_name)).setText(test.get(position));

        return convertView;
    }

    public void add(String item) {
        test.add(item);
    }
}
