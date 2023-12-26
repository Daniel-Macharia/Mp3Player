package com.example.mp3player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LastPlayed
{
    private static final String lastPlayList = "lastPlayList";
    private static final String lastSongTitle = "lastSongTitle";
    private static final String lastSongPath = "lastSongPath";
    private static final String lastSongId = "lastSongId";
    private static final String tableName = "lastSongTable";
    private static final String dbName = "lastSongDatabase";
    private static final int dbVersion = 1;

    private Context thisContext;

    private DBHelper helper;

    private SQLiteDatabase db;

    private class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper( Context context )
        {
            super( context, dbName, null, dbVersion );
        }

        @Override
        public void onCreate( SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE " + tableName + " ( " +
                    lastSongId + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    lastPlayList + " TEXT NOT NULL, " +
                    lastSongTitle + "TEXT NOT NULL, " +
                    lastSongPath + "TEXT NOT NULL )");
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
        {
            db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
            onCreate( db );
        }
    }

    public LastPlayed( Context context )
    {
        this.thisContext = context;

    }

    public LastPlayed open() throws SQLException
    {
        helper = new DBHelper( thisContext );
        db = helper.getWritableDatabase();

        return this;
    }
    public void close()
    {
        helper.close();
    }

    public long addLastPlayed( String lastList, String lastTitle, String lastPath)
    {
        ContentValues cv = new ContentValues();
        cv.put(lastPlayList, lastList);
        cv.put(lastSongTitle, lastTitle);
        cv.put(lastSongPath, lastPath);

        return db.insert( tableName, null, cv);
    }

    public String[] getLastPlayed()
    {
        String[] result = new String[3];
        String query = "SELECT * FROM " + tableName + ";";

        Cursor c = db.rawQuery( query, null );

        if( c.getCount() < 1)
        {
            return new String[]{null, null, null};
        }

        int listIndex = c.getColumnIndex( lastPlayList );
        int titleIndex = c.getColumnIndex( lastSongTitle );
        int pathIndex = c.getColumnIndex( lastSongPath );

        result[0] = c.getString( listIndex );
        result[1] = c.getString( titleIndex );
        result[2] = c.getString( pathIndex );

        return result;
    }
}
