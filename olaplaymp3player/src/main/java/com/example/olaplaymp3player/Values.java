package com.example.olaplaymp3player;

/**
 * Created by VICKY on 12/16/2017.
 */

public class Values {

    private String image,title,artists, songURL;

    public Values(String img, String tit, String art, String url) {
        // TODO Auto-generated constructor stub
      title = tit;
      image = img;
      artists =art;
        songURL = url;
    }

    public String getTitle() {
        return title;
    }
    public String getImageUrl() {
        return image;
    }
    public String getArtists() {
        return artists;
    }
    public String getSongURL() {
        return songURL;
    }
}
