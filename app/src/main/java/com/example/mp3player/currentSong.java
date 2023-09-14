package com.example.mp3player;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class currentSong  extends AppCompatActivity {

    public static TextView title;
    ImageView nxt;
    ImageView prev;
    ImageView playPause;
    //Context context;
    //Runnable methodPlayOrPause;
    @Override
    protected void onCreate( Bundle savesInstanceState)
    {
        super.onCreate(savesInstanceState);
        setContentView(R.layout.current_song);

        title = findViewById(R.id.name);
        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        playPause = findViewById(R.id.playOrPause);

        //title.setOnClickListener();

        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("data");
        ArrayList<String> paths = b.getStringArrayList("paths");
        ArrayList<String> titles = b.getStringArrayList("titles");
        int index = b.getInt("index");

        playMusic(paths, titles, index);


        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(currentSong.this, "Clicked Next", Toast.LENGTH_SHORT).show();
                if( !allSongs.isPaused )
                {
                    int i = ( allSongs.currentSongIndex + 1 == paths.size() ) ? 0 : ( allSongs.currentSongIndex + 1 );
                    allSongs.play(paths, titles, i);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !allSongs.isPaused )
                {
                    int i = ( allSongs.currentSongIndex - 1 < 0 ) ? ( paths.size() - 1) : (allSongs.currentSongIndex - 1);
                    allSongs.play(paths, titles, i);
                }
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( allSongs.isPaused )
                {
                    allSongs.resume();
                    playPause.setImageResource(R.drawable.pause);
                }
                else
                {
                    allSongs.pause();
                    playPause.setImageResource(R.drawable.play);
                }
            }
        });


    }

    public static void playMusic(ArrayList<String> paths, ArrayList<String> titles, int index)
    {
        allSongs.play(paths, titles, index);
    }

    public void playOrPauseMethod()
    {
        // Toast.makeText(s, "clicked Pause", Toast.LENGTH_SHORT).show();
    }

    public void next(View view)
    {
        Toast.makeText(view.getContext(), "Next Clicked", Toast.LENGTH_SHORT).show();
    }

}
