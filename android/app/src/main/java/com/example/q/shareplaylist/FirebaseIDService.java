package com.example.q.shareplaylist;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by q on 2017-01-24.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FID", "Refreshed Token : " + refreshedToken);

        JsonObject json = new JsonObject();
        json.addProperty("userID", MainActivity.userID);
        json.addProperty("userToken", refreshedToken);
        Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/user/enroll")
                .setJsonObjectBody(json).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("enrollMe onCompleted", result.toString());
            }
        });

        super.onTokenRefresh();
    }
}
