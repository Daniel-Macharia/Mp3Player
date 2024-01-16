package com.example.mp3player;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class CubeMusicPlayer {

    public static boolean isPaused = false;

    public static MediaPlayer player = new MediaPlayer();
    public static String currentSongTitle = "";

    public static int currentSongIndex = 0;
    public static ArrayList<String> paths = new ArrayList<>(10);
    public static ArrayList<String> titles = new ArrayList<>(10);
    public static ArrayList<View> playingSong = new ArrayList<>(10);
    private static ImageView image ;
    public static Context thisContext;


    public static void play(ArrayList<String> paths, ArrayList<String> titles, final int index, boolean startedFromHome)
    {
        //image = playingSong.get(index).findViewById( R.id.playing );
        //image.setImageResource( R.drawable.music_item_icon );
        //image.setImageResource( R.drawable.playing_music );;
        if( player.isPlaying() )
            stopCurrentSong();

        try{
            //View v = arr.getView(index, new , R.layout.all_songs );
            MainActivity.playPause.setImageResource(R.drawable.pause);
            currentSongTitle = titles.get(index);
            MainActivity.setTitle(currentSongTitle);
            currentSongIndex = index;
            if( startedFromHome )
                MainActivity.title.setText(titles.get( index ));
            else
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

                    //image.setImageResource( R.drawable.music_item_icon );
                    player = null;
                    player = new MediaPlayer();

                    if( startedFromHome )
                        play(paths, titles, (index == (paths.size() - 1) ) ? 0 : (index + 1), true );
                    else
                        play(paths, titles, (index == (paths.size() - 1) ) ? 0 : (index + 1), false );
                }
            });

        }catch(Exception e)
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    public static void stopCurrentSong()
    {
        player.stop();
        player.release();
        player = null;
    }

    public static void pause()
    {
        if( !isPaused )
        {
            player.pause();
            isPaused = true;
        }
    }

    public static void resume()
    {
        if( isPaused )
        {
            player.start();
            isPaused = false;
        }
    }

    public static void initPathsAndTitles()
    {
        paths = null;
        titles = null;
        paths = new ArrayList<>(10);
        titles = new ArrayList<>( 10);
    }
    public static ArrayList<String[]> queryAudio()
    {
        ArrayList<String[]> result = new ArrayList<>(10);
        initPathsAndTitles();

        try{
            String[] projections = {MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA /*,
                    MediaStore.Audio.Media.MIME_TYPE */};
            Cursor c = null;// for android 11 and higher
            Cursor c2 = null;//below android 11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
               // c = getApplicationContext().getContentResolver().query(MediaStore.Downloads.INTERNAL_CONTENT_URI, projections, null, null,null);
               // c2 = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);
                c = thisContext.getContentResolver().query(MediaStore.Downloads.INTERNAL_CONTENT_URI, projections, null, null,null);
                c2 = thisContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);


            }
            else{
                //c = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);
                c = thisContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);

            }

            //Toast.makeText(allSongs.this, c.toString(), Toast.LENGTH_LONG).show();
            //get data from the downloads table of the media store database
            int titleIndex = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int durationIndex = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //int uriIndex = c.getColumnIndex(String.valueOf( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ) ) ;
            int dataIndex = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            /*int mimeTypeIndex = c.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE); */

            for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
            {

                File f = new File( c.getString( dataIndex ));
                if( f.exists() )
                {
                    result.add( new String[]{ new String(c.getString(titleIndex) ),
                            new String(c.getString(durationIndex) ),
                            new String(c.getString(dataIndex)) /*,
                        new String( c.getString(mimeTypeIndex)) */ } );
                    //titles.add( c.getString( titleIndex ) );
                    //paths.add( c.getString( dataIndex ) );

                    allSongs.all_songs_in_device++;
                }

            }

            if( c2 != null)
            {
                int titleIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.DURATION);
                //int uriIndex = c.getColumnIndex(String.valueOf( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ) ) ;
                int dataIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.DATA);
                /* int mimeTypeIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE); */

                for( c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext() )
                {

                    File f = new File( c2.getString( dataIndex2 ));
                    if( f.exists() )
                    {
                        result.add( new String[]{ new String(c2.getString(titleIndex2) ),
                                new String(c2.getString(durationIndex2) ),
                                new String(c2.getString(dataIndex2)) /* ,
                            new String( c2.getString(mimeTypeIndex2)) */ } );
                        //titles.add( c2.getString( titleIndex2 ) );
                        //paths.add( c2.getString( dataIndex2 ) );

                        allSongs.all_songs_in_device++;
                    }

                }
            }


        }catch( Exception e )
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_LONG).show();

        }

        return result;
    }

}
