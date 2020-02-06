package com.example.aliazaz.exoplayer_sample.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.aliazaz.exoplayer_sample.R;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAdapter();

        verifyPermission();
    }

    private void setupAdapter() {
        listView = findViewById(R.id.listView);
        String[] items = {"Landscape videos playlist", "VR videos playlist"};
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(items)));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent activity = null;

                switch (i) {
                    case 0:
                        activity = new Intent(MainActivity.this, VideoActivity1.class);
                        break;
                    case 1:
                        activity = new Intent(MainActivity.this, VideoActivity2.class);
                        break;
                }

                startActivity(activity);

            }
        });
    }

    public void verifyPermission() {
        //Check permission for devices API > 22
        Permissions.check(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, null,
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        copyVideoToDevice();
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
     */
    private void copyVideoToDevice() {
        File file = new File(MyUtil.getVideoFilesPathStoredInDevice());
        if (!file.exists()) {
            file.mkdirs();
        }

        InputStream inputStream = getResources().openRawResource(R.raw.landscape_video_2);
        String filename = getResources().getResourceEntryName(R.raw.landscape_video_2) + ".mp4";

        File f = new File(MyUtil.getVideoFilesPathStoredInDevice(), filename);
        if(f.exists()){
            return;
        }

        try {
            OutputStream out = new FileOutputStream(f);
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
