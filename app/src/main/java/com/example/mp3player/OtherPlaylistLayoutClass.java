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

            //MainActivity.player.setPlaylist( listTitle );

            songsInPlayLists s = new songsInPlayLists(this);
            s.open();
            ArrayList<musicItem> songs = s.getSongsInList(listTitle);
            s.close();

            ArrayList<musicItem> m = new ArrayList<>(6);
            for( musicItem song : songs )
            {
                File f = new File( song.getData() );

                if( f.exists() )
                {
                    m.add( new musicItem( song.getName(), song.getData(), song.getDuration(), song.getArtist() ) );
                }

            }

            musicItemAdapter adapter = new musicItemAdapter(this, m, listTitle);
            //CubeMusicPlayer.adapter = new musicItemAdapter(this, m, listTitle);
            playlist.setAdapter(adapter);

            if( PlayerService.player == null )
            {
                PlayerService.player = new CubeMusicPlayer(getApplicationContext());
            }
            playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*Intent intent = new Intent( OtherPlaylistLayoutClass.this, currentSong.class );
                    intent.putExtra("index", i);
                    startActivity( intent ); */

                    PlayerService.player.setPlayList( new String(listTitle) );
                    Intent serviceIntent = new Intent(OtherPlaylistLayoutClass.this, PlayerService.class);
                    serviceIntent.putExtra("index", i);
                    startService(serviceIntent);

                    //CubeMusicPlayer.currentPlayList = new String( listTitle );

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
        //CubeMusicPlayer.adapter.notifyDataSetChanged();
        super.onResume();
    }
}
