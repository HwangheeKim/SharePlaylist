package com.example.q.shareplaylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-22.
 */


public class VideoAdapter extends BaseAdapter {
    ArrayList<VideoData> videos;

    public VideoAdapter() {
        videos = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public VideoData getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_video, parent, false);
        }

        Picasso.with(convertView.getContext()).load(videos.get(position).getThumbnail())
                .into((ImageView)convertView.findViewById(R.id.item_video_thumbnail));
        ((TextView)convertView.findViewById(R.id.item_video_title))
                .setText(videos.get(position).getTitle());
        ((TextView)convertView.findViewById(R.id.item_video_uploader))
                .setText(videos.get(position).getUploader());

        return convertView;
    }

    public void clear() { videos.clear(); }

    public void add(String url, String title, String uploader, String thumbnail) {
        videos.add(new VideoData(url, title, uploader, thumbnail));
        notifyDataSetChanged();
    }

    public void add(VideoData videoData) {
        videos.add(videoData);
        notifyDataSetChanged();
    }
}

class VideoData {
    private String url;
    private String title;
    private String uploader;
    private String thumbnail;

    public VideoData(String url, String title, String uploader, String thumbnail) {
        this.url = url;
        this.title = title;
        this.uploader = uploader;
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
