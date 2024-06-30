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
    public ImageView playPause;
    private SeekBar songProgress;
    private TextView max;
    private int index;
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
            index = intent.getIntExtra("index", 0);
            title.setText(PlayerService.player.getSongName(index));

            songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if( fromUser )
                    {

                       // int timeToSeek = (int)(progress * duration) / 100;
                        //Toast.makeText(currentSong.this, "duration: " + duration, Toast.LENGTH_SHORT).show();
                       // CubeMusicPlayer.forwardTo( timeToSeek );
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            nxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPlayerService(++index, "next");
                    title.setText(PlayerService.player.getSongName(index));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPlayerService( --index, "previous" );
                    title.setText(PlayerService.player.getSongName(index ));
                }
            });

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPlayerService(index, "play");

                    if( PlayerService.player.isPlaying() )
                    {
                        playPause.setImageResource( R.drawable.play );
                    }
                    else
                    {
                        playPause.setImageResource(R.drawable.pause);
                    }
                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString() , Toast.LENGTH_SHORT).show();
        }

    }

    private void startPlayerService( int index, String action )
    {
        Intent playerServiceIntent = new Intent( currentSong.this, PlayerService.class );
        playerServiceIntent.putExtra("index", index);
        playerServiceIntent.putExtra("com.example.mp3player.action", action);
        startService(playerServiceIntent);
    }

    private void playOrPausePlayer( int index )
    {
        Intent playerServiceIntent = new Intent( currentSong.this, PlayerService.class );
        playerServiceIntent.putExtra("index", index);
        playerServiceIntent.setAction("play");
        startService(playerServiceIntent);
    }

}
