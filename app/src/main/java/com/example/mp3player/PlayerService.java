package com.example.mp3player;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.DeviceInfo;
import androidx.media3.common.ForwardingPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.Size;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.MediaStyleNotificationHelper;

import java.util.List;

public class PlayerService extends Service {
    private int id = 444;

    @Override
    public void onCreate()
    {

    }

    @Override
    public void onDestroy()
    {
        toast("Destroying service");
        MainActivity.saveLastPlayedSongDetails( getApplicationContext() );
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {

            String action = intent.getStringExtra("com.example.mp3player.action");

            if( action == null )//not triggered by notification action
            {
                String songName = intent.getStringExtra("songTitle");
                String artistName = intent.getStringExtra("artistName");

                if( songName == null )
                {
                    songName = "untitled";
                }

                if( artistName == null )
                {
                    artistName = "unknown";
                }

                sendNotification(songName, artistName);

                //if( Build.VERSION.SDK_INT < Build.VERSION_CODES.O )
                {
                    startForeground(444, getNotification( songName, artistName));
                }
            }
            else //triggered by notification action
            {
                if( action.equals("next") )
                {
                    MainActivity.handleNextAction( getApplicationContext() );
                }

                if( action.equals("play") )
                {
                    MainActivity.handlePlayAction( getApplicationContext() );
                }

                if( action.equals("previous") )
                {
                    MainActivity.handlePrevAction( getApplicationContext() );
                }
            }


        }catch (Exception e)
        {
            toast( "Error: " + e );
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {

        toast("Stopped Service." );
        return super.onUnbind(intent);
    }

    private void sendNotification(String songName, String artistName)
    {
        try{
            //toast("sending notification");

            //MediaSession session = new MediaSession.Builder(getApplicationContext(), null).build();

            String channelId = "444";
            String channelName = "cube_music_player";
            NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE );


            int PREV = 1, PLAY = 2, NEXT = 3, CANCEL = 4, MAIN = 5;
            Intent prevIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action", "previous");
            Intent nextIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","next");
            Intent playIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","play");
            Intent cancelIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","cancel");

            Intent mainActivityIntent = new Intent( getApplicationContext(), MainActivity.class );

            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
            {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH );
                mgr.createNotificationChannel( channel );
            }


            PendingIntent prevPendingIntent = PendingIntent.getService( getApplicationContext(), PREV, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            PendingIntent playPendingIntent = PendingIntent.getService( getApplicationContext(), PLAY, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            PendingIntent nextPendingIntent = PendingIntent.getService( getApplicationContext(), NEXT, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            PendingIntent cancelPendingIntent = PendingIntent.getService( getApplicationContext(), CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            PendingIntent mainActivityPendingIntent = PendingIntent.getActivity( getApplicationContext(), MAIN, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if(ActivityCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED )
            {
                //ActivityCompat.requestPermissions( this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS }, 1);
                toast("Permission to post notifications is denied!");
            }

            Notification n;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon( R.drawable.music_item_icon )
                    .addAction( R.drawable.prev, "previous", prevPendingIntent )
                    .addAction( R.drawable.play, "pause", playPendingIntent )
                    .addAction( R.drawable.next, "next", nextPendingIntent )
                    .addAction( R.drawable.cancel, "Cancel", cancelPendingIntent )
                    .setColor(getResources().getColor(R.color.cream, null) )
                    .setContentTitle( new String( songName ) )
                    .setContentText( new String( artistName ) )
                    .setSilent(true)
                    .setContentIntent( mainActivityPendingIntent );

            n = builder.build();

            mgr.notify(id, n);
            //toast("after sending notification");
        }catch( Exception e )
        {
            toast("Error: " + e );
        }
    }

    private Notification getNotification(String songName, String artistName)
    {
        String channelId = "444";
        String channelName = "cube_music_player";
        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE );

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
        {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH );
            mgr.createNotificationChannel( channel );
        }

        int PREV = 1, PLAY = 2, NEXT = 3, CANCEL = 4, MAIN = 5;
        Intent prevIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","previous");
        Intent nextIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","next");
        Intent playIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","play");
        Intent cancelIntent = new Intent( getApplicationContext(), PlayerService.class).putExtra("com.example.mp3player.action","cancel");

        Intent mainActivityIntent = new Intent( getApplicationContext(), MainActivity.class );

        PendingIntent prevPendingIntent = PendingIntent.getService( getApplicationContext(), PREV, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent playPendingIntent = PendingIntent.getService( getApplicationContext(), PLAY, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent nextPendingIntent = PendingIntent.getService( getApplicationContext(), NEXT, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent cancelPendingIntent = PendingIntent.getService( getApplicationContext(), CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity( getApplicationContext(), MAIN, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return  new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon( R.drawable.music_item_icon )
                .addAction( R.drawable.prev, "previous", prevPendingIntent )
                .addAction( R.drawable.play, "pause", playPendingIntent )
                .addAction( R.drawable.next, "next", nextPendingIntent )
                .addAction( R.drawable.cancel, "Cancel", cancelPendingIntent )
                .setColor(getResources().getColor(R.color.cream, null) )
                .setContentTitle( new String( songName ) )
                .setContentText( new String( artistName ) )
                .setSilent(true)
                .setContentIntent( mainActivityPendingIntent ).build();
    }

    private void toast( String message )
    {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}

class ControlsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context c, Intent data )
    {
        try
        {
            if( data == null )
                return;
            String action = data.getStringExtra("com.example.mp3player.action");

            if( action == null )
                return;

            new Handler( Looper.getMainLooper() ).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c.getApplicationContext(), action + " clicked" , Toast.LENGTH_SHORT).show();
                }
            });
        }catch( Exception e )
        {
            new Handler( Looper.getMainLooper() ).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c.getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
