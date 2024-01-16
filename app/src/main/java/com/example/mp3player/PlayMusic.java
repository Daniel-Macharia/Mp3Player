package com.example.mp3player;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class PlayMusic implements Runnable {

    private Thread thread;
    private Handler handler;

    private Context context;

    public PlayMusic( Handler handler, Context context )
    {
        this.thread = null;
        this.handler = handler;
        this.context = context;
    }

    public void start()
    {
        try
        {
            if( thread != null )
            {
                if( thread.isAlive() )
                    thread.interrupt();

                this.thread = null;
            }

            this.thread = new Thread( this );
            thread.start();
        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error from application: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public Context getContext(){return this.context; }

    public void createNotification()
    {
        try
        {
            int idInt = 1316;
            String id = "1316";
            String name = "MusiCube";

            RemoteViews remoteViews = new RemoteViews( getContext().getPackageName(),R.layout.notification_layout);

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService( Context.NOTIFICATION_SERVICE );

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
            {
                NotificationChannel channel = new NotificationChannel( id, name, NotificationManager.IMPORTANCE_DEFAULT );

                notificationManager.createNotificationChannel( channel );
            }

            Bundle b = new Bundle();
            b.putParcelable("remoteViews", remoteViews);

            Intent intent = new Intent( getContext(), ProcessActionsService.class);
            intent.putExtra("bundle", b);
            intent.setAction("cancelAction");
            PendingIntent pendingIntent = PendingIntent.getService( getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

            remoteViews.setOnClickPendingIntent( R.id.stop_playing, pendingIntent );
            //remoteViews.setImageViewResource( R.id.stop_playing, R.drawable.add );

            remoteViews.setOnClickPendingIntent( R.id.previous, pendingIntent );
            remoteViews.setOnClickPendingIntent( R.id.next, pendingIntent );

            NotificationCompat.Builder builder = new NotificationCompat.Builder( getContext(), id )
                    .setContentTitle("MusicCube")
                    .setContentText("notification")
                    .setSmallIcon( R.drawable.music_icon )
                    .setSilent( true )
                    .setVisibility( NotificationCompat.VISIBILITY_PUBLIC )
                    .setContent( remoteViews )
                    .setContentIntent( pendingIntent );

            Notification notification = builder.build();


            if(ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED )
            {
                notificationManager.notify( idInt, notification );
            }
            else
            {
                //ActivityCompat.requestPermissions( getContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
            }

        }catch( Exception e )
        {
            Toast.makeText(getContext(), "Error from application: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //@Override
    public void run()
    {
        try
        {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    createNotification();
                }
            });

        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error from application: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private class ProcessActionsService extends Service
    {
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            try
            {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View view = inflater.inflate( R.layout.notification_layout, null);

                if( intent.getAction().equals("cancelAction") )
                {
                    Bundle b = intent.getBundleExtra("bundle");

                    RemoteViews remoteViews = (RemoteViews) b.get("remoteViews");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            remoteViews.setImageViewResource( R.id.stop_playing, R.drawable.add );
                            remoteViews.setTextViewText( R.id.song_name, "current song");
                        }
                    });
                }
            }catch( Exception e )
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText( getContext(), "Error from application: " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return Service.START_STICKY_COMPATIBILITY;

        }
    }


}
