package sample.Playlist;

import java.util.ArrayList;

/**
 * Created by NgThach96 on 5/13/2017.
 */
public class Album {

    private String mTitle;
    private ArrayList<Song> mSongs = new ArrayList<Song>();
    private Artist mOwner;

    public Album(String title, Artist owner) {
        mTitle = title;
        mOwner = owner;
    }

    public boolean addSong(Song song) {
        if (mSongs.contains(song)) {
            System.out.println("Song already exist");
            return false;
        } else if (!song.getmAlbum().equalsIgnoreCase(mTitle)) {
            System.out.println("This song doest not belong to this Album");
            return false;
        } else {
            mSongs.add(song);
            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Album) {
            if (((Album) obj).getmTitle() != null && this.mTitle != null) {
                if (((Album) obj).getmTitle().equalsIgnoreCase(this.mTitle)) return true;
            }
        }
        return false;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public ArrayList<Song> getmSongs() {
        return mSongs;
    }

    public void setmSongs(ArrayList<Song> mSongs) {
        this.mSongs = mSongs;
    }

    public Artist getmOwner() {
        return mOwner;
    }

    public void setmOwner(Artist mOwner) {
        this.mOwner = mOwner;
    }
}
