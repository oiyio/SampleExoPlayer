package com.example.aliazaz.exoplayer_sample.ui;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class VideoActivity1 extends AppCompatActivity {

    private static final String TAG = VideoActivity1.class.getName();
    TextView textViewName;
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    String directory;
    DataSource.Factory dataSourceFactory;
    Long playbackPosition = 0L;
    int currentWindow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playing);

        playerView = findViewById(R.id.player_view);
        textViewName = findViewById(R.id.textViewName);

        directory = Environment.getExternalStorageDirectory() + File.separator + "EXOPLAYER-SAMPLE";
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "simpleExoPlayer"));

        verifyPermission();
    }

    public void verifyPermission() {
        //Check permission for devices API > 22
        Permissions.check(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, null,
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        copyVideoToDevice();
                        setupExoPlayer();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        verifyPermission();
                    }
                });

    }

    /**
     * ❤️ landscape_video_2.mp4 file is copied to device
     * */
    private void copyVideoToDevice() {
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }

        InputStream inputStream = getResources().openRawResource(R.raw.landscape_video_2);
        String filename = getResources().getResourceEntryName(R.raw.landscape_video_2) + ".mp4";

        File f = new File(filename);

        if (!f.exists()) {
            try {
                OutputStream out = new FileOutputStream(new File(directory, filename));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }
                inputStream.close();
                out.close();
            } catch (Exception e) {
                Log.i("Test", "CopyRaw::copyResources - " + e.getMessage());
            }
        }
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

        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(directory + File.separator + "landscape_video_2" + ".mp4"));  // Getting media from device storage
        MediaSource secondSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.landscape_video_1)); // Getting media from raw resource
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(firstSource, secondSource);

        simpleExoPlayer.prepare(concatenatedSource);

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                String[] videoNames = new String[]{"landscape_video_2.mp4", "landscape_video_1.mp4"};
                String videoName = videoNames[simpleExoPlayer.getCurrentWindowIndex()].toUpperCase();
                textViewName.setText(String.format("Playing: %s", videoName));  // Set video name in textview
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                simpleExoPlayer.retry();
            }
        });

    }

    private void stopPlayer() {
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
            stopPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            stopPlayer();
        }
    }

}
