package com.example.anglermusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MusicListing extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_listing);

        recyclerView = findViewById(R.id.music_rv);
        setupRecyclerView();

        // Retrieve data from the singleton class
        List<MusicModel> musicList = MusicDataSingleton.getInstance().getMusicList();

        // Update the adapter with the new data
        adapter.setMusicList(musicList);
        adapter.notifyDataSetChanged();
    }

    void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(new ArrayList<>()); // Initialize adapter with an empty list
        recyclerView.setAdapter(adapter);
    }
}
