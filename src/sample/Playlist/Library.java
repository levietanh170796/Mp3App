package sample.Playlist;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by NgThach96 on 5/13/2017.
 */
public final class Library {
    private static ArrayList<Song> mSongs = new ArrayList<Song>();
    private static ArrayList<Artist> mArtists = new ArrayList<Artist>();
    private static ArrayList<Album> mAlbums = new ArrayList<Album>();

    //not used for now
    private static ArrayList<Playlist> mPlayists = new ArrayList<Playlist>();

    public static void importMP3FromFolder(File file) {
        if (file != null && file.isDirectory()) {
            File[] songPaths = file.listFiles();
            for (File path : songPaths) {
                if (path.getName().endsWith(".MP3") || path.getName().endsWith(".mp3")) {

                    //saved imported song's file
                    //new
                    FileSupporter.saveSongToAllSongFile(path.getAbsolutePath());
                    //////

                    Song song = new Song(path);
                    Artist artist = new Artist(song.getmArtis());
                    Album album = new Album(song.getmAlbum(), artist);
                    if (!songIsAdded(song)) {
                        addSong(song);
                        addSongToAlbum(album, song);
                        addSongToArtist(artist, song);
                    }
                }
            }
        } else {
            System.out.println("import fail, can't reconized file");
        }
    }

    /////////////////////////////////////////////////////////////////////////new
    public static void importMP3FromSavedFile() {
        try {
            File file = new File(System.getProperty("user.dir"));
            File AllSongPath = new File(file, FileSupporter.ALL_SONG);
            if(!AllSongPath.exists()) return;
            FileReader fileReader = new FileReader(AllSongPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while( (line = bufferedReader.readLine()) != null) {
                Song s = new Song(new File(line));
                Artist artist = new Artist(s.getmArtis());
                Album album = new Album(s.getmAlbum(), artist);
                if(!songIsAdded(s)) {
                    addSong(s);
                    addSongToArtist(artist, s);
                    addSongToAlbum(album, s);
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////////////

    public static void readPlaylistDirectory() {
        File rootFile = new File(System.getProperty("user.dir"));
        File directory = new File(rootFile, FileSupporter.PLAYLIST_FOLDER);
        File[] arrFile = directory.listFiles();
        try {
            for (File file : arrFile) {
                System.out.println(file);
                String str = file.getName();
                StringTokenizer stringTokenizer = new StringTokenizer(str, ".txt");
                String name = stringTokenizer.nextToken().toString();

                Playlist playlist = new Playlist(name);
                mPlayists.add(playlist);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Song s = new Song(new File(line));
                    playlist.addSong(s);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean addSong(Song song) {
        if (!songIsAdded(song)) {
            mSongs.add(song);
            return true;
        }
        return false;
    }

    private static void addSongToAlbum(Album album, Song song) {
        if (!albumIsAdded(album)) {
            //if album not exists in mAlbums, add it, then add song
            mAlbums.add(album);
            album.addSong(song);
        } else {
            //if album already existed in mAlbums, find it and add song
            for (Album temp : mAlbums) {
                if (temp.equals(album)) temp.addSong(song);
            }
        }

    }

    private static void addSongToArtist(Artist artist, Song song) {
        if (!artistIsAdded(artist)) {
            //if artist not exists in mArtists, add it, then add song
            mArtists.add(artist);
            artist.addSong(song);
        } else {
            //if artist already existed in mArtists, find it and add song
            for (Artist temp : mArtists) {
                if (temp.equals(artist)) temp.addSong(song);
            }
        }
    }


    public static boolean songIsAdded(Song song) {
        if (mSongs.contains(song)) {
            System.out.println(5);
            return true;
        }
        return false;
    }

    public static boolean artistIsAdded(Artist artist) {
        if (mArtists.contains(artist)) {
            return true;
        }
        return false;
    }

    public static boolean albumIsAdded(Album album) {
        if (mAlbums.contains(album)) {
            return true;
        }
        return false;
    }

    public static Artist findArtistByName(String name) {
        Artist artist = null;
        for (Artist a : mArtists) {
            if (a.getmName().equalsIgnoreCase(name)) artist = a;
        }
        return artist;
    }

    public static Album findAlbumByName(String strItem) {
        Album album = null;
        for (Album a : mAlbums) {
            if (a.getmTitle().equalsIgnoreCase(strItem)) album = a;
        }
        return album;
    }

    public static ArrayList<Song> getmSongs() {
        return mSongs;
    }

    public static ArrayList<Artist> getmArtists() {
        return mArtists;
    }

    public static ArrayList<Playlist> getmPlayists() {
        return mPlayists;
    }

    public static ArrayList<Album> getmAlbums() {
        return mAlbums;
    }

    public static Playlist findPlaylistByName(String name) {
        Playlist playlist = null;
        for (Playlist p : mPlayists) {
            if (p.getmTitle().equalsIgnoreCase(name)) {
                playlist = p;
            }
        }
        return playlist;
    }
}
