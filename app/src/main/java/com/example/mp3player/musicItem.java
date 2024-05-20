package com.example.mp3player;

public class musicItem {

    private String name;
    private String data;
    private String artist;
    private int duration;

    private int imageResource;
    private boolean isPlaying;

    public musicItem(String name,String data, int duration, String artist)
    {
        this.name = new String( name );
        this.data = new String( data );
        this.duration = duration ;
        this.artist = new String( artist );
        this.isPlaying = false;
    }

    public musicItem( musicItem item )
    {
        this.name = new String( item.getName() );
        this.data = new String( item.getData() );
        this.artist = new String( item.getArtist() );
        this.duration =  item.getDuration();
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
    public String getArtist(){return this.artist;}
    public int getDuration(){return this.duration;}
}
