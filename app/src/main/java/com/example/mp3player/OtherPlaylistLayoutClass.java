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

    private ListView playlist;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_playlist_layout);

        try{
            title = findViewById( R.id.list_title );
            playlist = findViewById( R.id.favouritesList );

            Intent intent = getIntent();
            String listTitle = intent.getStringExtra("listName");
            title.setText( listTitle );

            MainActivity.player.setPlaylist( listTitle );

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
                    m.add( new musicItem( song[0], song[1], song[2], song[3] ) );
                }

            }

            //musicItemAdapter adapter = new musicItemAdapter(this, m, listTitle);
            CubeMusicPlayer.adapter = new musicItemAdapter(this, m, listTitle);
            playlist.setAdapter(CubeMusicPlayer.adapter);

            playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent( OtherPlaylistLayoutClass.this, currentSong.class );
                    intent.putExtra("index", i);
                    startActivity( intent );

                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume()
    {
        CubeMusicPlayer.adapter.notifyDataSetChanged();
        super.onResume();
    }
}
