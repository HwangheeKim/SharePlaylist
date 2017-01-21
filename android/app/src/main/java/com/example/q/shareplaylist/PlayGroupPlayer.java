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

import org.json.JSONObject;

public class PlayGroupPlayer extends Fragment {
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private String video="ePpPVE-GGJw";
    //private List<String> videos; private int startIndex;
    private int initTime;

    JSONObject jsonObject;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play_group_player, container, false);

        setTypeface();
        initYouTube();

        return view;
    }

    private void setTypeface(){
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "Cassandra.ttf");

        TextView uploaded = (TextView)view.findViewById(R.id.textView2);
        TextView played= (TextView)view.findViewById(R.id.textView3);
        uploaded.setTypeface(myTypeface);
        played.setTypeface(myTypeface);
    }

    private void initYouTube(){
        youTubePlayerFragment = (YouTubePlayerFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment3);
        //youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.view);

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(video, initTime);
                //youTubePlayer.loadVideo(videos, startIndex, initTime);
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.e("error", errorReason.toString());
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerFragment.initialize("AIzaSyDDN48pBGknlr4oU8_-HEY1d2gMerq5mxw", onInitializedListener);


        /*    FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment2, youTubePlayerFragment);
            fragmentTransaction.commit();*/

    }

//    @Override
//    public void onDestroyView(){
//        FragmentManager fragmentManager = getActivity().getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.remove(youTubePlayerFragment);
//        fragmentTransaction.commit();
//        super.onDestroyView();
//    }
}
