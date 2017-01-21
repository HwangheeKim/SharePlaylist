package com.example.q.shareplaylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-20.
 */

public class GroupAdapter extends BaseAdapter {
    ArrayList<GroupData> groupDatas;

    public GroupAdapter() {
        groupDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return groupDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return groupDatas.get(position);
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

        ((TextView)convertView.findViewById(R.id.item_group_name))
                .setText(groupDatas.get(position).getGroupName());
        ((TextView)convertView.findViewById(R.id.item_group_creator))
                .setText(groupDatas.get(position).getCreatorName());
        ((TextView)convertView.findViewById(R.id.item_group_status))
                .setText(Integer.toString(groupDatas.get(position).getStatus()));

        // TODO : Set Thumbnail

        return convertView;
    }

    public void add(String groupID, String groupName, String creatorID, String creatorName, String thumbnail, int status) {
        groupDatas.add(new GroupData(groupID, groupName, creatorID, creatorName, thumbnail, status));
    }

    public void add(GroupData groupData) {
        groupDatas.add(groupData);
    }

    public void clear() {
        groupDatas.clear();
    }
}

class GroupData {
    private String groupID;
    private String groupName;
    private String creatorID;
    private String creatorName;
    private String thumbnail;
    private int status; // People in the group

    public GroupData(String groupID, String groupName, String creatorID, String creatorName, String thumbnail, int status) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.creatorID = creatorID;
        this.creatorName = creatorName;
        this.thumbnail = thumbnail;
        this.status = status;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
