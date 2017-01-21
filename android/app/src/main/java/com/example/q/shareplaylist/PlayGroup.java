package com.example.q.shareplaylist;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private String testvideo="ePpPVE-GGJw";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_play_group, container, false);

        viewPager = (ViewPager)rootView.findViewById(R.id.playgroup_pager);
        pagerAdapter = new PlayGroupPager(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        initYouTube();

        return rootView;
    }

    private void initYouTube(){
        youTubePlayerFragment = (YouTubePlayerFragment) getActivity().getFragmentManager().findFragmentById(R.id.playgroup_youtube);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(testvideo, 0);
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
}

class PlayGroupPager extends FragmentStatePagerAdapter {
    PlayGroupPlayer player;
    PlayGroupHistory history;
    PlayGroupAddVideo addVideo;

    public PlayGroupPager(FragmentManager fm) {
        super(fm);
        player = new PlayGroupPlayer();
        history = new PlayGroupHistory();
        addVideo = new PlayGroupAddVideo();
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
