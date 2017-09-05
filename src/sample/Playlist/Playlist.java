package sample.Playlist;

import java.util.ArrayList;

/**
 * Created by NgThach96 on 5/17/2017.
 */
public class Playlist {
    private String mTitle;
    private ArrayList<Song> mSongs = new ArrayList<>();

    public Playlist(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTitle() {
        return mTitle;
    }

    public ArrayList<Song> getmSongs() {
        return mSongs;
    }

    public boolean addSong(Song song) {
        if (mSongs.contains(song)) {
            System.out.println("Song already exist");
            return false;
        } else {
            mSongs.add(song);
            return true;
        }
    }
}
