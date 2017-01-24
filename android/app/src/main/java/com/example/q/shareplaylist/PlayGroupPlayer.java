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
    private View view;
    private TextView uploader;
    private TextView player;
    private TextView title;
    private TextView group;

    private JsonObject result;
    private boolean playing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play_group_player, container, false);
        uploader=(TextView)view.findViewById(R.id.uploader);
        title= (TextView)view.findViewById(R.id.title);
        player=(TextView)view.findViewById(R.id.player);
        group=(TextView)view.findViewById(R.id.playgroup_player_groupName);

//        setTypeface();
        setPlayingInfo();

        group.setText(MainActivity.currentGroupName);

        return view;
    }

    /*
    private void setTypeface(){
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "Cassandra.ttf");
        TextView uploaded = (TextView)view.findViewById(R.id.textView2);
        TextView played= (TextView)view.findViewById(R.id.textView3);
        uploaded.setTypeface(myTypeface);
        played.setTypeface(myTypeface);
    }
    */

    public void setPlayingInfoJson(JsonObject result, boolean setplaying){
        this.result=result;
        playing=setplaying;
        setPlayingInfo();
    }

    public void setPlayingInfo(){
        if(!playing) {
            view.findViewById(R.id.playgroup_player_information).setVisibility(View.GONE);
            view.findViewById(R.id.playgroup_player_novideo).setVisibility(View.VISIBLE);
            return;
        }

        view.findViewById(R.id.playgroup_player_information).setVisibility(View.VISIBLE);
        view.findViewById(R.id.playgroup_player_novideo).setVisibility(View.GONE);
        try {
            if(playing) {
                title.setText(URLDecoder.decode(result.get("title").getAsString(), "utf-8"));
                uploader.setText(URLDecoder.decode(result.get("uploader").getAsString(), "utf-8"));
                player.setText(URLDecoder.decode(result.get("playerName").getAsString(), "utf-8"));
            }
            else{
                title.setText("");
                uploader.setText("");
                player.setText("");
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
