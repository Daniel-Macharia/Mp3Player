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
    public static ImageView playPause;
    //Context context;
    //Runnable methodPlayOrPause;
    @Override
    protected void onCreate( Bundle savesInstanceState)
    {
        try{

            super.onCreate(savesInstanceState);
            setContentView(R.layout.current_song);

            title = findViewById(R.id.name);
            nxt = findViewById(R.id.next);
            prev = findViewById(R.id.previous);
            playPause = findViewById(R.id.playOrPause);

            //title.setOnClickListener();

            Intent intent = getIntent();
            //Bundle b = intent.getBundleExtra("data");
            int index = intent.getIntExtra("index", 0);

            if( CubeMusicPlayer.isPaused )
            {
                CubeMusicPlayer.player.release();
                CubeMusicPlayer.player = new MediaPlayer();
                //allSongs.stopCurrentSong();
                playMusic(CubeMusicPlayer.paths, CubeMusicPlayer.titles, index);
            }
            else
            {
                CubeMusicPlayer.player.release();
                CubeMusicPlayer.player = new MediaPlayer();
                playMusic(CubeMusicPlayer.paths, CubeMusicPlayer.titles, index);
            }

            nxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(currentSong.this, "Clicked Next", Toast.LENGTH_SHORT).show();
                    if( !CubeMusicPlayer.isPaused )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex + 1 == CubeMusicPlayer.paths.size() ) ? 0 : ( CubeMusicPlayer.currentSongIndex + 1 );
                        CubeMusicPlayer.play(CubeMusicPlayer.paths, CubeMusicPlayer.titles, i, false);
                    }
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( !CubeMusicPlayer.isPaused )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex - 1 < 0 ) ? ( CubeMusicPlayer.paths.size() - 1) : (CubeMusicPlayer.currentSongIndex - 1);
                        CubeMusicPlayer.play(CubeMusicPlayer.paths, CubeMusicPlayer.titles, i, false);
                    }
                }
            });

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( CubeMusicPlayer.isPaused )
                    {
                        CubeMusicPlayer.resume();
                        playPause.setImageResource(R.drawable.pause);
                        MainActivity.playPause.setImageResource(R.drawable.pause);
                    }
                    else
                    {
                        CubeMusicPlayer.pause();
                        playPause.setImageResource(R.drawable.play);
                        MainActivity.playPause.setImageResource(R.drawable.play);
                    }
                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString() , Toast.LENGTH_SHORT).show();
        }


    }

    public static void playMusic(ArrayList<String> paths, ArrayList<String> titles, int index)
    {
        CubeMusicPlayer.play(paths, titles, index, false);
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
