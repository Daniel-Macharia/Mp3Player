package com.example.mp3player;

public class playlistItems {

    private int listId;
    private String listName;
    private int number;

    public playlistItems( int listId, String listName, int number)
    {
        this.listId = listId;
        this.listName = new String( listName );
        this.number = number;
    }

    public void setListName( String listName ){this.listName = listName; };

    public void setNumber( int number ){this.number = number; };

    public String getListName(){ return this.listName; }

    public int getNumber(){ return this.number; }
    public int getListId(){return this.listId;}

}
