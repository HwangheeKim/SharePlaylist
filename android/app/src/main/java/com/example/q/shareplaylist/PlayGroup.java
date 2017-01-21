package com.example.q.shareplaylist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
        pagerAdapter = new PlayGroupPager(getFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        return rootView;
    }
}

class PlayGroupPager extends FragmentStatePagerAdapter {
    private PlayGroupPlayer player;
    private PlayGroupAddVideo addVideo;
    private PlayGroupHistory history;

    public PlayGroupPager(FragmentManager fm) {
        super(fm);
        player = new PlayGroupPlayer();
        addVideo = new PlayGroupAddVideo();
        history = new PlayGroupHistory();
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
