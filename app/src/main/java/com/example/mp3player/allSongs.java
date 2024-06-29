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

    private ArrayList<musicItem> songs = new ArrayList<>(10);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_songs);

        list = findViewById(R.id.musicList);

        //MainActivity.player.setPlaylist("allsongs");

        context = this;

        //CubeMusicPlayer.queryAudio();

        try{
            ArrayList<musicItem> items = new ArrayList<>(10);

            if( PlayerService.player == null )
            {
                PlayerService.player = new CubeMusicPlayer(getApplicationContext());
                PlayerService.player.setPlayList("allsongs");
            }

            songs = PlayerService.player.queryAudio("allsongs");

             for( musicItem song : songs )
            {

                    items.add( new musicItem( song ) );

            }

            //Toast.makeText(context, "Setting list adapter", Toast.LENGTH_SHORT).show();
            musicItemAdapter adapter = new musicItemAdapter(allSongs.this, songs, "allsongs");
            //adapter = new musicItemAdapter(allSongs.this, items, "allsongs");
            list.setAdapter( adapter );
            //Toast.makeText(context, "All songs adapter is set", Toast.LENGTH_SHORT).show();


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*Intent intent = new Intent( allSongs.this, currentSong.class);
                    intent.putExtra("index", i);
                    startActivity(intent); */
                    PlayerService.player.setPlayList("allsongs");
                    //Toast.makeText(allSongs.this, "Starting service", Toast.LENGTH_SHORT).show();
                    Intent serviceIntent = new Intent( allSongs.this, PlayerService.class);
                    serviceIntent.putExtra("index", i);
                    startService( serviceIntent );
                 //   Toast.makeText(allSongs.this, "Started service", Toast.LENGTH_SHORT).show();
                    //CubeMusicPlayer.currentPlayList = new String("allsongs");
                }
            });
        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume()
    {
        //CubeMusicPlayer.adapter.notifyDataSetChanged();
        super.onResume();
    }

}
