package sample.Playlist;

import java.util.ArrayList;

/**
 * Created by NgThach96 on 5/13/2017.
 */
public class Artist {

    private String mName;
    private ArrayList<Song> mSongs = new ArrayList<Song>();
    private ArrayList<Album> mAlbums = new ArrayList<Album>();

    public Artist(String name) {
        mName = name;

    }

    public boolean addSong(Song song) {
        if (mSongs.contains(song)) {
            System.out.println("Song already exist");
            return false;
        } else if(!song.getmArtis().equalsIgnoreCase(mName)) {
            System.out.println(song.getmTitle() + " doest not belong to" + mName);
            return false;
        } else {
            mSongs.add(song);
            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Artist) {
            if(((Artist) obj).getmName() != null && this.mName != null) {
                if (((Artist) obj).getmName().equalsIgnoreCase(this.mName)) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public ArrayList<Song> getmSongs() {
        return mSongs;
    }

    public void setmSongs(ArrayList<Song> mSongs) {
        this.mSongs = mSongs;
    }

    public ArrayList<Album> getmAlbums() {
        return mAlbums;
    }

    public void setmAlbums(ArrayList<Album> mAlbums) {
        this.mAlbums = mAlbums;
    }
}
