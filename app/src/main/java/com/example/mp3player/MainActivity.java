package com.example.mp3player;

import static android.widget.LinearLayout.VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.SubMenuBuilder;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridView;

import java.io.File;
import java.io.LineNumberInputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView nxt;
    private ImageView prev;
    private ImageView more;
    public static ImageView playPause;
    static TextView title;

    private TextView name;
    private ImageView img;
    //private ListView playlists;
    private GridView playlistGrid;

    private TableLayout listTable;
    private static Context context;
    public static PlayMusicThread player;

    private PlaylistItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CubeMusicPlayer.thisContext = this;//init the players context
        context = this;

        player = new PlayMusicThread( new Handler( Looper.getMainLooper() ), getApplicationContext(), "allsongs");

        requestFileReadAndWritePermission();

        Handler handler = new Handler(Looper.getMainLooper() );

        //listTable = findViewById( R.id.list_table );

        playlistGrid = findViewById( R.id.playlists_list );
        //playlists.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );

        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        playPause = findViewById(R.id.playOrPause);
        title = findViewById(R.id.musicItem);
        more = findViewById(R.id.more);

        initCurrent(handler);

        try{
            ArrayList<playlistItems> lists = new ArrayList<>(10);
            playLists p = new playLists( MainActivity.this );
            p.open();
            lists = p.getPlayListItems();
            p.close();

            lists.add( new playlistItems( -1,"Add Playlist",0) );

            adapter = new PlaylistItemsAdapter( MainActivity.this, lists);
            playlistGrid.setAdapter( adapter );

        }catch(Exception e)
        {
            Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        playPause.setImageResource(R.drawable.play);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    moreMenus(view, new musicItem(CubeMusicPlayer.musicItems.get( CubeMusicPlayer.currentSongIndex )) );
                }catch( Exception e )
                {
                    Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                        int index = ( CubeMusicPlayer.currentSongIndex + 1 == CubeMusicPlayer.musicItems.size() ) ? 0 : ( CubeMusicPlayer.currentSongIndex + 1 );
                        player.play( true, index );

                }catch( Exception e )
                {
                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                        int index = ( CubeMusicPlayer.currentSongIndex - 1 < 0 ) ? (CubeMusicPlayer.musicItems.size() - 1) : ( CubeMusicPlayer.currentSongIndex - 1 );
                        player.play( true, index );

                }catch ( Exception e )
                {
                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if(CubeMusicPlayer.player.isPlaying() )
                    {
                        Toast.makeText(MainActivity.this, "is playing", Toast.LENGTH_SHORT).show();
                        if( CubeMusicPlayer.isPaused )
                        {
                            //player.resume();
                            playPause.setImageResource(R.drawable.pause);
                        }
                        else
                        {
                            player.pause();
                            playPause.setImageResource(R.drawable.play);
                        }
                    }
                    else
                    {
                        player.startPlaying(true);
                    }

                }catch( IllegalStateException is)
                {
                    if( CubeMusicPlayer.isPaused )
                    {
                        player.resume();
                        playPause.setImageResource(R.drawable.pause);
                    }
                }
                catch( Exception e )
                {
                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static void moreMenus(View view , musicItem m)
    {
        PopupMenu popup = new PopupMenu(view.getContext(), view);

         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();

                if( id == R.id.deleteSong )
                {
                    Toast.makeText(view.getContext(), "Delete clicked", Toast.LENGTH_SHORT).show();
                } else if ( id == R.id.addTo ) {

                    addToAction( view, m);

                }

                return true;
            }
        });


        popup.inflate( R.menu.more_menus);
        popup.show();
    }

    public static void addToAction(View view, musicItem m)
    {
        try
        {
            playLists p = new playLists(view.getContext());
            p.open();
            ArrayList<String[]> lists = p.getPlayLists();
            p.close();

            PopupMenu subPopUp = new PopupMenu(view.getContext(), view);
            for( String list[] : lists )
            {
                if( list[1].equals("allsongs") )
                    continue;
                subPopUp.getMenu().add(list[1]);
            }
            //popup.dismiss();
            subPopUp.show();
            subPopUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String title = menuItem.getTitle().toString();

                    songsInPlayLists s = new songsInPlayLists(view.getContext());
                    s.open();

                    ArrayList<String[]> r = s.getSongsInList(title);
                    //check if song exists in the playlist
                    String data = new String( m.getData() );

                    for( String[] song : r )
                    {
                        if( data.equals( song[1] ) )
                        {
                            Toast.makeText(view.getContext(), "Song Already in " + title, Toast.LENGTH_SHORT).show();
                            s.close();
                            return true;
                        }
                    }

                    s.addSong( title, m.getName(), m.getData());
                    s.close();

                    return true;
                }
            });

        }catch( Exception e)
        {
            Toast.makeText(view.getContext() , e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    public  void requestFileReadAndWritePermission()
    {

        if( ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED )
        {
            // Toast.makeText(this, "Allow Permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }else
        {
            // Toast.makeText(this, "Permission to access External Storage granted", Toast.LENGTH_SHORT).show();
        }
        if( ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions( MainActivity.this, new String[]{ Manifest.permission.READ_MEDIA_AUDIO}, 0);
        }

    }

    public static void setTitle( String s)
    {
        title.setText(CubeMusicPlayer.currentSongTitle);
    }

    public void loadAllSongs(View view)
    {
        Intent intent = new Intent(MainActivity.this, allSongs.class);
        startActivity(intent);
    }

    public static Context getContext()
    {
        return context;
    }

    public void initCurrent( Handler handler )
    {
        try {
            String []last = new String[3];
            LastPlayed p = new LastPlayed( MainActivity.this );
            p.open();
            last = p.getLastPlayed();
            p.close();

            CubeMusicPlayer.queryAudio();

            title.setText( CubeMusicPlayer.musicItems.get(0).getName() );
        }catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void startCurrent( int index)
    {
        try{
            //Intent intent = new Intent( MainActivity.this, currentSong.class );
            //intent.putExtra( "index", index );
            //startActivity( intent );
            //CubeMusicPlayer.play( CubeMusicPlayer.paths, CubeMusicPlayer.titles, index, true);
        }catch( Exception e )
        {
            Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume()
    {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

}
