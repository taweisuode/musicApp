package com.pengge.musicplayer.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.pengge.musicplayer.R;

public class SongListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        getMessage();
    }
    private void getMessage() {
        Intent intent = getIntent();
        String playlist_id = intent.getStringExtra("playlist_id");
        Toast.makeText(this,playlist_id,Toast.LENGTH_SHORT).show();
    }
}
