package com.example.q.shareplaylist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;

public class FindGroup extends Fragment {
    private GroupAdapter groupAdapter;
    private EditText editText;
    private GridView gridView;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_group, container, false);

        gridView = (GridView)rootView.findViewById(R.id.findgroup_gridview);
        ViewCompat.setNestedScrollingEnabled(gridView, true);

        groupAdapter = new GroupAdapter();
        gridView.setAdapter(groupAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                JsonObject json = new JsonObject();
                json.addProperty("userID", MainActivity.userID);
                json.addProperty("current", groupAdapter.getItem(position).getGroupID());

                Ion.with(getContext()).load(MainActivity.serverURL+"/user/enroll")
                        .setJsonObjectBody(json).asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                MainActivity.currentGroup = groupAdapter.getItem(position).getGroupID();
                                Intent intent = new Intent();
                                ((MainActivity)getActivity()).onActivityResult(MainActivity.GROUP_SELECTED, Activity.RESULT_OK, intent);
                            }
                        });
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.findgroup_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.findgroup_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isLoggedIn()) {
                    Snackbar.make(rootView.findViewById(R.id.findgroup_coordinator), "You have to be logged in!", Snackbar.LENGTH_SHORT).show();
                } else {
                    FragmentManager fm = getFragmentManager();
                    AddGroupDialog addGroupDialog = new AddGroupDialog();
                    addGroupDialog.setTargetFragment(FindGroup.this, MainActivity.ADD_GROUP);
                    addGroupDialog.show(fm, "ADD GROUP");
                }
            }
        });


        // TODO : Search group

        loadFromServer();

        return rootView;
    }

    /***
     *  Load group data from the server and reset the adapter
     */
    public void loadFromServer() {
        groupAdapter.clear();
        Ion.with(getContext()).load(MainActivity.serverURL + "/group/all")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for (int i=0 ; i<result.size() ; i++) {
                            JsonObject record = result.get(i).getAsJsonObject();
                            String groupNameDecoded = "";
                            String creatorDecoded = "";
                            try {
                                groupNameDecoded = URLDecoder.decode(record.get("groupName").getAsString(), "utf-8");
                                creatorDecoded = URLDecoder.decode(record.get("creatorName").getAsString(), "utf-8");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            // TODO : Thumbnail fetching, Server status
                            groupAdapter.add(record.get("_id").getAsString(), groupNameDecoded,
                                    record.get("creatorID").getAsString(), creatorDecoded, "", 0);
                        }
                    }
                });
    }
}
