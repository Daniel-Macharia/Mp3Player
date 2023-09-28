package com.example.mp3player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class playLists {

    private static final String dbName = "SortedSongs";
    private static final String tableName = "playList";

    private static final String listID = "listID";
    private static final String listName = "listName";

    private static final int dbVersion = 1;

    private Context thisContext;

    private DBHelper myHelper;
    SQLiteDatabase listDb;

    private class DBHelper extends SQLiteOpenHelper
    {
        DBHelper( Context c)
        {
            super(c, dbName, null, dbVersion);
        }
        @Override
        public  void onCreate(SQLiteDatabase db)
        {
            db.execSQL(" CREATE TABLE " + tableName +" ( " +
                    listID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    listName + " TEXT NOT NULL ); " );
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
        {
            db.execSQL( " DROP TABLE IF EXISTS " + tableName + " ; " );

            onCreate( db );
        }

        @Override
        public void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL( " DROP TABLE IF EXISTS " + tableName + " ; " );

            onCreate(db);
        }


    }

    public playLists( Context c)
    {
        this.thisContext = c;
    }

    public playLists open() throws SQLException
    {
        myHelper = new DBHelper(thisContext);
        listDb = myHelper.getWritableDatabase();
       // listDb.execSQL(" INSERT INTO " +  tableName + "( " + listName + " ) " + " VALUES " + " (favourites);");

        return this;
    }

    public void close()
    {
        myHelper.close();
    }

    public long createPlayList( String name)
    {
        ContentValues cv = new ContentValues();
        cv.put(listName, name);

       // Toast.makeText(thisContext, "Adding to database", Toast.LENGTH_SHORT).show();
        long l = listDb.insert( tableName, null, cv );
        //Toast.makeText(thisContext, "after adding to database", Toast.LENGTH_SHORT).show();

        return l;
    }

    public ArrayList<String[]> getPlayLists()
    {
        String[] columns = { this.listID, this.listName };

        Cursor c = listDb.query(tableName, columns, null, null, null, null, null);

        int idIndex = c.getColumnIndex(listID);
        int nameIndex = c.getColumnIndex(listName);

        ArrayList<String[]> result = new ArrayList<>(6);

        for( c.moveToFirst() ; !c.isAfterLast(); c.moveToNext() )
        {
            result.add( new String[]{ new String( c.getString( idIndex ) ),
                       new String( c.getString( nameIndex ) ) });
        }

        return result;

    }

}
