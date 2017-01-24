package com.example.q.shareplaylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.q.shareplaylist.PlayGroupAddVideo;
import com.example.q.shareplaylist.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by q on 2017-01-22.
 */

public class loaderActivity {

    private View view;
    private EditText editText;
    private ListView listView;
    private VideoAdapter adapter;
    private Context context;
    private Activity activity;
    private String youtubeKey = "AIzaSyBOmiAJ9FD_IWza61CHPJCzZb8lj3gggrA";


    public loaderActivity(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_play_group_add_video, null);

        editText = (EditText)view.findViewById(R.id.playgroup_add_search);

        listView = (ListView)view.findViewById(R.id.playgroup_add_list);
        adapter = new VideoAdapter();
        listView.setAdapter(adapter);



        // Request Search query
        view.findViewById(R.id.playgroup_add_button).setOnClickListener(new View.OnClickListener() {
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
                addToPlayList(position);
            }
        });
    }

    public View getView(){
        return view;
    }


    public void addToPlayList(int position){
        JsonObject jsonObject= new JsonObject();
        VideoData videoData= adapter.getItem(position);
        final String[] video_String= {videoData.getUrl(), videoData.getTitle(), videoData.getUploader(), videoData.getThumbnail()};
        try{
        jsonObject.addProperty("url", video_String[0]);
        jsonObject.addProperty("title", URLEncoder.encode(video_String[1], "utf-8"));
        jsonObject.addProperty("uploader", URLEncoder.encode(video_String[2], "utf-8"));
        jsonObject.addProperty("thumbnail", video_String[3]);}catch(Exception e){e.printStackTrace();}
        Ion.with(context).load(MainActivity.serverURL+"/user/myplaylist/" + MainActivity.userID).setJsonObjectBody(jsonObject)
                .asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                ((MainActivity)activity).selectUpload(video_String, view);
                //Log.d("Post to playlist", result.toString());
            }
        });

    }






    void youtubeSearch() {
        String keyword="";
        try {
            keyword = URLEncoder.encode(editText.getText().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=viewCount&q="+keyword+"&type=video&videoDefinition=any&maxResults=20&key=" + youtubeKey;

        Ion.with(context).load(url).asJsonObject()
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
