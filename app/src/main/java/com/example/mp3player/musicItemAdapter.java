package com.example.mp3player;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
            img.setImageResource( R.drawable.playing_music );
        }
        else
        {
            img.setImageResource(R.drawable.music_item_icon);
        }

        title.setText(music.getName());

        //CubeMusicPlayer.playingSong.add( currentItemView );

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
                        deleteSongFromDevice( m, position );
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
            setNonePlaying();
            View currentItemView = getView(position, null, null);


            ImageView img = currentItemView.findViewById(R.id.playing);
            img.setImageResource( R.drawable.playing_music );

            musicItems.get( position ).setIsPlaying(true);

           // Toast.makeText(getContext(), "Setting current item", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void setNonePlaying()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for( musicItem item : musicItems )
                    item.setIsPlaying(false);
            }
        }).start();
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
            PlayerService.player.removeFromCurrentPlaylist( position );
            notifyDataSetChanged();
        }catch ( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteSongFromDevice( musicItem m, int index )
    {

        try
        {
            //CubeMusicPlayer.deleteSong( m.getData() );

            if(ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //Toast.makeText(getContext(), "Requesting delete permission", Toast.LENGTH_SHORT).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                Collection<Uri> col = new ArrayList<Uri>();

                col.add( Uri.parse( m.getData() ) );
                PendingIntent intent = MediaStore.createDeleteRequest( getContext().getContentResolver(), col);
                getContext().startIntentSender( intent.getIntentSender(), null, 0, 0, 0);
                Toast.makeText(getContext(), "File deleted!", Toast.LENGTH_SHORT).show();

                musicItems.remove( m );

                playLists p = new playLists(getContext());
                p.open();
                p.setNumberOfSongs("allsongs", (p.getNumberOfSongsInList("allsongs") - 1) );
                p.close();
                PlayerService.player.removeFromCurrentPlaylist( index );

                notifyDataSetChanged();
            }
            else
            {
                File file = new File( m.getData() );
                if( file.exists() )
                {

                    if( getContext().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.MediaColumns.DATA + " = ? ", new String[]{m.getData()} ) == 1 )
                    {
                        Toast.makeText(getContext(), "Deleted media.", Toast.LENGTH_SHORT).show();
                        if( file.delete() )
                        {
                            Toast.makeText(getContext(), "File deleted!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //Toast.makeText(getContext(), "Could not delete physical file!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Could not delete file!", Toast.LENGTH_SHORT).show();
                    }

                    musicItems.remove( m );

                    playLists p = new playLists(getContext());
                    p.open();
                    p.setNumberOfSongs("allsongs", (p.getNumberOfSongsInList("allsongs") - 1) );
                    p.close();

                    PlayerService.player.removeFromCurrentPlaylist( index );

                    notifyDataSetChanged();
                }
            }

        }catch( Exception e )
        {
            Toast.makeText( getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
