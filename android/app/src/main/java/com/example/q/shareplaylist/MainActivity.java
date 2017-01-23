package com.example.q.shareplaylist;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener{

    static int REQUEST_LOGIN = 0x1001;
    static int ADD_GROUP = 0x1002;
    static int GROUP_SELECTED = 0x1003;
    static int PLAY_GROUP = 0x1004;
    static int SELECT_UPLOAD= 0x1005;
    static String serverURL = "http://52.78.101.202:8080";
    static String userID = "";
    static String userName = "";
    static String currentGroup = "";
    static String youtubeKey = "AIzaSyBOmiAJ9FD_IWza61CHPJCzZb8lj3gggrA";

    private DrawerLayout drawer;
    private FindGroup findGroup;
    private MyPlaylist myPlaylist;
    private MyProfile myProfile;
    private PlayGroup playGroup;
    private int menuNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        findGroup = new FindGroup();
        myPlaylist = new MyPlaylist();
        myProfile = new MyProfile();

        // Set startup fragment, need to be modified!
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, findGroup).commit();

        // Set Onclicklistener to each drawer item
        findViewById(R.id.drawer_me).setOnClickListener(this);
        findViewById(R.id.drawer_findgroup).setOnClickListener(this);
        findViewById(R.id.drawer_playgroup).setOnClickListener(this);
        findViewById(R.id.drawer_myplaylist).setOnClickListener(this);
        findViewById(R.id.drawer_login).setOnClickListener(this);

        // Initialize FacebookSDK and check the login status
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(isLoggedIn()) {
                    updateMyProfile(false);
                } else {
                    findViewById(R.id.drawer_avatar_frame).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.drawer_me:
                if(userID.equals("")) {
                    Snackbar.make(findViewById(R.id.main_container), "You have to be logged in!", Snackbar.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, myProfile).commit();
                    menuNumber = 0;
                }
                break;
            case R.id.drawer_findgroup:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, findGroup).commit();
                menuNumber = 1;
                break;
            case R.id.drawer_playgroup:
                if(currentGroup.equals("")) {
                    Snackbar.make(findViewById(R.id.main_container), "You have to join the group!", Snackbar.LENGTH_SHORT).show();
                } else if (menuNumber != 2){
                    playGroup = new PlayGroup();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, playGroup).commit();
                    menuNumber = 2;
                }
                break;
            case R.id.drawer_myplaylist:
                if(userID.equals("")) {
                    Snackbar.make(findViewById(R.id.main_container), "You have to be logged in!", Snackbar.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, myPlaylist).commit();
                    menuNumber = 3;
                }
                break;
            case R.id.drawer_login:
                if(isLoggedIn()) {
                    Snackbar.make(findViewById(R.id.main_container), "Already Logged In!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, Login.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
                break;
        }

        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_LOGIN) {
            updateMyProfile(true);
        } else if (requestCode == GROUP_SELECTED) {
            playGroup = new PlayGroup();
            menuNumber=2;
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, playGroup).commit();
        } else if(requestCode == SELECT_UPLOAD){
            String[] str=data.getStringArrayExtra("video");
            try{
                myPlaylist.adapter.add(str[0], URLDecoder.decode(str[1],"utf-8"), URLDecoder.decode(str[2], "utf-8"), str[3]);
                myPlaylist.adapter.notifyDataSetChanged();
            }catch(Exception e){e.printStackTrace();}

            Snackbar.make(myPlaylist.listView, "Video (" + str[1] + ") will be added", Snackbar.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    static boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public void updateMyProfile(final boolean isFirsttime) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Request Result", response.toString());

                        try {
                            userID = response.getJSONObject().getString("id");
                            userName = URLDecoder.decode(response.getJSONObject().getString("name"), "utf-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(isFirsttime) {
                            Snackbar.make(findViewById(R.id.main_container), "Welcome, " + userName + "!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.main_container), "Welcome Back, " + userName + "!", Snackbar.LENGTH_SHORT).show();
                        }

                        enrollMe();
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void enrollMe() {
        JsonObject json = new JsonObject();

        try {
            json.addProperty("userID", userID);
            json.addProperty("userName", URLEncoder.encode(userName, "utf-8"));
            json.addProperty("picture", "https://graph.facebook.com/" + userID + "/picture?height=500");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ion.with(getApplicationContext()).load(serverURL+"/user/enroll")
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("enrollMe onCompleted", result.toString());

                        if(result.has("current")) {
                            currentGroup = result.get("current").getAsString();
                        }

                        ImageView drawerAvatar = (ImageView)findViewById(R.id.drawer_avatar);
                        String imgurl = "https://graph.facebook.com/" + userID + "/picture?height=500";
                        Picasso.with(getApplicationContext()).load(imgurl).into(drawerAvatar);

                        TextView drawerUserName = (TextView)findViewById(R.id.drawer_username);
                        drawerUserName.setText(userName);

                        findViewById(R.id.drawer_avatar_frame).setVisibility(View.VISIBLE);
                    }
                });
    }

    public void notifyMyplaylistAdded() {
        playGroup.addVideo.loadPlaylist();
    }

    public void addVideoToLineup(VideoData videoData) {
        playGroup.lineup.addToLineup(videoData);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean isVideoLoaded() {
        return playGroup.isVideoLoaded();
    }

    private void tt(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}
