package Bean;

import java.io.Serializable;
import java.util.ArrayList;

public class song implements Serializable {
    public int id;
    public int duration;
    public String name;
    public ArrayList<artist> artists;
    public String albumName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<artist> artists) {
        this.artists = artists;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
