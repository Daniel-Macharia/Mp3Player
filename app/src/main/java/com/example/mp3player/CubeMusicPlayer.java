package com.example.mp3player;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;

public class CubeMusicPlayer {

    private boolean isPaused = false;

    private MediaPlayer player = new MediaPlayer();
    private String currentSongTitle = "";

    private int currentSongIndex = 0;
    private String currentPlayList = "allsongs";
    //public static ArrayList<String> paths = new ArrayList<>(10);
    //public static ArrayList<String> titles = new ArrayList<>(10);

    private musicItemAdapter adapter;
    private ArrayList<musicItem> musicItems = new ArrayList<>(10);
    private  ArrayList<View> playingSong = new ArrayList<>(10);
    private ImageView image ;
    private Context thisContext;

    public CubeMusicPlayer( Context context )
    {
        this.thisContext = context;

        initPathsAndTitles();
        //queryAudio();
    }

    public void setPlayList( String listName )
    {
        this.currentPlayList = listName;
        initMusicItems();
    }

    private String getPlayList(){return this.currentPlayList;}

    public ArrayList<musicItem> getMusicItems(){return this.musicItems;}

    public boolean isPlaying()
    {
        return !isPaused;
    }

    public void setIsPaused(boolean isPaused)
    {
        this.isPaused = isPaused;
    }

    public int getIndex(int i)
    {
        if( i < 0 )
        {
            return musicItems.size() - 1;
        }
        else if( i == musicItems.size() )
        {
            return 0;
        }
        else
        {
            return i;
        }
    }

    public String getSongName( int index )
    {
        return this.musicItems.get(index).getName();
    }

    public String getArtistName( int index )
    {
        return this.musicItems.get(index).getArtist();
    }

    private void startPlayerService( int index )
    {
        Intent playerServiceIntent = new Intent( thisContext, PlayerService.class);
        playerServiceIntent.putExtra("index", index);
        //set flag to know whether the player is playing or not
        //to avoid recursive call
        playerServiceIntent.putExtra("isPlaying", true);

        thisContext.startService(playerServiceIntent);
    }

    public void play(Handler handler, int index)
    {

        try{
            stopCurrentSong();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    startPlayerService(index);
                    //PlayerService.showNotification(index);
                    MainActivity.playPause.setImageResource(R.drawable.pause);
                    currentSongTitle = musicItems.get(index).getName();
                    MainActivity.setTitle(currentSongTitle);
                    currentSongIndex = index;
                    /*if( startedFromHome )
                        MainActivity.title.setText(items.get( index ).getName());
                    else
                        currentSong.title.setText(items.get(index).getName());*/
                }
            });

            musicItems.get( index ).setIsPlaying( true );

            if( adapter != null )
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setPlayingSong(index);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            String songPath = new String(musicItems.get(index).getData());

            if( player == null )
            {
                player = new MediaPlayer();
            }
            player.setDataSource(songPath);
            player.prepare();
            player.start();

            updateLastPlayed(index);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player = null;
                    player = new MediaPlayer();

                    /*if( startedFromHome )
                        play( handler, items, (index == (items.size() - 1) ) ? 0 : (index + 1), true );
                    else
                        play( handler, items, (index == (items.size() - 1) ) ? 0 : (index + 1), false ); */

                    play(handler, (index == (musicItems.size() - 1) ) ? 0 : (index + 1));
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

    private void updateLastPlayed(int lastIndex)
    {
        LastPlayed p = new LastPlayed(thisContext);
        p.open();
        p.addLastPlayed( currentPlayList, musicItems.get(lastIndex).getName(), musicItems.get(lastIndex).getData());
        p.close();
    }

    public void forwardTo( int time )
    {
        player.seekTo(time);
    }

    public void stopCurrentSong()
    {
        player.stop();
        player.release();
        player = null;
    }

    public void pause()
    {
        if( !isPaused )
        {
            player.pause();
            isPaused = true;
        }
    }

    public void resume()
    {
        if( isPaused )
        {
            player.start();
            isPaused = false;
        }
    }

    public void removeFromCurrentPlaylist( int index )
    {
        musicItems.remove( index );
    }

    public void initPathsAndTitles()
    {
        musicItems = null;
        musicItems = new ArrayList<musicItem>(10);
    }

    public void initMusicItems()
    {
        musicItems = queryAudio(getPlayList());
    }
    public ArrayList<musicItem> queryAudio(String playlist)
    {
        initPathsAndTitles();
        ArrayList<musicItem> result = new ArrayList<musicItem>(10);
        if( !playlist.equals("allsongs") )
        {
            //load from correct playlist
            songsInPlayLists s = new songsInPlayLists(thisContext );
            s.open();
            result = s.getSongsInList( getPlayList() );
            s.close();

            Toast.makeText( thisContext, "Fetching from list: " + currentPlayList, Toast.LENGTH_SHORT).show();
            return result;
        }

        try{
            int allSongsInDevice = 0;
            String[] projections = new String[]{MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ARTIST};

            Cursor c = null;// for android 11 and higher
            Cursor c2 = null;//below android 11
            if(ActivityCompat.checkSelfPermission(thisContext.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED )
            {
                Toast.makeText(thisContext, "Permission not granted!", Toast.LENGTH_SHORT).show();
                //ActivityCompat.requestPermissions( thisContext.get, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
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
                int artistIndex = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
                {

                    File f = new File( c.getString( dataIndex ));
                    if( f.exists() )
                    {
                        /*result.add( new musicItem( new String(c.getString(titleIndex) ),
                                new String(c.getString(dataIndex)),
                                c.getInt(durationIndex) ,
                                new String(c.getString(artistIndex)) ) );*/

                        //titles.add( new String(c.getString(titleIndex)));
                        //paths.add( new String(c.getString(dataIndex)) );

                        result.add( new musicItem( c.getString( titleIndex ), c.getString( dataIndex) ,
                                c.getInt( durationIndex), c.getString(artistIndex) ) );

                        allSongsInDevice++;
                    }

                }

            }
            if( c2 != null)
            {
                int titleIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int dataIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.DATA);
                int artistIndex2 = c2.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                for( c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext() )
                {

                    File f = new File( c2.getString( dataIndex2 ));
                    if( f.exists() )
                    {
                        /*result.add( new musicItem( new String(c2.getString(titleIndex2) ),
                                new String(c2.getString(dataIndex2)),
                                c2.getInt(durationIndex2),
                                new String(c2.getString(artistIndex2)) ) );*/

                        //titles.add( new String(c2.getString(titleIndex2)));
                       // paths.add( new String(c2.getString(dataIndex2)) );

                        result.add( new musicItem( c2.getString( titleIndex2), c2.getString( dataIndex2 ),
                                c2.getInt( durationIndex2), c2.getString(artistIndex2) ));

                        allSongsInDevice++;
                    }

                }
            }
            if( c != null )
                c.close();
            if( c2 != null )
                c2.close();

            //update the total number of songs in the allsongs playlist
            playLists p = new playLists( thisContext );
            p.open();
            p.setNumberOfSongs( "allsongs", allSongsInDevice);
            p.close();

        }catch( Exception e )
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_LONG).show();

        }

        return result;
    }

}

/*class PlayMusicThread implements Runnable
{
    private Thread thread;
    private Handler handler;

    private Context context;
    private String listname;
    public static boolean isPlaying = true;
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

                ArrayList<musicItem> songData = new ArrayList<>(10);
                songsInPlayLists songs = new songsInPlayLists( getContext() );
                songs.open();
                songData = songs.getSongsInList(getListname());
                songs.close();

                for( musicItem songInfo : songData )
                {
                    CubeMusicPlayer.musicItems.add( new musicItem( songInfo.getName(), songInfo.getData(), songInfo.getDuration(), songInfo.getArtist() ));
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
}*/
