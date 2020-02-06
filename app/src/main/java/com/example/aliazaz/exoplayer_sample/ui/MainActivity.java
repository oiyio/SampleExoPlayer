package com.example.aliazaz.exoplayer_sample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.aliazaz.exoplayer_sample.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAdapter();
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
}
