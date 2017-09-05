package sample.Playlist;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by NgThach96 on 5/22/2017.
 */
public final class FileSupporter {

    public static final String ALL_SONG = "AllSong.txt";
    public static final String PLAYLIST_FOLDER = "Playlist";

    public static void saveSongToAllSongFile(String filePath) {
        File rootFile = new File(System.getProperty("user.dir"));
        File allSongfile = new File(rootFile, ALL_SONG);
        ArrayList<String> arrayList = new ArrayList<>();
        if (allSongfile.exists()) {
            try {
                FileReader fileReader = new FileReader(allSongfile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    arrayList.add(str);
                }
                bufferedReader.close();
                fileReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            arrayList = null;
        }

        try {
            FileWriter outputStream = new FileWriter(allSongfile, true);
            BufferedWriter writer = new BufferedWriter(outputStream);
            if (arrayList != null && arrayList.contains(filePath)) return;
            writer.write(filePath);
            writer.newLine();
            writer.flush();
            writer.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File savePlaylistToFile(Playlist playlist) {
        File playlistFile = null;
        try {
            File rootFile = new File(System.getProperty("user.dir"));
            File playlistFolder = new File(rootFile, PLAYLIST_FOLDER);
            // Create folder if not exists
            if (!playlistFolder.exists()) {
                Files.createDirectory(Paths.get(playlistFolder.getAbsolutePath()));
            }
            playlistFile = new File(playlistFolder, playlist.getmTitle() + ".txt");
            Path path = Paths.get(playlistFile.getAbsolutePath());

            /// Create file if not exsits
            if (!playlistFile.exists()) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playlistFile;
    }

    public static void saveSongToPlaylistFile(Playlist playlist, Song song) {
        try {
            File file = savePlaylistToFile(playlist);
            if (playlist.addSong(song)) {
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(song.getmFile().getAbsolutePath());
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
