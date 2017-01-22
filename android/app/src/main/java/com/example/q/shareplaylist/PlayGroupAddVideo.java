package com.example.q.shareplaylist;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class PlayGroupAddVideo extends Fragment {
    View rootView;
    EditText editText;
    ListView listView;
    VideoAdapter adapter;
    String youtubeKey = "AIzaSyBOmiAJ9FD_IWza61CHPJCzZb8lj3gggrA";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play_group_add_video, container, false);
        editText = (EditText)rootView.findViewById(R.id.playgroup_add_search);

        listView = (ListView)rootView.findViewById(R.id.playgroup_add_list);
        adapter = new VideoAdapter();
        listView.setAdapter(adapter);

        // Load Playlist From Server
        Ion.with(getContext()).load(MainActivity.serverURL+"/user/" + MainActivity.userID)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("Load user's playlist", result.toString());
                JsonArray playlist = result.get("playlist").getAsJsonArray();
                for (int i=0 ; i<playlist.size() ; i++) {
                    JsonObject record = playlist.get(i).getAsJsonObject();
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

        // Request Search query
        rootView.findViewById(R.id.playgroup_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load search result to the list
                if(editText.getText().toString().equals("")) {
                    Snackbar.make(listView, "Enter the search keyword", Snackbar.LENGTH_SHORT).show();
                } else {
                    youtubeSearch();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(listView, "Video (" + adapter.getItem(position).getTitle() + ") will be added", Snackbar.LENGTH_SHORT).show();
                ((PlayGroup)getTargetFragment()).addVideoToLineup(adapter.getItem(position));
            }
        });
        return rootView;
    }

    void youtubeSearch() {
        String keyword="";
        try {
            keyword = URLEncoder.encode(editText.getText().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=viewCount&q="+keyword+"&type=video&videoDefinition=any&maxResults=20&key=" + youtubeKey;

        Ion.with(getContext()).load(url).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        adapter.clear();
                        Log.d("Youtube Search Result", result.toString());
                        JsonArray items = result.get("items").getAsJsonArray();
                        for(int i=0 ; i<items.size() ; i++) {
                            JsonObject item = items.get(i).getAsJsonObject();
                            JsonObject snippet = item.get("snippet").getAsJsonObject();
                            adapter.add(item.get("id").getAsJsonObject().get("videoId").getAsString(),
                                    snippet.get("title").getAsString(),
                                    snippet.get("channelTitle").getAsString(),
                                    snippet.get("thumbnails").getAsJsonObject()
                                        .get("high").getAsJsonObject()
                                        .get("url").getAsString());
                        }
                    }
                });
    }
}
