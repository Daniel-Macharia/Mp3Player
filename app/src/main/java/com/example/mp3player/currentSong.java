package com.example.mp3player;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class currentSong  extends AppCompatActivity {

    public static TextView title;
    ImageView nxt;
    ImageView prev;
    public static ImageView playPause;

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

            Intent intent = getIntent();
            int index = intent.getIntExtra("index", 0);

            Intent playerServiceIntent = new Intent( this, PlayerService.class);
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
            {
                startForegroundService(playerServiceIntent);
            }
            else {
                startService(playerServiceIntent);
            }

            MainActivity.player.play( false, index);

            nxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( !CubeMusicPlayer.isPaused )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex + 1 == CubeMusicPlayer.musicItems.size() ) ? 0 : ( CubeMusicPlayer.currentSongIndex + 1 );
                        MainActivity.player.play( false, i );
                    }
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( !CubeMusicPlayer.isPaused )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex - 1 < 0 ) ? ( CubeMusicPlayer.musicItems.size() - 1) : (CubeMusicPlayer.currentSongIndex - 1);
                        MainActivity.player.play( false, i );
                    }
                }
            });

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( CubeMusicPlayer.isPaused )
                    {
                        MainActivity.player.resume();
                        playPause.setImageResource(R.drawable.pause);
                        MainActivity.playPause.setImageResource(R.drawable.pause);
                    }
                    else
                    {
                        MainActivity.player.pause();
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

}
