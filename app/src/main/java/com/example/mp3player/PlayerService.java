package com.example.mp3player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ServiceCompat;

public class PlayerService extends Service {

    private NotificationManager nm;
    private int id = 444;

    @Override
    public void onCreate()
    {
        nm = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        sendNotification();
        //startForeground(id, getNotification());
    }

    @Override
    public void onDestroy()
    {
        nm.cancel(id);
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
            if( Build.VERSION.SDK_INT < Build.VERSION_CODES.O )
            {
                startForeground(444, getNotification());
            }

            toast( "Started service." );
            //sendNotification();
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

    private void sendNotification()
    {
        try{
            Intent intent = new Intent( this, MainActivity.class );
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK );

            Notification n = new Notification.Builder(getApplicationContext() )
                    .setSmallIcon( R.drawable.next )
                    .setContentTitle( "Background service running.")
                    .setContentIntent(pendingIntent).build();

            //NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE );
            nm.notify(id, n);
        }catch( Exception e )
        {
            toast("Error: " + e );
        }
    }

    private Notification getNotification()
    {
        Notification n = new Notification.Builder(getApplicationContext() )
                .setSmallIcon( R.drawable.next )
                .setContentTitle( "Background service running.")
                .setContentText("Playing music").build();
                //.setContentIntent(pendingIntent).build();

        return n;
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
