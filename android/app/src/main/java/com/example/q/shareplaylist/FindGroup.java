package com.example.q.shareplaylist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
                if(!MainActivity.isLoggedIn()) {
                    Snackbar.make(rootView.findViewById(R.id.findgroup_coordinator), "You have to be logged in!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                JsonObject json = new JsonObject();
                json.addProperty("userID", MainActivity.userID);
                json.addProperty("current", groupAdapter.getItem(position).getGroupID());

                Ion.with(getContext()).load(MainActivity.serverURL+"/user/enroll")
                        .setJsonObjectBody(json).asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                MainActivity.currentGroup = groupAdapter.getItem(position).getGroupID();
                                MainActivity.currentGroupName = groupAdapter.getItem(position).getGroupName();
                                Intent intent = new Intent();
                                ((MainActivity)getActivity()).onActivityResult(MainActivity.GROUP_SELECTED, Activity.RESULT_OK, intent);
                            }
                        });
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(!MainActivity.isLoggedIn()) {
                    Snackbar.make(rootView.findViewById(R.id.findgroup_coordinator), "You have to be logged in!", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                if (MainActivity.userID.equals(groupAdapter.getItem(position).getCreatorID())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage("Delete " + groupAdapter.getItem(position).getGroupName() + "?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Ion.with(getContext()).load(MainActivity.serverURL+"/group/remove/"
                                            + groupAdapter.getItem(position).getGroupID())
                                            .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            loadFromServer();
                                        }
                                    });
                                }
                            });

                    builder.create().show();
                }
                return true;
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

        loadFromServer();

        return rootView;
    }

    /***
     *  Load group data from the server and reset the adapter
     */
    public void loadFromServer() {
        groupAdapter.clear();
        groupAdapter.notifyDataSetChanged();
        Ion.with(getContext()).load(MainActivity.serverURL + "/group/all")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for (int i=0 ; i<result.size() ; i++) {
                            JsonObject record = result.get(i).getAsJsonObject();
                            final String groupID = record.get("_id").getAsString();
                            String groupNameDecoded = "";
                            final String creatorID = record.get("creatorID").getAsString();
                            String creatorDecoded = "";
                            String thumbnail = "";
                            int status = 0;
                            try {
                                groupNameDecoded = URLDecoder.decode(record.get("groupName").getAsString(), "utf-8");
                                creatorDecoded = URLDecoder.decode(record.get("creatorName").getAsString(), "utf-8");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            final String finalGroupNameDecoded = groupNameDecoded;
                            final String finalCreatorDecoded = creatorDecoded;

                            Ion.with(getContext()).load(MainActivity.serverURL + "/group/info/" + groupID)
                                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if(result.has("thumbnail")) {
                                        groupAdapter.add(groupID, finalGroupNameDecoded, creatorID, finalCreatorDecoded,
                                                result.get("thumbnail").getAsString(), result.get("count").getAsInt());
                                    } else {
                                        groupAdapter.add(groupID, finalGroupNameDecoded, creatorID, finalCreatorDecoded,
                                                "", result.get("count").getAsInt());
                                    }
                                }
                            });
                        }
                    }
                });
    }
}
