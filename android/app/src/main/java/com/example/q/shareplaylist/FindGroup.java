package com.example.q.shareplaylist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class FindGroup extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_group, container, false);

//        Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.findgroup_toolbar);
//        ((AppBarLayout)rootView.findViewById(R.id.findgroup_appbar)).setExpanded(true);

        GridView gridView = (GridView)rootView.findViewById(R.id.findgroup_gridview);
        ViewCompat.setNestedScrollingEnabled(gridView, true);

        GroupAdapter groupAdapter = new GroupAdapter();
        groupAdapter.add("this is test");
        groupAdapter.add("is this work?");
        groupAdapter.add("how about this?");
        groupAdapter.add("this is test");
        groupAdapter.add("is this work?");
        groupAdapter.add("how about this?");
        groupAdapter.add("this is test");
        groupAdapter.add("is this work?");
        groupAdapter.add("how about this?");

        gridView.setAdapter(groupAdapter);

        return rootView;
    }
}
