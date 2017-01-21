package com.example.q.shareplaylist;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONObject;

import java.util.ArrayList;


public class PlayGroup extends Fragment {
    View rootView;
    ViewPager viewPager;
    PlayGroupPager pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_play_group, container, false);

        viewPager = (ViewPager)rootView.findViewById(R.id.playgroup_pager);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new PlayGroupPager(getActivity().getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        return rootView;
    }
}

class PlayGroupPager extends FragmentPagerAdapter {

    public PlayGroupPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PlayGroupPlayer();
            case 1:
                return new PlayGroupHistory();
            case 2:
                return new PlayGroupAddVideo();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
