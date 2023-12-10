package com.example.anglermusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<MusicModel> musicList = new ArrayList<>();
    private MusicAdapter adapter;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoInternet(MainActivity.this);
            }
        });
    }

    public static boolean isInternetEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void NoInternet(Context context) {
        if (!(isInternetEnabled(context))) {
            Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show();
        } else {
            FetchMusicData(this);
        }
    }

    private List<MusicModel> parseJsonArray(JSONArray jsonArray) throws JSONException {
        List<MusicModel> musicList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonMusic = jsonArray.getJSONObject(i);

            // Check if the key "trackName" exists in the current JSON object
            if (jsonMusic.has("trackName")) {
                // Assuming MusicModel has a constructor that takes relevant fields as parameters
                MusicModel musicModel = new MusicModel(
                        jsonMusic.getString("trackName"),
                        jsonMusic.optString("shortDescription", ""), // Use optString to handle optional fields
                        jsonMusic.optString("artworkUrl30", ""),
                        jsonMusic.optString("artworkUrl60", ""),
                        jsonMusic.optString("artworkUrl100", ""),
                        jsonMusic.optString("previewUrl", ""),
                        jsonMusic.optString("artistName", ""),
                        jsonMusic.optString("collectionName", ""),
                        jsonMusic.optString("releaseDate", ""),
                        jsonMusic.optString("trackId", "")
                );

                musicList.add(musicModel);
            } else {
                // Log or handle the case where "trackName" is missing
                Log.e("JSONParsing", "No value for trackName in the JSON object at index " + i);
            }
        }

        return musicList;
    }

    private void FetchMusicData(Context context) {
        String url = "https://itunes.apple.com/search?term=John";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    List<MusicModel> newMusicList = parseJsonArray(results);

                    // Store the data in the Singleton class
                    MusicDataSingleton.getInstance().setMusicList(newMusicList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicList.addAll(newMusicList);
                            // Store the data in the Singleton class
                            MusicDataSingleton.getInstance().setMusicList(musicList);
                            // Navigate to MusicListing.class with data
                            Intent intent = new Intent(getApplicationContext(), MusicListing.class);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("JSONException", e.getMessage());
                            // Handle JSONException, e.g., log or show an error message
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("VolleyError", error.getMessage());
                        // Handle VolleyError, e.g., log or show an error message
                    }
                });
            }
        });
        // Use Volley.newRequestQueue(context) instead of Volley.newRequestQueue(this)
        Volley.newRequestQueue(context).add(request);
    }

}