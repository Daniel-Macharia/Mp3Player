package com.example.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class OtherPlaylistLayoutClass extends AppCompatActivity {

    private ListView favouritesList;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_playlist_layout);

        try{
            title = findViewById( R.id.list_title );
            favouritesList = findViewById( R.id.favouritesList );

            Intent intent = getIntent();
            String listTitle = intent.getStringExtra("listName");
            title.setText( listTitle );

            songsInPlayLists s = new songsInPlayLists(this);
            s.open();
            ArrayList<String[]> songs = s.getSongsInList(listTitle);
            s.close();

            ArrayList<musicItem> m = new ArrayList<>(6);
            for( String[] song : songs )
            {
                File f = new File( song[1] );

                if( f.exists() )
                {
                    m.add( new musicItem( song[0], song[1] ) );
                }

            }

            musicItemAdapter adapter = new musicItemAdapter(this, m, listTitle);

            favouritesList.setAdapter(adapter);

            favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(OtherPlaylistLayoutClass.this, "You clicked " + i , Toast.LENGTH_SHORT).show();

                     CubeMusicPlayer.isPaused = false;

                    Intent intent = new Intent( OtherPlaylistLayoutClass.this, currentSong.class );
                    intent.putExtra("index", i);
                    startActivity( intent );

                    CubeMusicPlayer.initPathsAndTitles();
                    for( String[] song : songs )
                    {
                        CubeMusicPlayer.titles.add( new String( song[0] ) );
                        CubeMusicPlayer.paths.add( new String( song[1] ) );
                    }

                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
