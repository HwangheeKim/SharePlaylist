package com.example.q.shareplaylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.BitmapDrawableFactory;
import com.koushikdutta.ion.Ion;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;


public class MyProfile extends Fragment {

    View view;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_my_profile, container, false);

        imageView= (ImageView) view.findViewById(R.id.profile_image);
        new LongOperation().execute();


        ((TextView)view.findViewById(R.id.name)).setText(MainActivity.userName);
        ((TextView)view.findViewById(R.id.gender)).setText(MainActivity.userGender);
        ((TextView)view.findViewById(R.id.birthday)).setText(MainActivity.userBirthday);
        ((TextView)view.findViewById(R.id.age)).setText(MainActivity.userAge_range);





        return view;
    }

    public class LongOperation extends AsyncTask<String, Void, String> {
        Bitmap avatar;

        @Override
        protected String doInBackground(String... params) {
            String result=null;
            try{
                URL url = new URL("https://graph.facebook.com/" + MainActivity.userID + "/picture?height=200");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                avatar = BitmapFactory.decodeStream(input);
                result= "OK";
            }catch(Exception e){e.printStackTrace();}



            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar);
                roundDrawable.setCircular(true);
                imageView.setImageDrawable(roundDrawable);
            }
            else{
                Toast.makeText(getContext(), "Profile Loading failed", Toast.LENGTH_SHORT).show();
            }


            super.onPostExecute(s);
        }
    }
}
