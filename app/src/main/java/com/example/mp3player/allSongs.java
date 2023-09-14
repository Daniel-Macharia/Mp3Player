package com.example.mp3player;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

    ListView list;
    static ImageView current;
    public static boolean isPaused = false;

    private static MediaPlayer player = new MediaPlayer();
    public static String currentSongTitle = "";

    public static int currentSongIndex = 0;
    private static Context context;

    public static ArrayList<String> paths = new ArrayList<>(10);
     public static ArrayList<String> titles = new ArrayList<>(10);

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



        try{
            ArrayList<musicItem> items = new ArrayList<>(10);
            //ArrayList<String> paths = new ArrayList<>(10);
           // ArrayList<String> titles = new ArrayList<>(10);

            //ArrayList<String[]> r = queryAudio();
            MainActivity.r = queryAudio();
            for( String[] s : MainActivity.r )
            {
                File f = new File(s[2]);
                if( f.exists() )
                {
                    items.add( new musicItem( s[0]));
                    paths.add( s[2] );
                    titles.add( s[0] );
                }

            }

            //ArrayAdapter arr = new ArrayAdapter(allSongs.this,R.layout.music_items,R.id.musicItem,titles);

            //list.setAdapter(arr);
            musicItemAdapter arr = new musicItemAdapter(allSongs.this, items);
            list.setAdapter(arr);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(allSongs.this, "You clicked " + i, Toast.LENGTH_SHORT).show();
                    //play(paths, titles, i);
                    //currentSong c = new currentSong(allSongs.this);

                    current = view.findViewById(R.id.playing);
                    current.setImageResource(R.drawable.playing_music);


                    Intent intent = new Intent( allSongs.this, currentSong.class);
                    Bundle b = new Bundle();
                    b.putStringArrayList("paths",paths);
                    b.putStringArrayList("titles", titles);
                    b.putInt("index", i);
                    intent.putExtra("data", b);
                    startActivity(intent);

                    //play(paths, titles, i);
                }
            });
        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public static void play(ArrayList<String> paths, ArrayList<String> titles, final int index)
    {
        //AudioManager a = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if( player.isPlaying() )
            stopCurrentSong();

        try{
            currentSongTitle = titles.get(index);
            MainActivity.setTitle(currentSongTitle);
            currentSongIndex = index;
            currentSong.title.setText(titles.get(index));

            //title.setText(titles.get(index));
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
                    play(paths, titles, (index == (paths.size() - 1) ) ? 0 : (index + 1) );
                }
            });

        }catch(Exception e)
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private static void stopCurrentSong()
    {
        player.stop();
        player.release();
        player = null;
    }

    public static void pause()
    {
        player.pause();
        isPaused = true;
    }

    public static void resume()
    {
        player.start();
        isPaused = false;
    }


    public  ArrayList<String[]> queryAudio()
    {
        ArrayList<String[]> result = new ArrayList<>(10);

        try{
            if( ActivityCompat.checkSelfPermission( allSongs.this, android.Manifest.permission.READ_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED )
            {
               // Toast.makeText(this, "Allow Permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions( allSongs.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
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
