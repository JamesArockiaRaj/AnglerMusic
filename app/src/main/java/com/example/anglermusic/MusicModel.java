package com.example.anglermusic;
public class MusicModel {
    private String trackName;
    private String shortDescription;
    private String artworkUrl30;
    private String artworkUrl60;
    private String artworkUrl100;
    private String previewUrl;
    private String artistName;
    private String collectionName;
    private String releaseDate;
    private String trackId;




    public MusicModel(String trackName, String shortDescription, String artworkUrl30, String artworkUrl60, String artworkUrl100, String previewUrl, String artistName, String collectionName, String releaseDate, String trackId ) {
        this.trackName = trackName;
        this.shortDescription = shortDescription;
        this.artworkUrl30 = artworkUrl30;
        this.artworkUrl60 = artworkUrl60;
        this.artworkUrl100 = artworkUrl100;
        this.previewUrl = previewUrl;
        this.artistName = artistName;
        this.collectionName = collectionName;
        this.releaseDate = releaseDate;
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getArtworkUrl30() {
        return artworkUrl30;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCollectionName() {
        return collectionName;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getTrackId() {
        return trackId;
    }
}
