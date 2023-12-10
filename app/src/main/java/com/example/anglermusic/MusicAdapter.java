package com.example.anglermusic;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<MusicModel> musicList;

    MusicAdapter(List<MusicModel> musicList) {
        this.musicList = new ArrayList<>(musicList);
    }

    public void setMusicList(List<MusicModel> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_rv, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicModel music = musicList.get(position);
        holder.titleTv.setText(music.getTrackName());
        holder.descTv.setText(music.getShortDescription());

        if (music.getArtworkUrl30() != null && !music.getArtworkUrl30().isEmpty()) {
            Picasso.get().load(music.getArtworkUrl30())
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener((view -> {
                Intent intent = new Intent(view.getContext(), DetailedMusic.class);
                intent.putExtra("previewUrl", music.getPreviewUrl());
                intent.putExtra("trackName", music.getTrackName());
                intent.putExtra("shortDescription", music.getShortDescription());
                intent.putExtra("artworkUrl30", music.getArtworkUrl30());
                intent.putExtra("artworkUrl60", music.getArtworkUrl60());
                intent.putExtra("artworkUrl100", music.getArtworkUrl100());
                intent.putExtra("ArtistName", music.getArtistName());
                intent.putExtra("CollectionName", music.getCollectionName());
                intent.putExtra("ReleaseDate", music.getReleaseDate());
                intent.putExtra("TrackId", music.getTrackId());
                intent.putExtra("position", position);


                view.getContext().startActivity(intent);
            }));
        } else {
            holder.imageView.setImageResource(R.drawable.no_img);
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv, descTv;
        ImageView imageView;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.music_title);
            descTv = itemView.findViewById(R.id.music_desc);
            imageView = itemView.findViewById(R.id.music_img);
        }
    }
}
