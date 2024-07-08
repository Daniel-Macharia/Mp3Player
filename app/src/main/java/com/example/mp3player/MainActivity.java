package com.example.mp3player;

import static android.widget.LinearLayout.VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.SubMenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.media3.common.Player;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridView;

import java.io.File;
import java.io.LineNumberInputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private ImageView nxt;
    private ImageView prev;
    private ImageView more;
    public static ImageView playPause;
    private TextView title;

    private TextView name;
    private ImageView img;
    //private ListView playlists;
    private GridView playlistGrid;

    private TableLayout listTable;
    private Context context;
    //public static PlayMusicThread player;

    private PlaylistItemsAdapter adapter;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        requestFileReadAndWritePermission();

        Handler handler = new Handler(Looper.getMainLooper() );

        playlistGrid = findViewById( R.id.playlists_list );
        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        playPause = findViewById(R.id.playOrPause);
        title = findViewById(R.id.musicItem);
        more = findViewById(R.id.more);

        seekBar = findViewById( R.id.songProgress );

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Toast.makeText(MainActivity.this, "Progress: " + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        initPlaylist(handler);

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
                    //moreMenus(view, new musicItem(CubeMusicPlayer.musicItems.get( CubeMusicPlayer.currentSongIndex )) );
                }catch( Exception e )
                {
                    Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //handleNextAction( getApplicationContext() );
                int index = getSongDataIndex( PlayerService.player.getMusicItems(), getLastSongData() ) + 1;
                startPlayerService( index, "next");
                title.setText(PlayerService.player.getSongName( index ));
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //handlePrevAction( getApplicationContext() );
                int index = getSongDataIndex( PlayerService.player.getMusicItems(), getLastSongData() ) - 1;
                startPlayerService( index, "previous");//setting title requires service to activity communication
                title.setText(PlayerService.player.getSongName(index));
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPlayerService(getSongDataIndex( PlayerService.player.getMusicItems(), getLastSongData() ), "play");
                //playing or pausing requires activity to service communication
                if( PlayerService.player.isPlaying() )
                {
                    //pause the audio
                    //pausePlayer(getSongDataIndex( PlayerService.player.getMusicItems(), getLastSongData() ));
                    playPause.setImageResource( R.drawable.play );
                }
                else
                {
                    //play the audio
                    //pausePlayer(getSongDataIndex( PlayerService.player.getMusicItems(), getLastSongData() ));
                    playPause.setImageResource( R.drawable.pause );
                }
            }
        });
    }

    private String getLastSongData()
    {
        return new String(getLastSongInfo()[2]);
    }

    private String getLastSongTitle()
    {
        return new String(getLastSongInfo()[1]);
    }

    private String[] getLastSongInfo()
    {
        String[] last;

        LastPlayed l = new LastPlayed(getApplicationContext());
        l.open();
        last = l.getLastPlayed();
        l.close();

        return last;
    }

    private int getSongDataIndex(ArrayList<musicItem> items, String songData )
    {
        int index = 0;
        for( musicItem item : items )
        {
            if( item.getData().equals(songData) )
            {
                break;
            }
            index++;
        }
        return index;
    }

    private void pausePlayer(int index )
    {
        Intent playOrPause = new Intent(MainActivity.this, PlayerService.class );
        playOrPause.putExtra("index", index);
        playOrPause.setAction("play");
        startService(playOrPause);
    }

    private void startPlayerService( int index, String action)
    {
        Intent playerServiceIntent = new Intent( MainActivity.this, PlayerService.class);
        playerServiceIntent.putExtra("index", index);
        playerServiceIntent.putExtra("com.example.mp3player.action", action);
        startService( playerServiceIntent );

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

                    ArrayList<musicItem> songs = s.getSongsInList(title);
                    //check if song exists in the playlist
                    String data = new String( m.getData() );

                    for( musicItem song : songs )
                    {
                        if( data.equals( song.getData() ) )
                        {
                            Toast.makeText(view.getContext(), "Song Already in " + title, Toast.LENGTH_SHORT).show();
                            s.close();
                            return true;
                        }
                    }

                    s.addSong( title, m.getName(), m.getData(),  m.getDuration(), m.getArtist());
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

    public void initPlaylist( Handler handler )
    {
        try {
            String[] last = getLastSongInfo();

            if( last[0] == null)
            {
                title.setText("Unknown");
            }
            title.setText( last[1] );

            //init the player list to the last played playlist
            if( PlayerService.player == null )
            {
                PlayerService.player = new CubeMusicPlayer(getApplicationContext());
                PlayerService.player.setPlayList(last[0]);
            }
        }catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume()
    {
        initPlaylist(new Handler( Looper.getMainLooper() ) );
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
