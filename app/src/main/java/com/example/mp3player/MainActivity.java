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

    TableLayout listTable;

    LinearLayout add;
    TextView name;
    ImageView img;

    LinearLayout all, favourites;
    static Context context;

    //public static ArrayList<String[]> r = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CubeMusicPlayer.thisContext = this;//init the players context
        //CubeMusicPlayer.queryAudio();

        context = this;
        setUpFavourites();
        //add = findViewById( R.id.addPlayList );
        //img = findViewById(R.id.img );
       // name = findViewById( R.id.name );

        all = findViewById(R.id.all);
        favourites = findViewById(R.id.favourites);
        listTable = findViewById( R.id.playlists);
        add = new LinearLayout(MainActivity.this);
        add.setOrientation( LinearLayout.VERTICAL );
        //add.setWeightSum(1);
        img = new ImageView(MainActivity.this );
        img.setImageResource(R.drawable.add);
        TextView tv = new TextView(MainActivity.this);
        tv.setGravity(Gravity.CENTER);
        tv.setText("Add New Playlist");
        tv.setTextColor( Color.BLUE);

        TableRow tr = new TableRow(MainActivity.this);
        //tr.setMinimumHeight(184);
        //tr.setMinimumWidth( );

        int i = listTable.getChildCount();

        TableRow t = (TableRow) listTable.getChildAt( i - 1);
        int j = t.getChildCount();

        if( j == 2)
        {

            add.addView( img, 0);
            add.addView( tv, 1);
            add.setMinimumHeight(100);
            add.setMinimumWidth(100);
            //add.layout(184, 70, 184, 70);
           // View l = LayoutInflater.from(MainActivity.this).inflate( R.layout.lists, listTable);
            //tr.addView(add, 0);
            //add.layout( t.getChildAt(0).getLeft(), t.getChildAt(0).getTop(), t.getChildAt(0).getRight(), t.getChildAt(0).getBottom() );
            //tr.addView(add, 0);
            tr.setMinimumHeight( LinearLayout.LayoutParams.WRAP_CONTENT);
            tr.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            tr.addView(add, 0);
            listTable.addView(tr, i);
        }


        //Toast.makeText(this, "the" + i + "th child has " + j + " children", Toast.LENGTH_SHORT).show();


        requestFileReadAndWritePermission();

        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        playPause = findViewById(R.id.playOrPause);
        title = findViewById(R.id.musicItem);
        more = findViewById(R.id.more);

        playPause.setImageResource(R.drawable.play);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreMenus(view, new musicItem("Current Song", "no data"));
            }
        });

        //ArrayList<listItem> arr = new ArrayList<>();
       // arr.add( new listItem(0, "all"));
        //listItemAdapter adapter = new listItemAdapter(MainActivity.this, arr);

        //list.setAdapter(adapter);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Clicked Next", Toast.LENGTH_SHORT).show();
                if( !CubeMusicPlayer.isPaused )
                {
                    if( CubeMusicPlayer.player.isPlaying() )
                    {
                        int i = ( CubeMusicPlayer.currentSongIndex + 1 == CubeMusicPlayer.paths.size() ) ? 0 : ( CubeMusicPlayer.currentSongIndex + 1 );
                        CubeMusicPlayer.play(CubeMusicPlayer.paths, CubeMusicPlayer.titles, i);
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
                        CubeMusicPlayer.play(CubeMusicPlayer.paths, CubeMusicPlayer.titles, i);
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
                   Toast.makeText(MainActivity.this, "Loading data ", Toast.LENGTH_SHORT).show();
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

            }
        });




        //r = allSongs.queryAudio();


         all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAllSongs(view);
            }
        });

         favourites.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 loadFavourites(view);
             }
         });

       /* add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText et = new EditText(MainActivity.this);
                Button save = new Button(MainActivity.this);
                et.setHint( "New Playlist Name");
                //et.setInputType();

                et.setTextColor( Color.BLUE );
                save.setText("save");
                save.setGravity(Gravity.CENTER );
                save.setBackgroundResource( R.drawable.background_rounded_corners  );
                save.setBackgroundColor( Color.LTGRAY );
                save.setTextColor(Color.BLUE);

                LinearLayout l = new LinearLayout(MainActivity.this);
                l.setOrientation( LinearLayout.HORIZONTAL );
                l.addView( et, 0);
                l.addView( save, 1);


                add.removeViewAt(1);
                add.addView( l, 1 );


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            String n = et.getText().toString();

                            if( !n.equals("") )
                            {
                                LinearLayout l = new LinearLayout( MainActivity.this );
                                l.setOrientation(LinearLayout.VERTICAL);

                                ImageView i = new ImageView(MainActivity.this);
                                i.setImageResource(R.drawable.play);
                                TextView t = new TextView(MainActivity.this);
                                t.setText(n);
                                t.setGravity( Gravity.CENTER);

                                name.setText("Add Playlist");
                                //img.setImageResource( R.drawable.add );
                                //add.removeViewAt(0);
                                //add.removeViewAt(0);

                                //add.setOrientation(LinearLayout.VERTICAL);
                                //add.addView(img, 0);
                                add.addView( name, 1);
                                l.addView( i, 0);
                                l.addView( t, 1);

                                LinearLayout p = (LinearLayout) add.getParent();

                                p.removeViewAt(1);
                                p.removeViewAt(0);
                                //p.setOrientation(LinearLayout.HORIZONTAL);
                               // p.setMinimumHeight(182);

                                //l.setWeightSum(1);
                                //add.setWeightSum(1);

                                p.addView( l, 0);
                                p.addView( add, 1);

                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Enter a valid name!", Toast.LENGTH_SHORT).show();
                            }
                        }catch( Exception e)
                        {
                            Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        }); */
       //setUpFavourites();
    }

    private void setUpFavourites()
    {
        String f = "favourites";
        playLists p = new playLists(MainActivity.this);
        p.open();

        ArrayList<String[]> lists = p.getPlayLists();

           if (lists.isEmpty()) {
               p.createPlayList(f);
           }

        p.close();

       ArrayList<String[]> songs = new ArrayList<>(10);
       songsInPlayLists s = new songsInPlayLists(MainActivity.this);
       s.open();
       songs = s.getSongsInList(f);
       s.close();

       if( !songs.isEmpty() )
           CubeMusicPlayer.initPathsAndTitles();

        for( String[] song : songs )
        {
            File file = new File(song[1]);
            if( file.exists() )
            {
                CubeMusicPlayer.paths.add( song[1] );
                CubeMusicPlayer.titles.add( song[0] );
            }

        }

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


}
