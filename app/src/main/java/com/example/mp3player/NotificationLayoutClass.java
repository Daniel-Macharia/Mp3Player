package com.example.mp3player;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NotificationLayoutClass extends AppCompatActivity {

    private ImageView prev, playOrPause, next, cancel;
    private TextView songName;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.notification_layout );

        cancel = findViewById( R.id.stop_playing );
        songName = findViewById( R.id.song_name );
        prev = findViewById( R.id.previous );
        next = findViewById( R.id.next );
        playOrPause = findViewById( R.id.playOrPause );

        songName.setText("no song,just a notification");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificationLayoutClass.this, "Clicked cancel on notification", Toast.LENGTH_SHORT).show();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificationLayoutClass.this, "Clicked prev on notification", Toast.LENGTH_SHORT).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificationLayoutClass.this, "Clicked next on notification", Toast.LENGTH_SHORT).show();
            }
        });

        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificationLayoutClass.this, "Clicked play or pause on notification", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
