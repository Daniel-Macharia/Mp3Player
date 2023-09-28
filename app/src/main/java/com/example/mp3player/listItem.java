package com.example.mp3player;

public class listItem {

    private int imageResource;
    private String name;

    public listItem( int img,String listName)
    {
        imageResource = img;
        name = listName;
    }

    public int getImg()
    {
        return imageResource;
    }

    public String getName()
    {
        return name;
    }

}
