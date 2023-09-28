package com.example.mp3player;

public class musicItem {

    private String name;
    private String data;

    private int imageResource;

    public musicItem(String name,String data)
    {
        this.name = new String( name );
        this.data = new String( data );
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

    public String getData()
    {
        return data;
    }

    public String getName()
    {
        return name;
    }
}
