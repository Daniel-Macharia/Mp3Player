package com.example.mp3player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PlaylistItemsAdapter extends BaseAdapter {

    private ArrayList<playlistItems> list ;
    private Context context;
    public PlaylistItemsAdapter(Context context, ArrayList<playlistItems> itemList )
    {
        this.list = itemList;
        this.context = context;
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

            assert current != null;

            text.setText( current.getListName() );


            if( current.getListName().equals("Add Playlist") )
            {
                image.setImageResource( R.drawable.add );
                text.setGravity( Gravity.CENTER);
                num.setText("");

                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getNewListName();

                    }
                });
            }
            else
            {
                image.setImageResource( R.drawable.music_item_icon );

                String name = current.getListName();

                int number = 0;
                playLists p = new playLists( getContext() );
                p.open();
                number = p.getNumberOfSongsInList( name );
                p.close();

                if( name.equals("allsongs") )
                {
                    num.setText( allSongs.all_songs_in_device + " songs");
                }
                else
                {
                    num.setText( number + " songs" );
                }

                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try{

                            if( name.equals("allsongs") )
                            {
                                Intent intent = new Intent( getContext(), allSongs.class);
                                getContext().startActivity( intent );
                            }
                            else {
                                Intent intent = new Intent( getContext(), OtherPlaylistLayoutClass.class);
                                intent.putExtra("listName", name );
                                getContext().startActivity( intent );
                            }

                        }catch ( Exception e )
                        {
                            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }


            String name = current.getListName();
            currentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    TextView nameTv = view.findViewById( R.id.name );

                    String nameStr = nameTv.getText().toString();


                    if( name.equals("Add Playlist") )
                    {
                        Toast.makeText(context, "add playlist cannot be selected", Toast.LENGTH_SHORT).show();
                    }
                    else if( name.equals("allsongs") )
                    {
                        Toast.makeText(context, "allsongs cannot be selected", Toast.LENGTH_SHORT).show();
                    }
                    else if( name.equals("Favourites") )
                    {
                        Toast.makeText(context, "Favourites cannot be selected", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        view.setBackgroundColor( view.getResources().getColor( R.color.grey ));

                        return true;
                    }

                    return false;
                }
            });


        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return currentView;
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

                                list.set( list.size() - 2, new playlistItems( name, 0));

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

}
