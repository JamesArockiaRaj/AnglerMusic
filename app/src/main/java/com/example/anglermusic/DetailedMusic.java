package com.example.anglermusic;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedMusic extends AppCompatActivity {

    TextView titleTv, Artistname, description, currentTime, totalTime, collectionName, dateTv, lyrics;
    ImageView musicimg, backbtn;
    String audioUrl, finalTime, startTime;
    MediaPlayer mediaPlayer;
    ImageButton buttonPlay, buttonPrevious, buttonNext;
    SeekBar seekbar;
    private int lastPlaybackPosition = 0;
    private List<MusicModel> musicList;
    private int currentPosition; // Track the current position in the musicList

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_music);
        InitViews();
    }

    private void InitViews() {
        audioUrl = getIntent().getStringExtra("previewUrl");
        musicList = MusicDataSingleton.getInstance().getMusicList();

        currentPosition = getIntent().getIntExtra("position",0);

        titleTv = findViewById(R.id.titleTv);
        Artistname = findViewById(R.id.Artistname);
        description = findViewById(R.id.desc);
        musicimg = findViewById(R.id.imgAlbum);
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        seekbar = findViewById(R.id.playerSeekBar);
        backbtn = findViewById(R.id.backbtn);
        collectionName = findViewById(R.id.collectionName);
        dateTv = findViewById(R.id.dateTv);
        lyrics = findViewById(R.id.lyrics);

        lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailedMusic.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition > 0) {
                    currentPosition--;
                    loadMusicData(currentPosition);
                } else {
                    Toast.makeText(DetailedMusic.this, "No Previous Music", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition < musicList.size() - 1) {
                    currentPosition++;
                    loadMusicData(currentPosition);
                } else {
                    Toast.makeText(DetailedMusic.this, "No more Music", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekbar.setClickable(true);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(milliSecondsToTimer(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                finalTime = String.valueOf(mediaPlayer.getDuration());
                startTime = String.valueOf(mediaPlayer.getCurrentPosition());
                updateTimings();
                seekbar.setMax(mediaPlayer.getDuration());
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initializeMediaPlayer();

        titleTv.setText(getIntent().getStringExtra("trackName"));
        Artistname.setText(getIntent().getStringExtra("ArtistName"));
        description.setText(getIntent().getStringExtra("shortDescription"));
        collectionName.setText(getIntent().getStringExtra("CollectionName"));
        loadReleaseDate(getIntent().getStringExtra("ReleaseDate"));

        Picasso.get().load(getIntent().getStringExtra("artworkUrl100"))
                .error(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .into(musicimg);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        loadMusicData(currentPosition);
        mHandler.postDelayed(updateSeekBarTime, 1000);
    }

    private void loadReleaseDate(String releaseDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(releaseDate);
            String formattedDate = outputFormat.format(date);
            dateTv.setText("Release Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            dateTv.setText("Release Date: " + releaseDate);
        }
    }

    private void loadMusicData(int position) {
        if (position >= 0 && position < musicList.size()) {
            currentPosition = position; // Update the currentPosition
            MusicModel music = musicList.get(currentPosition);
            updateUI(music);

            audioUrl = music.getPreviewUrl();
            stopAudio();
            initializeMediaPlayer();

            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepareAsync();
                playAudio();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DetailedMusic.this, "Error setting data source", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(MusicModel music) {

        seekbar.setProgress(0);
        titleTv.setText(music.getTrackName());
        Artistname.setText(music.getArtistName());
        description.setText(music.getShortDescription());
        collectionName.setText(music.getCollectionName());
        dateTv.setText("Release Date: " + formatReleaseDate(music.getReleaseDate()));
        Picasso.get().load(music.getArtworkUrl100())
                .error(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .into(musicimg);
    }

    private String formatReleaseDate(String releaseDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(releaseDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return releaseDate;
        }
    }

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        currentTime.setText(milliSecondsToTimer(currentPosition));
                    }
                });
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    private String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer string
        return finalTimerString;
    }

    private void updateTimings() {
        currentTime.setText(milliSecondsToTimer(Long.parseLong(startTime)));
        totalTime.setText(milliSecondsToTimer(Long.parseLong(finalTime)));
    }

    private void initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    finalTime = String.valueOf(mediaPlayer.getDuration());
                    startTime = String.valueOf(mediaPlayer.getCurrentPosition());
                    updateTimings();
                    seekbar.setMax(mediaPlayer.getDuration());
                }
            });
        }
    }

    private void playAudio() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }

            if (!mediaPlayer.isPlaying()) {
                buttonPlay.setImageResource(R.drawable.ic_pause);
                // If not playing, start playback or resume
                if (lastPlaybackPosition == 0) {
                    // Start playback from the beginning
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } else {
                    // Resume playback from the last position
                    mediaPlayer.seekTo(lastPlaybackPosition);
                    mediaPlayer.start();
                }

                // Set up completion listener to handle the end of playback
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        buttonPlay.setImageResource(R.drawable.ic_play);
                        mediaPlayer.stop();
                        seekbar.setProgress(0);
                        currentTime.setText("00:00");
                        lastPlaybackPosition = 0;
                    }
                });
            } else {
                buttonPlay.setImageResource(R.drawable.ic_play);
                // If playing, pause
                mediaPlayer.pause();
                lastPlaybackPosition = mediaPlayer.getCurrentPosition(); // Save the current position
            }
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(DetailedMusic.this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopAudio();
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}



