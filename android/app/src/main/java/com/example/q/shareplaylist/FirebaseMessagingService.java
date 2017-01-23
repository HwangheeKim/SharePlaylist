package com.example.q.shareplaylist;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by q on 2017-01-24.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, remoteMessage.getNotification().getBody());

        if(remoteMessage.getNotification().getTitle().equals("Lineup Changed")) {
            Intent sendIntent = new Intent("com.example.q.shareplaylist.LINEUP_BROADCAST");
            sendBroadcast(sendIntent);
        }

        super.onMessageReceived(remoteMessage);
    }
}
