package com.example.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;

public class musicItemAdapter extends ArrayAdapter<musicItem> {

    private String playListName;
    private ArrayList<musicItem> musicItems;

    public musicItemAdapter(Context context, ArrayList<musicItem> musicItemList, String playListName)
    {
        super(context, 0, musicItemList);

        this.playListName = playListName;
        this.musicItems = musicItemList;
    }

    @NonNull
    @Override
    public View getView( int position, View convertView, ViewGroup parent)
    {
        View currentItemView = convertView;

        if( currentItemView == null )
        {
            currentItemView = LayoutInflater.from(getContext()).inflate( R.layout.music_items, parent, false);
        }

        musicItem music = getItem(position);

        ImageView img = currentItemView.findViewById(R.id.playing);
        TextView title = currentItemView.findViewById(R.id.musicItem);

        ImageView more = currentItemView.findViewById( R.id.more );
        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                moreMenus( view, music, playListName, position);
            }
        });

        assert music != null;

        if( music.getIsPlaying() )
        {
            img.setImageDrawable( AppCompatResources.getDrawable( getContext(), R.drawable.playing_music ) );
        }
        else
        {
            img.setImageResource(R.drawable.music_item_icon);
        }

        title.setText(music.getName());

        CubeMusicPlayer.playingSong.add( currentItemView );

        return currentItemView;
    }

    private void moreMenus( View view, musicItem m, String listName, int position)
    {
        PopupMenu popup = new PopupMenu( view.getContext(), view );
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();

                if( id == R.id.deleteSong )
                {
                    if( listName.equals("allsongs") )
                    {
                        //delete from device
                        deleteSongFromDevice( m );
                    }
                    else
                    {
                        deleteSongFromList(listName, m, position);
                    }

                    return true;
                }
                else if( id == R.id.addTo )
                {
                    MainActivity.addToAction( view, m);
                    return true;
                }

                return false;
            }
        });

        popup.inflate( R.menu.more_menus );
        popup.show();

    }

    public void setPlayingSong( int position )
    {
        try
        {
            View currentItemView = getView(position, null, null);


            ImageView img = currentItemView.findViewById(R.id.playing);
            img.setImageResource( R.drawable.playing_music );

            Toast.makeText(getContext(), "Setting current item", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void unsetPlayingSong( int position )
    {
        try
        {
            View currentItemView = getView(position, null, null);


            ImageView img = currentItemView.findViewById(R.id.playing);
            img.setImageResource( R.drawable.music_item_icon );
            Toast.makeText(getContext(), "Unsetting current item", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSongFromList( String listName, musicItem item, int position)
    {
        try
        {
            songsInPlayLists s = new songsInPlayLists( getContext() );
            s.open();
            s.deleteSongFromList( listName, item.getName() );
            s.close();

            musicItems.remove( position );
            notifyDataSetChanged();
        }catch ( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteSongFromDevice( musicItem m )
    {

        try
        {
            CubeMusicPlayer.deleteSong( m.getData() );
        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
