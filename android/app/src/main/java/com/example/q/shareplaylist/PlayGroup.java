package com.example.q.shareplaylist;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class PlayGroup extends Fragment {
    private View rootView;
    private ViewPager viewPager;
    private PlayGroupPager pagerAdapter;
    PlayGroupPlayer player;
    PlayGroupLineup lineup;
    PlayGroupAddVideo addVideo;

    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private boolean isLoaded = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_play_group, container, false);
        viewPager = (ViewPager)rootView.findViewById(R.id.playgroup_pager);

        player = new PlayGroupPlayer();
        lineup = new PlayGroupLineup();
        addVideo = new PlayGroupAddVideo();

        // These lines were problem but don't know why....
//        player.setTargetFragment(this, MainActivity.PLAY_GROUP);
//        lineup.setTargetFragment(this, MainActivity.PLAY_GROUP);
//        addVideo.setTargetFragment(this, MainActivity.PLAY_GROUP);
        pagerAdapter = new PlayGroupPager(getActivity().getSupportFragmentManager(),
                                          player, lineup, addVideo);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        initYouTube();

        return rootView;
    }

    private void initYouTube(){
        youTubePlayerFragment = (YouTubePlayerFragment) getActivity().getFragmentManager().findFragmentById(R.id.playgroup_youtube);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                mYouTubePlayer = youTubePlayer;
                setUpYouTubeListener();
                playNextLineup();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        youTubePlayerFragment.initialize("AIzaSyDDN48pBGknlr4oU8_-HEY1d2gMerq5mxw", onInitializedListener);
}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(getActivity().isDestroyed()==true) {
                return;
            }
        }
        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(youTubePlayerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadYouTube(String url_id , int millis) {
        if(millis < 0) millis = 0;
        mYouTubePlayer.loadVideo(url_id, millis);
    }

    public void playNextLineup() {
        Ion.with(getContext()).load(MainActivity.serverURL+"/group/"+MainActivity.currentGroup+"/nextLineup")
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if(!result.has("_id")) {
                    isLoaded = false;
                    player.setPlayingInfoJson(new JsonObject(), false);
                } else {
                    isLoaded = true;
                    player.setPlayingInfoJson(result, true);
                    loadYouTube(result.get("url").getAsString(),
                            (int)(System.currentTimeMillis() - result.get("startedAt").getAsLong()));
                    lineup.adapter.setCurrentPlayingVideo_id(result.get("_id").getAsString());
                }
            }
        });
    }

    private void setUpYouTubeListener(){
        mYouTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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
                playNextLineup();
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {
            }
        });


    }

    public boolean isVideoLoaded() {
        return isLoaded;
    }
}

class PlayGroupPager extends FragmentStatePagerAdapter {
    PlayGroupPlayer player;
    PlayGroupLineup history;
    PlayGroupAddVideo addVideo;

    public PlayGroupPager(FragmentManager fm) {
        super(fm);
        player = new PlayGroupPlayer();
        history = new PlayGroupLineup();
        addVideo = new PlayGroupAddVideo();
    }

    public PlayGroupPager(FragmentManager fm, PlayGroupPlayer player, PlayGroupLineup history, PlayGroupAddVideo addVideo) {
        super(fm);
        this.player = player;
        this.history = history;
        this.addVideo = addVideo;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return player;
            case 1:
                return history;
            case 2:
                return addVideo;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
