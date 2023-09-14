package com.example.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class musicItemAdapter extends ArrayAdapter<musicItem> {

    public musicItemAdapter(Context context, ArrayList<musicItem> musicItemList)
    {
        super(context, 0, musicItemList);
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

        assert music != null;

        img.setImageResource(R.drawable.music_item_icon);
        //img.setImageResource(music.getImageResource());
        title.setText(music.getName());

        return currentItemView;
    }

}
