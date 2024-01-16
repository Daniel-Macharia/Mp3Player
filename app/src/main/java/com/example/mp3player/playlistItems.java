package com.example.mp3player;

public class playlistItems {
    public String listName;
    public int number;

    public playlistItems( String listName, int number)
    {
        this.listName = new String( listName );
        this.number = number;
    }

    public void setListName( String listName ){this.listName = listName; };

    public void setNumber( int number ){this.number = number; };

    public String getListName(){ return this.listName; }

    public int getNumber(){ return this.number; }

}
