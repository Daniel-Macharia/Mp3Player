package com.example.mp3player;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
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


    public static void play(Handler handler, ArrayList<String> paths, ArrayList<String> titles, final int index, boolean startedFromHome)
    {
        if( player == null )
        {
            player = new MediaPlayer();
        }
        else if( player.isPlaying() )
        {
            stopCurrentSong();
        }

        try{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.playPause.setImageResource(R.drawable.pause);
                    currentSongTitle = titles.get(index);
                    MainActivity.setTitle(currentSongTitle);
                    currentSongIndex = index;
                    if( startedFromHome )
                        MainActivity.title.setText(titles.get( index ));
                    else
                        currentSong.title.setText(titles.get(index));
                }
            });

            String songPath = new String(paths.get(index));

            if( player == null )
            {
                player = new MediaPlayer();
            }
            else
            {
                if(isPaused)
                    resume();
            }
            player.setDataSource(songPath);
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player = null;
                    player = new MediaPlayer();

                    if( startedFromHome )
                        play( handler, paths, titles, (index == (paths.size() - 1) ) ? 0 : (index + 1), true );
                    else
                        play( handler, paths, titles, (index == (paths.size() - 1) ) ? 0 : (index + 1), false );
                }
            });

        }catch( IllegalStateException is)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(thisContext, "Exception in play method", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
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
        allSongs.all_songs_in_device = 0;
        ArrayList<String[]> result = new ArrayList<>(10);
        initPathsAndTitles();

        try{
            String[] projections = new String[]{MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA };

            Cursor c = null;// for android 11 and higher
            Cursor c2 = null;//below android 11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                c = thisContext.getContentResolver().query(MediaStore.Downloads.INTERNAL_CONTENT_URI, projections, null, null, null);
            }

            c2 = thisContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);

            if( c != null)
            {
                //get data from the downloads table of the media store database
                int titleIndex = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationIndex = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int dataIndex = c.getColumnIndex(MediaStore.Audio.Media.DATA);

                for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
                {

                    File f = new File( c.getString( dataIndex ));
                    if( f.exists() )
                    {
                        result.add( new String[]{ new String(c.getString(titleIndex) ),
                                new String(c.getString(durationIndex) ),
                                new String(c.getString(dataIndex)) } );

                        titles.add( new String(c.getString(titleIndex)));
                        paths.add( new String(c.getString(dataIndex)) );

                        allSongs.all_songs_in_device++;
                    }

                }

            }
            if( c2 != null)
            {
                int titleIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int dataIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.DATA);

                for( c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext() )
                {

                    File f = new File( c2.getString( dataIndex2 ));
                    if( f.exists() )
                    {
                        result.add( new String[]{ new String(c2.getString(titleIndex2) ),
                                new String(c2.getString(durationIndex2) ),
                                new String(c2.getString(dataIndex2)) } );

                        titles.add( new String(c2.getString(titleIndex2)));
                        paths.add( new String(c2.getString(dataIndex2)) );

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

class PlayMusicThread implements Runnable
{
    private Thread thread;
    private Handler handler;

    private Context context;
    private String listname;
    private int index;
    private boolean startedFromHome;

    public PlayMusicThread(Handler handler, Context context, String listname)
    {
        this.handler = handler;
        this.thread = null;
        this.context = context;
        this.listname = new String( listname );
        this.index = 0;
    }

    private Context getContext()
    {
        return this.context;
    }

    private void toast( String message )
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText( getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPathsAndTitles()
    {
        try
        {
            if( getListname().equals( "allsongs") )
            {
                CubeMusicPlayer.queryAudio();
            }
            else
            {
                CubeMusicPlayer.initPathsAndTitles();

                ArrayList<String[]> songData = new ArrayList<>(10);
                songsInPlayLists songs = new songsInPlayLists( getContext() );
                songs.open();
                songData = songs.getSongsInList(getListname());
                songs.close();

                for( String[] songInfo : songData )
                {
                    CubeMusicPlayer.paths.add( songInfo[1] );
                    CubeMusicPlayer.titles.add( songInfo[0] );
                }
            }
        }catch( Exception e)
        {
            toast("Error: " + e);
        }
    }

    public void setPlaylist( String listname )
    {
        this.listname = listname;
    }
    private String getListname(){return this.listname;}
    private int getIndex(){return this.index;}

    public void pause()
    {
        CubeMusicPlayer.pause();
    }

    public void resume()
    {
        CubeMusicPlayer.resume();
    }

    public void play( boolean startedFromHome, int index )
    {
        try
        {
            this.index = index;
            startPlaying(startedFromHome);
        }catch( Exception e )
        {
            toast("Error: " + e);
        }
    }

    private boolean isFromHome(){return this.startedFromHome;}

    public void startPlaying( boolean startedFromHome)
    {
        try
        {
            this.startedFromHome = startedFromHome;
            if( thread == null )
            {
                thread = new Thread( this );
                thread.start();
            }
            else if( !thread.isAlive() )
            {
                thread = null;
                thread = new Thread( this );
                thread.start();
            }
        }catch(Exception e)
        {
            toast("Error: " + e);
        }
    }

    @Override
    public void run()
    {
        try
        {
            initPathsAndTitles();

            CubeMusicPlayer.play( handler, CubeMusicPlayer.paths, CubeMusicPlayer.titles, getIndex(), isFromHome());
        }catch( Exception e )
        {
            toast("Error: " + e);
        }
    }
}
