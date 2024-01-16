package com.example.mp3player;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;

public class allSongs extends AppCompatActivity {

    public static int all_songs_in_device = 0;
    ListView list;
    static ImageView current;
    private static Context context;

    private ArrayList<String[]> songs = new ArrayList<>(10);
    //private static musicItemAdapter arr;

    //currentSong s = new currentSong("unknown", this::playOrPauseMethod);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_songs);

        list = findViewById(R.id.musicList);
        //current = findViewById( R.id.playing);
        //current.setImageResource(R.drawable.playing_music);

        context = this;

        //CubeMusicPlayer.queryAudio();

        try{
            ArrayList<musicItem> items = new ArrayList<>(10);

            songs = CubeMusicPlayer.queryAudio();

             for( String[] song : songs )
            {

                    items.add( new musicItem( song[0], song[2] ) );

            }

            musicItemAdapter arr = new musicItemAdapter(allSongs.this, items, "allsongs");
            list.setAdapter(arr);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(allSongs.this, "You clicked " + i, Toast.LENGTH_SHORT).show();
                    //play(paths, titles, i);
                    //currentSong c = new currentSong(allSongs.this);

                    //current = view.findViewById(R.id.playing);
                    //current.setImageResource(R.drawable.playing_music);
                    //CubeMusicPlayer.isPaused = false;



                    Intent intent = new Intent( allSongs.this, currentSong.class);
                    //Bundle b = new Bundle();
                   // b.putStringArrayList("paths", CubeMusicPlayer.paths);
                    //b.putStringArrayList("titles", CubeMusicPlayer.titles);
                    //b.putInt("index", i);
                    //intent.putExtra("data", b);
                    intent.putExtra("index", i);
                    startActivity(intent);

                    CubeMusicPlayer.initPathsAndTitles();
                    for( String[] song : songs )
                    {

                        CubeMusicPlayer.titles.add( song[0] );
                        CubeMusicPlayer.paths.add( song[2] );

                    }

                    //play(paths, titles, i);
                }
            });
        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


}
