package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView nxt;
    ImageView prev;
    ImageView playPause;
    static TextView title;

    public static ArrayList<String[]> r = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        playPause = findViewById(R.id.playOrPause);
        title = findViewById(R.id.musicItem);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Next", Toast.LENGTH_SHORT).show();
                if( !allSongs.isPaused )
                {
                    int i = ( allSongs.currentSongIndex + 1 == allSongs.paths.size() ) ? 0 : ( allSongs.currentSongIndex + 1 );
                    allSongs.play(allSongs.paths, allSongs.titles, i);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Previous", Toast.LENGTH_SHORT).show();
                if( !allSongs.isPaused )
                {
                    int i = ( allSongs.currentSongIndex - 1 < 0 ) ? ( allSongs.paths.size() - 1) : (allSongs.currentSongIndex - 1);
                    allSongs.play(allSongs.paths, allSongs.titles, i);
                }
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Pause", Toast.LENGTH_SHORT).show();
                if( allSongs.isPaused )
                {
                    allSongs.resume();
                    playPause.setImageResource(R.drawable.pause);
                }
                else {
                    allSongs.pause();
                    playPause.setImageResource(R.drawable.play);
                }
            }
        });

        //r = allSongs.queryAudio();


    }

    public static void setTitle( String s)
    {
        title.setText(allSongs.currentSongTitle);
    }

    public void loadAllSongs(View view)
    {
        Intent intent = new Intent(MainActivity.this, allSongs.class);
        startActivity(intent);
    }

}