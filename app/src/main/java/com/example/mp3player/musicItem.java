package com.example.mp3player;

public class musicItem {

    private String name;
    private String data;

    private int imageResource;
    private boolean isPlaying;

    public musicItem(String name,String data)
    {
        this.name = new String( name );
        this.data = new String( data );
        this.isPlaying = false;
    }

    public musicItem( musicItem item )
    {
        this.name = new String( item.getName() );
        this.data = new String( item.getData() );
        this.isPlaying = item.getIsPlaying();
    }
    public void setIsPlaying( boolean isPlaying )
    {
        this.isPlaying = isPlaying;
    }

    public boolean getIsPlaying(){return this.isPlaying; }

    public void setImageResource(int imageResource)
    {
        this.imageResource = imageResource;
    }

    public void setName( String name)
    {
        this.name = new String(name);
    }
    public void setData( String data ) { this.data = new String(data); }

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
