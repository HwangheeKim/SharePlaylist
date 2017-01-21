package com.example.q.shareplaylist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class PlayGroupAddVideo extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play_group_add_video, container, false);

        // Load Playlist From Server
        Ion.with(getContext()).load(MainActivity.serverURL+"/playlist/" + MainActivity.userID)
                .asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                // TODO : Check whether playlist fetched correctly
                // TODO : Convert information into item_video
                Log.d("Load user's playlist", result.toString());
            }
        });

        // TODO : URGENT, From here!
        // Request Search query
        // (in callback method) Load search result to the list

        return rootView;
    }
}
