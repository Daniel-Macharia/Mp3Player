package com.example.mp3player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class songsInPlayLists {

    private static final String playList = "playlist";
    private static final String songName = "title";
    private static final String songData = "path";
    private static final String songId = "songID";
    private static final String tableName = "songs";
    private static final String dbName = "songInPlaylist";
    private static final int dbVersion = 2;

    private Context thisContext;
    private SQLiteDatabase songDb;
    private DBHelper ourHelper;

    private class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context c)
        {
            super(c, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(" CREATE TABLE " + tableName + " ( " +
                            songId + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                            playList + " TEXT NOT NULL , " +
                            songName + " TEXT NOT NULL , " +
                            songData + " TEXT NOT NULL ) ; ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion )
        {
            db.execSQL(" DROP TABLE IF EXISTS " + tableName + " ; ");
            onCreate(db);
        }
        @Override
        public void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL(" DROP TABLE IF EXISTS " + tableName + " ; ");
            onCreate(db);
        }

    }

    public songsInPlayLists(Context c)
    {
        thisContext = c;
    }

    public songsInPlayLists open() throws SQLException
    {
        ourHelper = new DBHelper( thisContext);
        songDb = ourHelper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        ourHelper.close();
    }

    public long addSong(String playListName, String songName, String songData)
    {

        ContentValues cv = new ContentValues();
        cv.put(this.playList, playListName);
        cv.put(this.songName, songName);
        cv.put(this.songData, songData);

        Toast.makeText(thisContext, "Added to " + playListName, Toast.LENGTH_SHORT).show();
        long l = songDb.insert( tableName, null, cv);

        playLists p = new playLists( thisContext );
        p.open();
        p.incrementNumber( playListName );
        p.close();

        return l;
    }

    public ArrayList<String[]> getSongsInList(String ListName)
    {
        //String[] cols = { songName, songData };
        //Cursor c = songDb.query( tableName,cols, playList, new String[]{ ListName }, null, null, null);
        String query = " SELECT " + songName + " , " + songData +
                " FROM " + tableName + " WHERE " + this.playList + " LIKE '%" + ListName + "%' ; ";
        Cursor c = songDb.rawQuery( query,null);

        int songNameIndex = c.getColumnIndex( songName );
        int songDataIndex = c.getColumnIndex(songData);

        ArrayList<String[]> result = new ArrayList<>(10);

        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
        {
            result.add( new String[] { new String( c.getString(songNameIndex) ),
                    new String( c.getString(songDataIndex) ) } );
        }

        return result;
    }

    public boolean deleteSongFromList( String listName, String song_name)
    {

        try
        {
            String query = "DELETE FROM " + tableName +
                    " WHERE " + playList + " LIKE('%" + listName + "%')" + " AND " +
                    songName + " LIKE('%" + song_name + "%');";

            songDb.execSQL( query );

        }catch( Exception e )
        {
            Toast.makeText( thisContext, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
