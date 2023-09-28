package com.example.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class favourites extends AppCompatActivity {

    ListView favouritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_layout);

        try{

            favouritesList = findViewById( R.id.favouritesList );

            songsInPlayLists s = new songsInPlayLists(this);
            s.open();
            ArrayList<String[]> songs = s.getSongsInList("favourites");
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

            musicItemAdapter adapter = new musicItemAdapter(this, m);

            favouritesList.setAdapter(adapter);

            favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(favourites.this, "You clicked " + i , Toast.LENGTH_SHORT).show();

                     CubeMusicPlayer.isPaused = false;

                    Intent intent = new Intent( favourites.this, currentSong.class );
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
