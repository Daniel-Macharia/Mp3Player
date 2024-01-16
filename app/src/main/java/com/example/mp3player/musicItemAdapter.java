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

import java.util.ArrayList;

public class musicItemAdapter extends ArrayAdapter<musicItem> {

    private String playListName;

    public musicItemAdapter(Context context, ArrayList<musicItem> musicItemList, String playListName)
    {
        super(context, 0, musicItemList);

        this.playListName = playListName;
    }

    @NonNull
    @Override
    public View getView( int position, @NonNull View convertView, @NonNull ViewGroup parent)
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
                moreMenus( view, music, playListName);
            }
        });

        assert music != null;

        img.setImageResource(R.drawable.music_item_icon);
        //img.setImageResource(music.getImageResource());
        title.setText(music.getName());

        CubeMusicPlayer.playingSong.add( currentItemView );

        return currentItemView;
    }

    private void moreMenus( View view, musicItem m, String listName)
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
                        songsInPlayLists s = new songsInPlayLists( view.getContext() );
                        s.open();
                        s.deleteSongFromList( listName, m.getName() );
                        s.close();

                        Toast.makeText( getContext(), "Playlist name: " + listName +
                                "\nsongName: " + m.getName(), Toast.LENGTH_SHORT).show();
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

    private boolean deleteSongFromDevice( musicItem m )
    {

        try
        {

        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
