package com.example.mp3player;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;

public class allSongs extends AppCompatActivity {

    ListView list;
    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_songs);

        list = findViewById(R.id.musicList);

        try{
            ArrayList<String> paths = new ArrayList<>(10);
            ArrayList<String> type = new ArrayList<>(10);

            ArrayList<String[]> r = queryAudio();
            for( String[] s : r )
            {
                File f = new File(s[2]);
                if( f.exists() )
                {
                    paths.add( s[2] );
                    type.add( s[3] );
                }

            }

            ArrayAdapter arr = new ArrayAdapter(allSongs.this,R.layout.music_items,R.id.musicItem,paths);

            list.setAdapter(arr);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(allSongs.this, "You clicked " + i, Toast.LENGTH_SHORT).show();
                    play(paths, i);
                }
            });
        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void requestRunTimeRead()
    {
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
        }
    }

    private void play(ArrayList<String> paths, final int index)
    {
        //AudioManager a = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if( player.isPlaying() )
            pause();

        try{
            //requestRunTimeRead();
            String songPath = new String(paths.get(index));
            if( player == null )
            {
                player = new MediaPlayer();
            }

            player.setDataSource(songPath);
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player = null;
                    player = new MediaPlayer();
                    play(paths,(index == (paths.size() - 1) ) ? 0 : (index + 1) );
                }
            });

        }catch(Exception e)
        {
            Toast.makeText(allSongs.this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void pause()
    {
        player.stop();
        player.release();
        player = null;
    }

    private ArrayList<String[]> queryAudio()
    {
        ArrayList<String[]> result = new ArrayList<>(10);

        try{
            if( ActivityCompat.checkSelfPermission( allSongs.this, android.Manifest.permission.READ_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED )
            {
               // Toast.makeText(this, "Allow Permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else
            {
               // Toast.makeText(this, "Permission to access External Storage granted", Toast.LENGTH_SHORT).show();
            }


            String[] projections = {MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.MIME_TYPE};
            Cursor c = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);

            //Toast.makeText(allSongs.this, c.toString(), Toast.LENGTH_LONG).show();

            int titleIndex = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int durationIndex = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //int uriIndex = c.getColumnIndex(String.valueOf( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ) ) ;
            int dataIndex = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            int mimeTypeIndex = c.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);

            String s = "";
            for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
            {
                s += "\n" + c.getString(titleIndex);
                result.add( new String[]{ new String(c.getString(titleIndex) ),
                        new String(c.getString(durationIndex) ),
                        new String(c.getString(dataIndex)),
                        new String( c.getString(mimeTypeIndex))} );
            }

           // Toast.makeText(allSongs.this, s, Toast.LENGTH_LONG).show();


        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }

        return result;
    }

}
