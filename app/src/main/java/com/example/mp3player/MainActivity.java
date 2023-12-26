package com.example.mp3player;

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
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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

import java.io.File;
import java.io.LineNumberInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView nxt;
    ImageView prev;
    ImageView more;
    public static ImageView playPause;
    static TextView title;

    TextView name;
    ImageView img;
    static Context context;

    private ArrayList<String> titles = new ArrayList<>(10);
    private ArrayList<String> paths = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CubeMusicPlayer.thisContext = this;//init the players context
        context = this;

        requestFileReadAndWritePermission();

        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        playPause = findViewById(R.id.playOrPause);
        title = findViewById(R.id.musicItem);
        more = findViewById(R.id.more);

        initCurrent();

        playPause.setImageResource(R.drawable.play);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreMenus(view, new musicItem("Current Song", "no data"));
            }
        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Next", Toast.LENGTH_SHORT).show();
                if( !CubeMusicPlayer.isPaused )
                {
                    if( CubeMusicPlayer.player.isPlaying() )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex + 1 == CubeMusicPlayer.paths.size() ) ? 0 : ( CubeMusicPlayer.currentSongIndex + 1 );
                        CubeMusicPlayer.play(CubeMusicPlayer.paths, CubeMusicPlayer.titles, i, true);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Loading songs", Toast.LENGTH_SHORT).show();
                        /* if( !allSongs.paths.isEmpty() )
                        {
                            allSongs.play(allSongs.paths, allSongs.titles, 0);
                        } */
                    }

                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Previous", Toast.LENGTH_SHORT).show();
                if( !CubeMusicPlayer.isPaused )
                {
                    if( CubeMusicPlayer.player.isPlaying() )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex - 1 < 0 ) ? ( CubeMusicPlayer.paths.size() - 1) : (CubeMusicPlayer.currentSongIndex - 1);
                        CubeMusicPlayer.play(CubeMusicPlayer.paths, CubeMusicPlayer.titles, i, true);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Loading song data ", Toast.LENGTH_SHORT).show();
                        /* if( !allSongs.paths.isEmpty() )
                        {
                            allSongs.play(allSongs.paths, allSongs.titles, 0);
                        } */
                    }
                }
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Pause", Toast.LENGTH_SHORT).show();
               if( CubeMusicPlayer.player.isPlaying() )
               {
                   if( CubeMusicPlayer.isPaused )
                   {
                       CubeMusicPlayer.resume();
                       playPause.setImageResource(R.drawable.pause);
                       CubeMusicPlayer.isPaused = false;
                   }
                   else {
                       CubeMusicPlayer.pause();
                       playPause.setImageResource(R.drawable.play);
                       CubeMusicPlayer.isPaused = true;
                   }
               }
               else
               {
                   //Toast.makeText(MainActivity.this, "Loading data ", Toast.LENGTH_SHORT).show();
                   startCurrent(0);
                   /* if( CubeMusicPlayer.isPaused )
                   {
                       Toast.makeText(MainActivity.this, "Loading data/resuming player ", Toast.LENGTH_SHORT).show();
                       CubeMusicPlayer.resume();
                       playPause.setImageResource(R.drawable.pause);
                       CubeMusicPlayer.isPaused = false;
                   }
                   else {
                       Toast.makeText(MainActivity.this, "Loading data/pausing player", Toast.LENGTH_SHORT).show();
                       CubeMusicPlayer.pause();
                       playPause.setImageResource(R.drawable.play);
                       CubeMusicPlayer.isPaused = true;
                   } */
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

                    try
                    {
                        playLists p = new playLists(view.getContext());
                        p.open();
                        ArrayList<String[]> lists = p.getPlayLists();
                        p.close();

                        PopupMenu subPopUp = new PopupMenu(view.getContext(), view);
                        for( String list[] : lists )
                        {
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
                                        return false;
                                    }
                                }

                                s.addSong( title, m.getName(), m.getData());
                                s.close();

                                return false;
                            }
                        });

                    }catch( Exception e)
                    {
                        Toast.makeText(view.getContext() , e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }
        });


        popup.inflate( R.menu.more_menus);
        popup.show();
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

    public void loadFavourites( View view)
    {
        Intent intent = new Intent(MainActivity.this, favourites.class);
        startActivity(intent);
    }

    public static Context getContext()
    {
        return context;
    }

    public void initCurrent()
    {
        String []last = new String[3];
        LastPlayed p = new LastPlayed( MainActivity.this );
        p.open();
        last = p.getLastPlayed();
        p.close();

        if( last[0] != null )
        {
            Toast.makeText( MainActivity.this, "There's a last played song", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText( MainActivity.this, "There's no last played song", Toast.LENGTH_SHORT).show();

        ArrayList<String[]> list = CubeMusicPlayer.queryAudio();

        if( !list.isEmpty() )
        {
            last[0] = list.get( 0 )[0];
            last[1] = list.get( 0 )[1];
            last[2] = list.get( 0 )[2];
        }

        for( String []song : list )
        {
            CubeMusicPlayer.paths.add( new String( song[2] ) );
            CubeMusicPlayer.titles.add( new String( song[0] ) );
        }

        title.setText( last[0] );
    }

    private void startCurrent( int index)
    {
        try{
            //Intent intent = new Intent( MainActivity.this, currentSong.class );
            //intent.putExtra( "index", index );
            //startActivity( intent );
            CubeMusicPlayer.play( CubeMusicPlayer.paths, CubeMusicPlayer.titles, index, true);
        }catch( Exception e )
        {
            Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }


}
