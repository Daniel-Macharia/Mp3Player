package com.example.mp3player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class PlayerService extends Service {


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText( this, "Started service.", Toast.LENGTH_SHORT).show();
        sendNotification();
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {

        Toast.makeText( this, "Stopped Service.", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    private void sendNotification()
    {
        try{
            int id = 444;
            Intent intent = new Intent( this, MainActivity.class );
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK );

            Notification n = new Notification.Builder(getApplicationContext() )
                    .setSmallIcon( R.drawable.next )
                    .setContentTitle( "Background service running.")
                    .setContentIntent(pendingIntent).build();

            NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE );
            mgr.notify(id, n);


        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
