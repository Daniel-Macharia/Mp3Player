package com.example.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class listItemAdapter extends ArrayAdapter<listItem> {

    public listItemAdapter(Context context, ArrayList<listItem> listItemList)
    {
        super(context, R.layout.lists);
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent)
    {
        View currentView = convertView;

        if( currentView == null )
        {
            currentView = LayoutInflater.from( getContext() ).inflate( R.layout.lists, parent);
        }

        listItem playlist = getItem(position);

        assert playlist != null;

        return currentView;
    }
}
