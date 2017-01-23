package com.example.q.shareplaylist;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.URLDecoder;

public class PlayGroupPlayer extends Fragment {
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private int initTime;

    private View view;
    private TextView uploader;
    private TextView player;
    private TextView title;

    private JsonObject result = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play_group_player, container, false);
        uploader=(TextView)view.findViewById(R.id.uploader);
        title= (TextView)view.findViewById(R.id.title);
        player=(TextView)view.findViewById(R.id.player);
        setTypeface();
        setPlayingInfo();

        return view;
    }

    private void setTypeface(){
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "Cassandra.ttf");
        TextView uploaded = (TextView)view.findViewById(R.id.textView2);
        TextView played= (TextView)view.findViewById(R.id.textView3);
        uploaded.setTypeface(myTypeface);
        played.setTypeface(myTypeface);
    }

    public void setPlayingInfoJson(JsonObject result){
        this.result=result;
    }

    public void setPlayingInfo(){
        if(result==null)
            return;
        try {
            title.setText(URLDecoder.decode(result.get("title").getAsString(), "utf-8"));
            uploader.setText(URLDecoder.decode(result.get("uploader").getAsString(), "utf-8"));
            player.setText(URLDecoder.decode(result.get("playerName").getAsString(), "utf-8"));
        }catch(Exception e){e.printStackTrace();}
    }


}
