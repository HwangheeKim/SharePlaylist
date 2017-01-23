package com.example.q.shareplaylist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    BroadcastReceiver broadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_play_group_lineup, container, false);

        listView = (ListView)rootView.findViewById(R.id.playgroup_lineup_list);
        adapter = new VideoAdapter();
        listView.setAdapter(adapter);

        loadFromServer();
        registerBroadcastReceiver();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setMessage("What?")
                        .setPositiveButton("Add to my playlist", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JsonObject jsonObject= new JsonObject();
                                VideoData videoData= adapter.getItem(position);
                                try {
                                    jsonObject.addProperty("url", videoData.getUrl());
                                    jsonObject.addProperty("title", URLEncoder.encode(videoData.getTitle(), "utf-8"));
                                    jsonObject.addProperty("uploader", URLEncoder.encode(videoData.getUploader(), "utf-8"));
                                    jsonObject.addProperty("thumbnail", videoData.getThumbnail());
                                } catch (Exception e) { e.printStackTrace(); }

                                Ion.with(getContext()).load(MainActivity.serverURL+"/user/myplaylist/" + MainActivity.userID).setJsonObjectBody(jsonObject)
                                        .asJsonArray().setCallback(new FutureCallback<JsonArray>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonArray result) {
                                        loadFromServer();
                                        ((MainActivity)getActivity()).notifyMyplaylistAdded();
                                    }
                                });

                            }
                        });

                if (adapter.getItem(position).getPlayerID().equals(MainActivity.userID) &&
                        adapter.getItem(position).getStartedAt() >= 10000000000000L &&
                        !adapter.getCurrentPlayingVideo_id().equals(adapter.getItem(position).get_id())) {
                    builder.setNegativeButton("Remove from lineup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeLineup(position);
                        }
                    });
                }

                builder.create().show();
            }
        });

        return rootView;
    }

    private void removeLineup(final int position) {
        JsonObject json = new JsonObject();
        json.addProperty("_id", adapter.getItem(position).get_id());
        json.addProperty("url", adapter.getItem(position).getUrl());
        json.addProperty("playerID", adapter.getItem(position).getPlayerID());

        Ion.with(getContext())
                .load(MainActivity.serverURL+"/group/"+MainActivity.currentGroup+"/removeLineup")
                .setJsonObjectBody(json)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                loadFromServer();
            }
        });
    }

    private void loadFromServer() {
        adapter.clear();
        Ion.with(getContext()).load(MainActivity.serverURL+"/group/" + MainActivity.currentGroup)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("Lineup Request", result.get("videoLineup").getAsJsonArray().toString());
                JsonArray lineup = result.get("videoLineup").getAsJsonArray();
                for (int i=lineup.size()-1 ; i>=0 ; i--) {
                    JsonObject record = lineup.get(i).getAsJsonObject();
                    String titleDecoded = "";
                    String uploaderDecoded = "";
                    String playerNameDecoded = "";
                    try {
                        titleDecoded = URLDecoder.decode(record.get("title").getAsString(), "utf-8");
                        uploaderDecoded = URLDecoder.decode(record.get("uploader").getAsString(), "utf-8");
                        playerNameDecoded = URLDecoder.decode(record.get("playerName").getAsString(), "utf-8");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    adapter.add(record.get("_id").getAsString(),
                            record.get("url").getAsString(), titleDecoded, uploaderDecoded,
                            record.get("thumbnail").getAsString(), record.get("playerID").getAsString(),
                            playerNameDecoded, record.get("startedAt").getAsLong(),
                            record.get("duration").getAsLong(), record.get("like").getAsInt());
                }
            }
        });
    }

    public void addToLineup(final VideoData videoData) {
        String url = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id="+ videoData.getUrl() + "&key=" + MainActivity.youtubeKey;
        Ion.with(getContext()).load(url).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                JsonObject json = new JsonObject();

                try {
                    json.addProperty("url", videoData.getUrl());
                    json.addProperty("title", URLEncoder.encode(videoData.getTitle(), "utf-8"));
                    json.addProperty("uploader", URLEncoder.encode(videoData.getUploader(), "utf-8"));
                    json.addProperty("thumbnail", videoData.getThumbnail());
                    json.addProperty("playerID", MainActivity.userID);
                    json.addProperty("playerName", URLEncoder.encode(MainActivity.userName, "utf-8"));
                    json.addProperty("duration", result.get("items").getAsJsonArray().get(0).getAsJsonObject()
                                                    .get("contentDetails").getAsJsonObject()
                                                    .get("duration").getAsString());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                Ion.with(getContext()).load(MainActivity.serverURL+"/group/" + MainActivity.currentGroup + "/addLineup")
                        .setJsonObjectBody(json).asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
//                                loadFromServer();
                                if(!isVideoLoaded())
                                    ((MainActivity)getActivity()).playNextLineup();
                            }
                        });
            }
        });
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.q.shareplaylist.LINEUP_BROADCAST");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Get FCM", "GOT FCM FROM THE SERVER, refresh!");
                loadFromServer();
                if(!isVideoLoaded())
                    ((MainActivity)getActivity()).playNextLineup();
            }
        };

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroyView();
    }

    private boolean isVideoLoaded() {
        return ((MainActivity)getActivity()).isVideoLoaded();
    }
}
