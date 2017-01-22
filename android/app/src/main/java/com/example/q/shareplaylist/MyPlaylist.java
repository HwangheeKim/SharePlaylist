package com.example.q.shareplaylist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos=position;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setMessage("delete?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url=adapter.getItem(pos).getUrl();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("url", url);
                        Ion.with(getContext()).load(MainActivity.serverURL+"/user/myplaylist/delete/"+MainActivity.userID).setJsonObjectBody(jsonObject)
                                .asJsonObject().setCallback(new FutureCallback<JsonObject>(){

                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                adapter.clear();
                                loadFromServer();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.create().show();
            }
        });

        loadFromServer();

        fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getContext(), loaderActivity.class);
                getActivity().startActivityForResult(searchIntent, MainActivity.SELECT_UPLOAD);
            }
        });

        return rootView;
    }

    private void loadFromServer(){
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
    }




}
