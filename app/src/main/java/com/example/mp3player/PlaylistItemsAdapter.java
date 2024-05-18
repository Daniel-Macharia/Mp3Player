package com.example.mp3player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

public class PlaylistItemsAdapter extends BaseAdapter {

    private ArrayList<playlistItems> list ;
    private Context context;
    playlistItems all, fav, add;
    public PlaylistItemsAdapter(Context context, ArrayList<playlistItems> itemList )
    {
        this.list = itemList;
        this.context = context;
        fav = list.remove(1);
        all = list.remove(0);
        add = list.remove(list.size() - 1);
        list.sort(new Comparator<playlistItems>() {
            @Override
            public int compare(playlistItems o1, playlistItems o2) {
                return o1.getListName().compareTo(o2.getListName());
            }
        });
        list.add(0, new playlistItems( all.getListId(), "allsongs", all.getNumber()));
        list.add( 1, new playlistItems( fav.getListId(), "Favourites", fav.getNumber()));
        list.add(new playlistItems( add.getListId(),"Add Playlist", add.getNumber()));
    }

    private Context getContext()
    {
        return this.context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public playlistItems getItem(int i) {
        return this.list.get( i );
    }

    @NonNull
    @Override
    public View getView( int position, View convertView, ViewGroup parent)
    {
        View currentView = convertView;

        if( currentView == null )
        {
            currentView = LayoutInflater.from( getContext() ).inflate( R.layout.playlists, parent, false);
        }

        try
        {
            playlistItems current = getItem( position );

            ImageView image;
            TextView text, num;

            image = currentView.findViewById( R.id.image);
            text = currentView.findViewById( R.id.name );
            num = currentView.findViewById( R.id.number );
            ImageView moreView = currentView.findViewById( R.id.more );

            assert current != null;

            text.setText( current.getListName() );

            int currentId = current.getListId();
            if( currentId == add.getListId() )
            {
                image.setImageResource( R.drawable.add );
                text.setGravity( Gravity.CENTER);
                num.setText("");

                moreView.setVisibility(View.INVISIBLE);
                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getNewListName();

                    }
                });
            }else if( currentId == all.getListId() )
            {
                image.setImageResource( R.drawable.baseline_queue_music_24);
                num.setText( allSongs.all_songs_in_device + " songs");
                moreView.setVisibility(View.INVISIBLE);
                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent( getContext(), allSongs.class);
                        getContext().startActivity( intent );
                    }
                });
            }
            else
            {
                image.setImageResource( R.drawable.baseline_queue_music_24 );

                String name = current.getListName();

                int number = 0;
                playLists p = new playLists( getContext() );
                p.open();
                number = p.getNumberOfSongsInList( name );
                p.close();
                num.setText( number + " songs" );

                if( currentId == fav.getListId() )
                {
                    moreView.setVisibility( View.INVISIBLE );
                }
                else
                {
                    moreView.setVisibility( View.VISIBLE );
                    moreView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMenus(v, current.getListId());
                        }
                    });
                }
                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent( getContext(), OtherPlaylistLayoutClass.class);
                        intent.putExtra("listName", name );
                        getContext().startActivity( intent );
                    }
                });
            }

        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return currentView;
    }

    private void showMenus( View view, int listId)
    {
        PopupMenu popup = new PopupMenu( getContext(), view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if( id == R.id.deleteList )
                {
                    playLists p = new playLists(getContext());
                    p.open();
                    p.deletePlayList( listId );
                    p.close();

                    loadFromDb();
                    return true;
                }

                return false;
            }
        });

        popup.inflate( R.menu.list_menu);
        popup.show();
    }

    private void getNewListName()
    {
        try
        {
            //Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate( R.layout.dialog, null );

            EditText newListName = view.findViewById( R.id.new_list_name );

            builder.setView( view );
            builder.setTitle("Enter Playlist Name")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try
                            {
                                String name = newListName.getText().toString();

                                playLists p = new playLists( getContext() );
                                p.open();
                                p.createPlayList(name);
                                p.close();

                                //list.set( list.size() - 2, new playlistItems( name, 0));
                                //PlaylistItemsAdapter.this.notifyDataSetChanged();
                                loadFromDb();
                                Toast.makeText(context, "Created Playlist: " + name, Toast.LENGTH_SHORT).show();
                            }catch( Exception e )
                            {
                                Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //dialog.dismiss();
                        }
                    }).create().show();

        }catch( Exception e )
        {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromDb()
    {
        playLists p = new playLists( getContext() );
        p.open();
        list = new ArrayList<>(10);
        list = p.getPlayListItems();
        p.close();
        list.add( new playlistItems(-1, "Add Playlist", 0) );

        fav = list.remove(1);
        all = list.remove(0);
        add = list.remove(list.size() - 1);
        list.sort(new Comparator<playlistItems>() {
            @Override
            public int compare(playlistItems o1, playlistItems o2) {
                return o1.getListName().compareTo(o2.getListName());
            }
        });
        list.add(0, new playlistItems( all.getListId(), "allsongs", all.getNumber()));
        list.add( 1, new playlistItems( fav.getListId(), "Favourites", fav.getNumber()));
        list.add(new playlistItems( add.getListId(),"Add Playlist", add.getNumber()));

        notifyDataSetChanged();
    }

}
