package com.example.mp3player;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CubeMusicPlayer {

    public static boolean isPaused = false;

    public static MediaPlayer player = new MediaPlayer();
    public static String currentSongTitle = "";

    public static int currentSongIndex = 0;
    //public static ArrayList<String> paths = new ArrayList<>(10);
    //public static ArrayList<String> titles = new ArrayList<>(10);

    public static musicItemAdapter adapter;
    public static ArrayList<musicItem> musicItems = new ArrayList<>(10);
    public static ArrayList<View> playingSong = new ArrayList<>(10);
    private static ImageView image ;
    public static Context thisContext;


    public static void play(Handler handler, ArrayList<musicItem> items, final int index, boolean startedFromHome)
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
                    currentSongTitle = items.get(index).getName();
                    MainActivity.setTitle(currentSongTitle);
                    currentSongIndex = index;
                    if( startedFromHome )
                        MainActivity.title.setText(items.get( index ).getName());
                    else
                        currentSong.title.setText(items.get(index).getName());
                }
            });

            items.get( index ).setIsPlaying( true );
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //adapter.notifyDataSetChanged();
                    adapter.setPlayingSong(index);
                }
            });


            String songPath = new String(items.get(index).getData());

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

                    items.get( index ).setIsPlaying( false );

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //adapter.notifyDataSetChanged();
                            adapter.unsetPlayingSong( index );
                        }
                    });

                    if( startedFromHome )
                        play( handler, items, (index == (items.size() - 1) ) ? 0 : (index + 1), true );
                    else
                        play( handler, items, (index == (items.size() - 1) ) ? 0 : (index + 1), false );
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
        //paths = null;
        //titles = null;
        //paths = new ArrayList<>(10);
        //titles = new ArrayList<>( 10);
        musicItems = null;
        musicItems = new ArrayList<musicItem>(10);
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
            if(ActivityCompat.checkSelfPermission(thisContext.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED )
            {
                Toast.makeText(thisContext, "Permission not granted!", Toast.LENGTH_SHORT).show();
                //ActivityCompat.requestPermissions( thisContext.get, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                c = thisContext.getContentResolver().query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, projections, null, null, null);
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

                        //titles.add( new String(c.getString(titleIndex)));
                        //paths.add( new String(c.getString(dataIndex)) );

                        musicItems.add( new musicItem( c.getString( titleIndex ), c.getString( dataIndex) ) );

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

                        //titles.add( new String(c2.getString(titleIndex2)));
                       // paths.add( new String(c2.getString(dataIndex2)) );

                        musicItems.add( new musicItem( c2.getString( titleIndex2), c2.getString( dataIndex2 ) ));

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

    public static void deleteSong(String songUri)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Collection<Uri> col = new ArrayList<Uri>();

                col.add( Uri.parse( songUri ) );
                PendingIntent intent = MediaStore.createDeleteRequest( thisContext.getContentResolver(), col);
                thisContext.startIntentSender( intent.getIntentSender(), null, 0, 0, 0);
                Toast.makeText(thisContext, "File deleted!", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
            else
            {
                File file = new File( songUri );
                if( file.exists() )
                {

                    if( thisContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.MediaColumns.DATA + " = ? ", new String[]{songUri} ) == 1 )
                    {
                        Toast.makeText(thisContext, "Deleted from media store!", Toast.LENGTH_SHORT).show();
                        if( file.delete() )
                            Toast.makeText(thisContext, "File deleted!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(thisContext, "Could not delete physical file!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(thisContext, "Could not delete file!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }
        }catch( Exception e )
        {
            Toast.makeText(thisContext, "Error deleting file: " + e, Toast.LENGTH_SHORT).show();
        }
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
                    CubeMusicPlayer.musicItems.add( new musicItem( songInfo[0], songInfo[1] ));
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

            CubeMusicPlayer.play( handler, CubeMusicPlayer.musicItems, getIndex(), isFromHome());
        }catch( Exception e )
        {
            toast("Error: " + e);
        }
    }
}
