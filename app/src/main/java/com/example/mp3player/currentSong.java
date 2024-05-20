package com.example.mp3player;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class currentSong  extends AppCompatActivity {

    public static TextView title;
    private ImageView nxt;
    private ImageView prev;
    public static ImageView playPause;
    private SeekBar songProgress;
    private TextView max;

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
            songProgress = findViewById( R.id.songProgress );

            max = findViewById( R.id.max );

            Intent intent = getIntent();
            int index = intent.getIntExtra("index", 0);

            final int duration = CubeMusicPlayer.musicItems.get( index ).getDuration();
            int min = (int)( duration / 6000);
            int sec = (int)((duration - ( min * 6000) ) / 1000);
            max.setText(min + ":" + sec);
            songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if( fromUser )
                    {

                        int timeToSeek = (int)(progress * duration) / 100;
                        //Toast.makeText(currentSong.this, "duration: " + duration, Toast.LENGTH_SHORT).show();
                        CubeMusicPlayer.forwardTo( timeToSeek );
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

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
