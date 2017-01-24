package com.example.q.shareplaylist;

import android.content.Context;
import android.graphics.Color;
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
    private String currentPlayingVideo_id = "";

    public VideoAdapter() {
        videos = new ArrayList<>();
    }

    public VideoAdapter(String currentPlayingVideo_id) {
        this.currentPlayingVideo_id = currentPlayingVideo_id;
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

        if(currentPlayingVideo_id.length()>0 && videos.get(position).get_id().equals(currentPlayingVideo_id)) {
            convertView.findViewById(R.id.item_video_layout).setBackgroundColor(0x33e16060);
            convertView.findViewById(R.id.item_video_overlay).setVisibility(View.GONE);
        } else if(videos.get(position).hasStartedAt() && videos.get(position).getStartedAt() < 10000000000000L) {
            convertView.findViewById(R.id.item_video_layout).setBackgroundColor(0xffffff);
            convertView.findViewById(R.id.item_video_overlay).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.item_video_layout).setBackgroundColor(0xffffff);
            convertView.findViewById(R.id.item_video_overlay).setVisibility(View.GONE);
        }

        return convertView;
    }

    public void clear() {
        videos.clear();
        notifyDataSetChanged();
    }

    public void add(String url, String title, String uploader, String thumbnail) {
        videos.add(new VideoData(url, title, uploader, thumbnail));
        notifyDataSetChanged();
    }

    public void add(String url, String title, String uploader, String thumbnail, String playerID,
                    String playerName, Long startedAt, Long duration, int like) {
        videos.add(new VideoData(url, title, uploader, thumbnail, playerID, playerName, startedAt, duration, like));
        notifyDataSetChanged();
    }

    public void add(String _id, String url, String title, String uploader, String thumbnail, String playerID,
                    String playerName, Long startedAt, Long duration, int like) {
        videos.add(new VideoData(_id, url, title, uploader, thumbnail, playerID, playerName, startedAt, duration, like));
        notifyDataSetChanged();
    }

    public void add(VideoData videoData) {
        videos.add(videoData);
        notifyDataSetChanged();
    }

    public void setCurrentPlayingVideo_id(String currentPlayingVideo_id) {
        this.currentPlayingVideo_id = currentPlayingVideo_id;
        notifyDataSetChanged();
    }

    public String getCurrentPlayingVideo_id() {
        return currentPlayingVideo_id;
    }
}

class VideoData {
    private String _id;
    private String url;
    private String title;
    private String uploader;
    private String thumbnail;

    private String playerID;
    private String playerName;
    private Long startedAt;
    private Long duration;
    private int like;

    public VideoData(String _id, String url, String title, String uploader, String thumbnail, String playerID, String playerName, Long startedAt, Long duration, Integer like) {
        this._id = _id;
        this.url = url;
        this.title = title;
        this.uploader = uploader;
        this.thumbnail = thumbnail;
        this.playerID = playerID;
        this.playerName = playerName;
        this.startedAt = startedAt;
        this.duration = duration;
        this.like = like;
    }

    public VideoData(String url, String title, String uploader, String thumbnail) {
        this._id = "";
        this.url = url;
        this.title = title;
        this.uploader = uploader;
        this.thumbnail = thumbnail;
    }

    public VideoData(String url, String title, String uploader, String thumbnail, String playerID, String playerName, Long startedAt, Long duration, int like) {
        this._id = "";
        this.url = url;
        this.title = title;
        this.uploader = uploader;
        this.thumbnail = thumbnail;
        this.playerID = playerID;
        this.playerName = playerName;
        this.startedAt = startedAt;
        this.duration = duration;
        this.like = like;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public boolean hasStartedAt() { return this.startedAt!=null; }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
