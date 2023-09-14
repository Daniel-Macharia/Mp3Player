package com.example.mp3player;

public class musicItem {

    private String name;
    private int imageResource;

    public musicItem(String name)
    {
        this.name = name;
        //this.imageResource = imageResource;
    }

    public void setImageResource(int imageResource)
    {
        this.imageResource = imageResource;
    }

    public void setName( String name)
    {
        this.name = name;
    }

    public int getImageResource()
    {
        return imageResource;
    }

    public String getName()
    {
        return name;
    }
}
