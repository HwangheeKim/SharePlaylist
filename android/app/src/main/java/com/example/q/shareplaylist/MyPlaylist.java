package com.example.q.shareplaylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class MyPlaylist extends Fragment {
    static final int RESULT_OK = 1;
    View rootView;
    FloatingActionButton fab;

    ListView listView;
    VideoAdapter adapter;
    String youtubeKey = "AIzaSyBOmiAJ9FD_IWza61CHPJCzZb8lj3gggrA";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_playlist, container, false);

        listView = (ListView) rootView.findViewById(R.id.myPlayListView);
        adapter = new VideoAdapter();
        listView.setAdapter(adapter);

        // Load Playlist From Server
        Ion.with(this).load(MainActivity.serverURL+"/user/" + MainActivity.userID)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                adapter.clear();
                JsonArray items = result.get("playlist").getAsJsonArray();
                for(int i=0 ; i<items.size() ; i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    try{adapter.add(item.get("url").getAsString(),
                            URLDecoder.decode(item.get("title").getAsString(), "utf-8"),
                            URLDecoder.decode(item.get("uploader").getAsString(), "utf-8"),
                            item.get("thumbnail").getAsString());}catch(Exception d){d.printStackTrace();}
                }
                Log.d("Load user's playlist", result.toString());
            }
        });





        fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), loaderActivity.class);
                getActivity().startActivityForResult(searchIntent, MainActivity.SELECT_UPLOAD);
            }
        });

        return rootView;
    }






}
