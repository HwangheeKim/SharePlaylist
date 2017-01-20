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
    static String serverURL = "http://52.78.101.202:8080";
    static String userID = "";
    static String userName = "";

    private DrawerLayout drawer;
    private FindGroup findGroup;
    private MyPlaylist myPlaylist;
    private MyProfile myProfile;
    private PlayGroup playGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        findGroup = new FindGroup();
        myPlaylist = new MyPlaylist();
        myProfile = new MyProfile();
        playGroup = new PlayGroup();

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
                }
                break;
            case R.id.drawer_findgroup:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, findGroup).commit();
                break;
            case R.id.drawer_playgroup:
                // TODO : Set argument
                Bundle args = new Bundle();
                args.putString("URL", "SOMETHING");
                playGroup.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, playGroup).commit();
                break;
            case R.id.drawer_myplaylist:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, myPlaylist).commit();
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_LOGIN) {
            updateMyProfile(true);
        }
    }

    private boolean isLoggedIn() {
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
                        ImageView drawerAvatar = (ImageView)findViewById(R.id.drawer_avatar);
                        String imgurl = "https://graph.facebook.com/" + userID + "/picture?height=500";
                        Picasso.with(getApplicationContext()).load(imgurl).into(drawerAvatar);

                        TextView drawerUserName = (TextView)findViewById(R.id.drawer_username);
                        drawerUserName.setText(userName);

                        findViewById(R.id.drawer_avatar_frame).setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void tt(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}
