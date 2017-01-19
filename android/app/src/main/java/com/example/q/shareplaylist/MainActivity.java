package com.example.q.shareplaylist;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener{

    static String serverURL = "http://52:78:101.202:3000";

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

        findViewById(R.id.drawer_me).setOnClickListener(this);
        findViewById(R.id.drawer_findgroup).setOnClickListener(this);
        findViewById(R.id.drawer_playgroup).setOnClickListener(this);
        findViewById(R.id.drawer_myplaylist).setOnClickListener(this);
        findViewById(R.id.drawer_login).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.drawer_me:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, myProfile).commit();
                break;
            case R.id.drawer_findgroup:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, findGroup).commit();
                break;
            case R.id.drawer_playgroup:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, playGroup).commit();
                break;
            case R.id.drawer_myplaylist:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, myPlaylist).commit();
                break;
        }

        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}
