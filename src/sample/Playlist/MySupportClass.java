package sample.Playlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import sample.Mp3Player;

/**
 * Created by NgThach96 on 5/14/2017.
 */
public final class MySupportClass {

    //this will store song which show in listview
    private static ArrayList<Song> mSongsInSelectedTreeItem = null;

    private static ArrayList<Song> mNowPlayingSongs = null;

    private static ListView<String> mlv;
    // Now Playing song's position in listview
    //new
    private static int mCurrentPosition = -1;

    public static int getmCurrentPosition() {
        return mCurrentPosition;
    }

    public static void setmCurrentPosition(int mCurrentPosition) {
        MySupportClass.mCurrentPosition = mCurrentPosition;
    }

    public static int getPositionByName(Song song) {
        int position = -1;
        for (Song s : mNowPlayingSongs) {
            if (s.getmTitle().equalsIgnoreCase(song.getmTitle())) {
                position = mNowPlayingSongs.indexOf(s);
                break;
            }
        }
        return position;
    }

    public static int getMax() {
        if (mNowPlayingSongs == null) return 0;
        return mNowPlayingSongs.size();
    }

    public static Song getSongAtPosition(int position) {
        if (mCurrentPosition == -1) return null;
        if (position > getMax()) return null;
        try {
            return mNowPlayingSongs.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    //////////////////
    public static void setFocusForListView(int position) {
        try {
            mlv.getSelectionModel().select(position);
        } catch (NullPointerException e) {
            System.out.println("setFocusForLIstView get error");
        }
    }

    public static void HandleBtnCreateDirectoryChooserAndImportSongToLibraryAndUpdateTreeView(Pane btn,
                                                                                              TreeView<String> treeView,
                                                                                              ListView<String> showSong,
                                                                                              TextField searchTextField) {
        //new
        Library.importMP3FromSavedFile();
        Library.readPlaylistDirectory();
        createTreeView(treeView, showSong, searchTextField);
        ///////////////////

        btn.setOnMouseClicked(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            Library.importMP3FromFolder(directoryChooser.showDialog(btn.getScene().getWindow()));
            createTreeView(treeView, showSong, searchTextField);

        });
    }

    public static void createTreeView(TreeView<String> treeView, ListView<String> showSong, TextField searchTextField) {
        mlv = showSong;

        MyImage myImage = new MyImage();
        TreeItem<String> rootItem = new TreeItem<>("Library", new ImageView(myImage.root));
        TreeItem<String> allSong = new TreeItem<>("All Songs", new ImageView(myImage.musicNode));
        TreeItem<String> artists = new TreeItem<>("Artists", new ImageView(myImage.artistNode));
        TreeItem<String> albums = new TreeItem<>("Albums", new ImageView(myImage.albumNode));
        TreeItem<String> playlist = new TreeItem<>("Playlists", new ImageView(myImage.playList));

        for (Artist artist : Library.getmArtists()) {
            TreeItem<String> itemArtist = new TreeItem<>(artist.getmName(), new ImageView(myImage.man));
            artists.getChildren().add(itemArtist);
        }

        for (Album album : Library.getmAlbums()) {
            TreeItem<String> itemAlbum = new TreeItem<>(album.getmTitle(), new ImageView(myImage.cd));
            albums.getChildren().add(itemAlbum);
        }

        //setFocusForListView(5);
        rootItem.getChildren().add(allSong);
        rootItem.getChildren().add(playlist);
        rootItem.getChildren().add(artists);
        rootItem.getChildren().add(albums);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);

        //
        mNowPlayingSongs = new ArrayList<>();
        mSongsInSelectedTreeItem = new ArrayList<>();
        ObservableList<String> tempObservableList = FXCollections.observableArrayList();
        for (Song tempSong : Library.getmSongs()) {
            mNowPlayingSongs.add(tempSong);
            mSongsInSelectedTreeItem.add(tempSong);
            tempObservableList.add(tempSong.getmTitle());
        }
        showSong.setItems(tempObservableList);
        //

        //
        for (Playlist playlist1 : Library.getmPlayists()) {
            TreeItem<String> itemPlaylist = new TreeItem<>(playlist1.getmTitle(), new ImageView(myImage.playListNode));
            playlist.getChildren().add(itemPlaylist);
        }

        //

        //custom listview
        showSong.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new MusicView();
            }
        });

        searchTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    searchTextField.setText("");
                } else {
                    searchTextField.setPromptText("Search");
                }
            }
        });

        //add click listener for listview
        showSong.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MouseButton mouseButton = event.getButton();
                if (mouseButton == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    mNowPlayingSongs = mSongsInSelectedTreeItem;
                    System.out.println("u clicked primary btn");
                    //int position = showSong.getSelectionModel().getSelectedIndex();
                    int position = 0;
                    String selectedSong = showSong.getSelectionModel().getSelectedItem();
                    for (int i = 0; i < mNowPlayingSongs.size(); i++) {
                        Song song = mNowPlayingSongs.get(i);
                        if (song.getmTitle().equalsIgnoreCase(selectedSong)) {
                            position = i;
                            break;
                        }
                    }

                    Song song = mNowPlayingSongs.get(position);
                    mCurrentPosition = position;
                    Mp3Player.setSong(song);
                    Mp3Player.play();
                } else if (mouseButton == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    for (Playlist pl : Library.getmPlayists()) {
                        MenuItem menuItem = new MenuItem("Add to " + pl.getmTitle());
                        menuItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Add to " + pl.getmTitle());
                                int position = showSong.getSelectionModel().getSelectedIndex();
                                //pl.addSong(mSongsInSelectedTreeItem.get(position));
                                FileSupporter.saveSongToPlaylistFile(pl, mSongsInSelectedTreeItem.get(position));
                            }
                        });
                        contextMenu.getItems().add(menuItem);
                    }

                    MenuItem createNewPlaylist = new MenuItem("Create new Playlist");
                    contextMenu.getItems().add(createNewPlaylist);
                    createNewPlaylist.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            MyImage m = new MyImage();
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
                            String defaultName = simpleDateFormat.format(calendar.getTime());
                            String playlistName;

                            TextInputDialog dialog = new TextInputDialog(defaultName);
                            dialog.setHeaderText("Create Playlist");
                            dialog.setContentText("Playlist's name: ");
                            dialog.setGraphic(new ImageView(m.playListNode));
                            dialog.setTitle("");
                            dialog.setGraphic(new ImageView(m.dialogImage));
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                playlistName = result.get();
                                TreeItem<String> newTemp = new TreeItem<String>(playlistName, new ImageView(m.playListNode));
                                Playlist np = new Playlist(playlistName);
                                int position = showSong.getSelectionModel().getSelectedIndex();
                                //np.addSong(mSongsInSelectedTreeItem.get(position));
                                FileSupporter.saveSongToPlaylistFile(np, mSongsInSelectedTreeItem.get(position));
                                Library.getmPlayists().add(np);
                                playlist.getChildren().add(newTemp);
                            }
                        }
                    });
                    showSong.setContextMenu(contextMenu);
                }

            }
        });

        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String strItem;
                String strItemParent;
                Artist tempArtist;
                Album tempAlbum;
                ObservableList<String> names = FXCollections.observableArrayList();
                if (event.getClickCount() == 2) {
                    TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
                    TreeItem<String> itemParent = item.getParent();
                    if (item.isLeaf()) {
                        mSongsInSelectedTreeItem = new ArrayList<>();
                        strItemParent = itemParent.getValue();
                        strItem = item.getValue();
                        mCurrentPosition = 0;
                        switch (strItemParent) {
                            case "Artists":
                                tempArtist = Library.findArtistByName(strItem);
                                for (Song song : tempArtist.getmSongs()) {
                                    mSongsInSelectedTreeItem.add(song);
                                    names.add(song.getmTitle());
                                }
                                showSong.setItems(names);
                                break;
                            case "Albums":
                                tempAlbum = Library.findAlbumByName(strItem);
                                for (Song song : tempAlbum.getmSongs()) {
                                    mSongsInSelectedTreeItem.add(song);
                                    names.add(song.getmTitle());
                                }
                                showSong.setItems(names);
                                break;
                            case "Library":
                                if (!strItem.equalsIgnoreCase("Playlists")) {
                                    for (Song song : Library.getmSongs()) {
                                        mSongsInSelectedTreeItem.add(song);
                                        names.add(song.getmTitle());
                                    }
                                    showSong.setItems(names);
                                }
                                break;
                            case "Playlists":
                                Playlist tempPlaylist = Library.findPlaylistByName(strItem);
                                for (Song song : tempPlaylist.getmSongs()) {
                                    mSongsInSelectedTreeItem.add(song);
                                    names.add(song.getmTitle());
                                }
                                showSong.setItems(names);
                                break;
                        }
                    }
                    /////////////////////////////////////////////////////////////////////////////////new
                    searchTextField.textProperty().addListener(new InvalidationListener() {

                        @Override
                        public void invalidated(Observable observable) {
                            if (searchTextField.textProperty().get().isEmpty()) {
                                showSong.setItems(names);
                                return;
                            }
                            ObservableList<String> tableItem = FXCollections.observableArrayList();
                            for (int i = 0; i < names.size(); i++) {
                                String item = names.get(i);
                                item = item.toLowerCase();
                                if (item.contains(searchTextField.textProperty().get().toLowerCase())) {
                                    tableItem.add(names.get(i));
                                }
                            }
                            showSong.setItems(tableItem);
                        }
                    });
///////////////////////////////////////////////////////////////////////////////
                }
            }
        });
    }
}
