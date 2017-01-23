package com.example.q.shareplaylist;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;


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
    private String testvideo="ePpPVE-GGJw";

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

        initYouTube();
        playNextLineup();

        return rootView;
    }

    private void initYouTube(){
        youTubePlayerFragment = (YouTubePlayerFragment) getActivity().getFragmentManager().findFragmentById(R.id.playgroup_youtube);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                mYouTubePlayer = youTubePlayer;
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

    // TODO : ONGOING, implement load video function
    private void loadYouTube(/* URL? , from when?*/) {
        mYouTubePlayer.loadVideo("", 0);
    }

    private void playNextLineup() {
        // TODO : ONGOING, Server nextLineup query
        // TODO : If the response is not empty, call loadYouTube()
        // TODO : else, stop the video.
    }

    // TODO : ONGOING, Implement this
    public boolean isVideoLoaded() {
        return false;
    }

    // TODO : ONGOING, set (timerCallback / youtubeVideoEndCallback), get nextLineup
    // TODO : load video from server
    // public void ?? () {}

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
