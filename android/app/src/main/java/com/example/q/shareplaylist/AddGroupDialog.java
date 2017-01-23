package com.example.q.shareplaylist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLEncoder;

/**
 * Created by q on 2017-01-21.
 */

public class AddGroupDialog extends DialogFragment {
    View rootView;
    EditText groupName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_addgroup, container, false);
        groupName = (EditText)rootView.findViewById(R.id.addgroup_groupname);

        rootView.findViewById(R.id.addgroup_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupName.getText().toString().equals("")) {
                    return;
                }

                JsonObject json = new JsonObject();
                try {
                    json.addProperty("groupName", URLEncoder.encode(groupName.getText().toString(), "utf-8"));
                    json.addProperty("creatorID", MainActivity.userID);
                    json.addProperty("creatorName", URLEncoder.encode(MainActivity.userName, "utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Ion.with(getContext()).load(MainActivity.serverURL+"/group/new")
                        .setJsonObjectBody(json)
                        .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("AddGroupDialog Dialog", result.toString());
                        ((FindGroup)getTargetFragment()).loadFromServer();
                        dismiss();
                    }
                });
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
