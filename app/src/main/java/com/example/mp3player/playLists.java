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

    private static final String numberOfSongs = "numberOfSongs";

    private static final int dbVersion = 1;

    private Context thisContext;

    private DBHelper myHelper;
    private SQLiteDatabase listDb;

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
                    listName + " TEXT NOT NULL, " +
                    numberOfSongs + " INTEGER NOT NULL ); " );
            db.execSQL("INSERT INTO " + tableName + "( " + listName + ", " + numberOfSongs + ") VALUES ( 'allsongs', 0), ('Favourites', 0);");
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
        cv.put( numberOfSongs, 0);

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

    public ArrayList<playlistItems> getPlayListItems()
    {
        String[] columns = { this.listName };

        Cursor c = listDb.query(tableName, columns, null, null, null, null, null);

        int nameIndex = c.getColumnIndex(listName);

        ArrayList<playlistItems> result = new ArrayList<>(6);

        for( c.moveToFirst() ; !c.isAfterLast(); c.moveToNext() )
        {
            result.add( new playlistItems( c.getString( nameIndex ), 20 ) );
        }

        return result;

    }

    public void incrementNumber( String name )
    {
        try
        {
            int n = getNumberOfSongsInList( name );
            n++;

            ContentValues cv = new ContentValues();
            cv.put(numberOfSongs, n);

            String whereClause = listName + " LIKE('%" + name + "%')";

            listDb.update( tableName, cv, whereClause, null);

            //String updateQuery = "UPDATE " + tableName + " SET " + numberOfSongs + " = " + n +
              //      " WHERE " + listName + " LIKE('%" + name + "%');";

            //listDb.execSQL( updateQuery );
        }catch( Exception e )
        {
            Toast.makeText( thisContext, "Error: " + e, Toast.LENGTH_SHORT).show();
        }


    }

    public int getNumberOfSongsInList( String name )
    {
        int n = 0;
        try
        {
            String getQuery = "SELECT " + numberOfSongs + " FROM " + tableName + " WHERE " + listName + " LIKE('%" + name + "%')";

            Cursor c = listDb.rawQuery( getQuery, null );

            int numberIndex = c.getColumnIndex( numberOfSongs );

            if( c.getCount() > 0 )
            {
                c.moveToFirst();
                n = c.getInt( numberIndex );
            }

            c.close();
        }catch( Exception e )
        {
            Toast.makeText(thisContext, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return n;
    }

}
