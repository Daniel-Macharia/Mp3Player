package com.example.mp3player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;

public class currentSong  extends AppCompatActivity {

    public TextView title;
    private ImageView nxt;
    private ImageView prev;
    public ImageView playPause;
    private SeekBar songProgress;
    private TextView max, min;
    private int index;
    private UpdateProgressBar update;

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
            min = findViewById( R.id.min );

            Intent intent = getIntent();
            index = intent.getIntExtra("index", 0);
            title.setText(PlayerService.player.getSongName(index));

            int songDuration = PlayerService.player.getDuration(index);
            songProgress.setMax( songDuration );
            max.setText("" + songDuration);
            update = new UpdateProgressBar(songProgress, new Handler(Looper.getMainLooper() ), getApplicationContext(), max, min, songDuration );
            update.start();
            //thread = new Thread( update );
            //thread.start();

            songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if( fromUser )
                        {
                            Toast.makeText(currentSong.this, "Progress: " + progress, Toast.LENGTH_SHORT).show();
                            PlayerService.player.forwardTo( progress );
                            update.updateCurrent( progress );
                        }
                    }catch (Exception e)
                    {
                        Toast.makeText(currentSong.this, "Error: " + e, Toast.LENGTH_SHORT).show();
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
                    try {
                        index = (((index + 1) > (PlayerService.player.getMusicItems().size() - 1) ) ? 0 : (index + 1) );
                        startPlayerService( index, "next");
                        title.setText(PlayerService.player.getSongName(index));
                        update.setMax( PlayerService.player.getDuration( index ) );
                        update.start();
                        /*thread = null;
                        thread = new Thread( update );
                        thread.start();*/
                    }catch( Exception e )
                    {
                        Toast.makeText(currentSong.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        index = (( (index - 1) < 0) ? (PlayerService.player.getMusicItems().size() - 1) : (index - 1) );
                        startPlayerService( index, "previous" );
                        title.setText(PlayerService.player.getSongName(index ));
                        update.setMax( PlayerService.player.getDuration( index ) );
                        update.start();
                        /*thread = null;
                        thread = new Thread( update );
                        thread.start();*/
                    }catch( Exception e )
                    {
                        Toast.makeText(currentSong.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startPlayerService(index, "play");

                        if( PlayerService.player.isPlaying() )
                        {
                            playPause.setImageResource( R.drawable.play );
                            update.setPaused(true);
                        }
                        else
                        {
                            playPause.setImageResource(R.drawable.pause);
                            update.setPaused(false);
                        }
                    }catch( Exception e )
                    {
                        Toast.makeText(currentSong.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, "Error: " + e.toString() , Toast.LENGTH_SHORT).show();
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
    @Override
    public void onDestroy()
    {
        update.exit();
        super.onDestroy();
    }

}

class UpdateProgressBar implements Runnable
{
    private int progress;
    private int max;

    private Handler handler;
    private Thread thread;
    private Context context;
    private SeekBar seekBar;
    private TextView maxTimeText, minTimeText;
    private boolean paused;

    public UpdateProgressBar( SeekBar seekBar, Handler handler, Context context, TextView maxTimeText, TextView minTimeText, int max )
    {
        this.seekBar = seekBar;
        this.max = max;
        this.handler = handler;
        this.context = context;
        this.maxTimeText = maxTimeText;
        this.minTimeText = minTimeText;
        this.paused = false;
    }

    public void start()
    {
        try
        {
            if( thread != null )
            {
                if( thread.isAlive() )
                {
                    thread.interrupt();
                }
                thread = null;
            }

            thread = new Thread( this );
            thread.start();

        }
        catch( Exception e )
        {
            toast("Error starting thread: " + e );
        }
    }

    public void exit()
    {
        try
        {
            if( thread != null )
            {
                if( thread.isAlive() )
                {
                    thread.interrupt();
                }

                thread = null;
            }
        }catch( Exception e )
        {
            toast("Error killing worker thread: " + e);
        }
    }

    public void updateCurrent( int progress )
    {
        this.progress = progress;
    }
    public void setPaused( boolean paused ){this.paused = paused;}

    public void setMax( int max )
    {
        this.max = max;
        updateCurrent(0);
    }

    private Context getContext(){return this.context;}

    private void toast( String message )
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void run()
    {

        try
        {
            int s = max / 1000;
            int ms = s / 60;
            final int h = ms / 60;

            final int m = (ms % 60);
            s = (s % 60);

            int finalSec = s;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    maxTimeText.setText("" + ( h == 0 ? "" : (h < 10 ? ("" + h + ":" ) : (h + ":" ))) + (m < 10 ? "0" + m : m) + ":" + (finalSec < 10 ? "0" + finalSec : finalSec));
                    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
                    {
                        seekBar.setMin(0);
                    }
                }
            });

            progress = 0;
            while( progress < max )
            {
                progress += 1;
                final int p = ( ( progress % 2 == 0) ? (progress + 1) : progress);
                int second =  p / 1000;
                int mins = second / 60;
                final int hour = mins / 60;
                final int min = ( mins % 60);

                final int finalSecond = (second % 60 );

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        minTimeText.setText("" + (hour == 0 ? "" : (hour < 10 ? ("0" + hour + ":" ) : (hour + ":" )) ) + (min < 10 ? "0" + min : min ) + ":" + (finalSecond < 10 ? "0" + finalSecond : finalSecond) );
                        seekBar.setProgress( p );
                    }
                });

                try
                {
                    //android.os.SystemClock.sleep(1);
                    Thread.sleep(1);//for smooth running, rest for only a millisecond
                    while( paused )//keep sleeping when paused
                    {
                        Thread.sleep(1);//android.os.SystemClock.sleep(1);
                    }
                }catch (InterruptedException ie )
                {
                    toast("Thread interrupted");
                    break;
                }
                catch( Exception e )
                {
                    toast("Error: " + e);
                }
            }
             handler.post(new Runnable() {
                 @Override
                 public void run() {
                     minTimeText.setText("00:00");
                     seekBar.setProgress(0);
                 }
             });
        }catch( Exception e )
        {
            toast( "Error: " + e );
        }
    }
}