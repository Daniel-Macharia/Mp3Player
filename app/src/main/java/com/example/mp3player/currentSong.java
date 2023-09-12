package com.example.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class currentSong  extends AppCompatActivity {

    TextView title;

    @Override
    protected void onCreate( Bundle savesInstanceState)
    {
        super.onCreate(savesInstanceState);
        setContentView(R.layout.current_song);

        title = findViewById(R.id.name);

        Intent intent = getIntent();
       // String[] titles = intent.getStringArrayExtra("titles");
        //String[] paths = intent.getStringArrayExtra("paths");
        //int index = intent.getIntExtra("index", 0);
        String songTitle = intent.getStringExtra("title");

        if( songTitle == null)
            title.setText("unknown");
        else
            title.setText(songTitle);
    }
}
