package com.example.q.shareplaylist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.net.URLEncoder;

public class PlayGroupLineup extends Fragment {
    View rootView;
    ListView listView;
    VideoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_play_group_lineup, container, false);

        listView = (ListView)rootView.findViewById(R.id.playgroup_lineup_list);
        adapter = new VideoAdapter();
        listView.setAdapter(adapter);

        loadFromServer();

        return rootView;
    }

    private void loadFromServer() {
        adapter.clear();
        Ion.with(getContext()).load(MainActivity.serverURL+"/group/" + MainActivity.currentGroup)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("Lineup Request", result.get("videoLineup").getAsJsonArray().toString());
                JsonArray lineup = result.get("videoLineup").getAsJsonArray();
                for (int i=0 ; i<lineup.size() ; i++) {
                    JsonObject record = lineup.get(i).getAsJsonObject();
                    String titleDecoded = "";
                    String uploaderDecoded = "";
                    try {
                        titleDecoded = URLDecoder.decode(record.get("title").getAsString(), "utf-8");
                        uploaderDecoded = URLDecoder.decode(record.get("uploader").getAsString(), "utf-8");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    adapter.add(record.get("url").getAsString(), titleDecoded, uploaderDecoded,
                            record.get("thumbnail").getAsString());
                }
            }
        });
    }

    public void addToLineup(final VideoData videoData) {
        JsonObject json = new JsonObject();

        try {
            json.addProperty("url", videoData.getUrl());
            json.addProperty("title", URLEncoder.encode(videoData.getTitle(), "utf-8"));
            json.addProperty("uploader", URLEncoder.encode(videoData.getUploader(), "utf-8"));
            json.addProperty("thumbnail", videoData.getThumbnail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ion.with(getContext()).load(MainActivity.serverURL+"/group/" + MainActivity.currentGroup + "/addLineup")
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
//                        adapter.add(videoData);
                        loadFromServer();
                    }
                });

        // TODO : Notify other users (data set changed through Firebase)
    }
}
