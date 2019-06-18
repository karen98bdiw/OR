package com.example.or;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.or.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView gifLook;
    private Gson gson;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String gifURLInRespons = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        gifLook = findViewById(R.id.gifLook);
        gson = new Gson();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new WorkTask().execute("https://yesno.wtf/api");
            }
        });


    }

    private class WorkTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {

            String loadedURL = null;

            String query = strings[0];

            HttpURLConnection connection = null;

            try {

                connection = (HttpURLConnection) new URL(query).openConnection();

                connection.setConnectTimeout(800);
                connection.setReadTimeout(800);

                connection.setRequestMethod("GET");


                connection.connect();
                ;

                StringBuilder stringBuilder = new StringBuilder();

                if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append('\n');
                    }

                    Gson gson = new Gson();

                    YesOrNo yesOrNo = gson.fromJson(stringBuilder.toString(), YesOrNo.class);

                    loadedURL = yesOrNo.getImage();


                } else {
                    Toast.makeText(MainActivity.this, "conection error" + connection.getResponseCode() + connection.getResponseMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Throwable couse) {
                return loadedURL;
//                this.cancel(true);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return loadedURL;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null){
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "No conection (((", Toast.LENGTH_SHORT).show();
            }else {
                Glide
                        .with(MainActivity.this)
                        .load(s)
                        .into(gifLook);

                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}