package com.example.anglermusic;

import java.util.ArrayList;
import java.util.List;

public class MusicDataSingleton {
    private static MusicDataSingleton instance;
    private List<MusicModel> musicList;

    private MusicDataSingleton() {
        // Private constructor to prevent instantiation
        musicList = new ArrayList<>();
    }

    public static synchronized MusicDataSingleton getInstance() {
        if (instance == null) {
            instance = new MusicDataSingleton();
        }
        return instance;
    }

    public List<MusicModel> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicModel> musicList) {
        this.musicList = musicList;
    }
}
