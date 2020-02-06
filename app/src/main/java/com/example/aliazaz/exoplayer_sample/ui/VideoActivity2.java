package com.example.aliazaz.exoplayer_sample.ui;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.aliazaz.exoplayer_sample.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class VideoActivity2 extends AppCompatActivity {

    private static final String TAG = VideoActivity2.class.getName();
    TextView textViewName;
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    DataSource.Factory dataSourceFactory;
    Long playbackPosition = 0L;
    int currentWindow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_video_playing);

        playerView = findViewById(R.id.player_view);
        textViewName = findViewById(R.id.textViewName);

        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "simpleExoPlayer"));

        setupExoPlayer();
    }

    /**
     * ❤️ MediaSource represents the media to be played.
     * ❤️ If we've more then one video then ConcatenatingMediaSource is used.
     *   In this way, the first video is played , then the second video is played
     * ❤️ In this method, landscape_video_2.mp4 file is read from external file directory️ of device.
     *  landscape_video_1.mp4 file is read from raw resource. Two different possible reading is shown.
     * */
    private void setupExoPlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);

        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.vr_video_1)); // Getting media from raw resource
        MediaSource secondSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.vr_video_2)); // Getting media from raw resource
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(firstSource, secondSource);  // Plays the first video, then the second video.

        simpleExoPlayer.prepare(concatenatedSource);

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                String[] videoNames = new String[]{"vr_video_1.mp4", "vr_video_2.mp4"};
                String videoName = videoNames[simpleExoPlayer.getCurrentWindowIndex()].toUpperCase();
                textViewName.setText(String.format("Playing: %s", videoName));  // Set video name in textview
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                simpleExoPlayer.retry();
            }
        });
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            playbackPosition = simpleExoPlayer.getCurrentPosition();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            simpleExoPlayer.setPlayWhenReady(false);
            playerView.onResume();
        }
    }

    private void restartPlayer() {
        playerView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.seekTo(currentWindow, playbackPosition);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (simpleExoPlayer != null) {
            restartPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

}
