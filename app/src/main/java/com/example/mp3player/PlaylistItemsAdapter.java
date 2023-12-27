package com.example.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistItemsAdapter extends ArrayAdapter<playlistItems> {
    private ArrayList<playlistItems> itemList = new ArrayList<>(10);
    private Context context;

    public PlaylistItemsAdapter(Context context, ArrayList<playlistItems> itemList )
    {
        super( context, 0, itemList );
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public playlistItems getItem(int i) {
        return itemList.get( i );
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent)
    {
        View currentView = convertView;

        if( currentView == null )
        {
            LayoutInflater.from( context ).inflate( R.layout.playlist_items, parent, false);
        }

        playlistItems item = getItem( position );

        ImageView leftImage, rightImage;
        TextView leftText, rightText;

        leftImage = currentView.findViewById( R.id.left_image );
        leftText = currentView.findViewById( R.id.left_text );

        rightImage = currentView.findViewById( R.id.right_image );
        rightText = currentView.findViewById( R.id.right_text );

        assert currentView != null;

        leftText.setText( item.leftString );
        rightText.setText( item.rightString );

        rightImage.setImageResource( R.drawable.music_item_icon );
        leftImage.setImageResource( R.drawable.music_item_icon );

        return currentView;
    }

}
