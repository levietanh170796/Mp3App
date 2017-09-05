package sample;

import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.Controller.MainController;
import sample.Playlist.MySupportClass;
import sample.Playlist.Song;


import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Mp3Player extends Application {
    private static MainController mainController;
    private static Song nowPlaying;
    private static Song nextSong = null;
    private static Song previousSong = null;
    private static ArrayList<Song> previousSongList = new ArrayList<Song>();
    private static int nowSongindex;
    private static MediaPlayer mediaPlayer;
    private static Timer timer;
    private static int timerCounter;
    private static int secondsPlayed;
    private static boolean isLoopActive = false;
    private static boolean isAutoPlay = false;
    private static boolean isMuted = false;


    public static Song getNowPlaying() {
        return nowPlaying;
    }

    public static Song getNextSong() {
        return nextSong;
    }

    public static Song getPreviousSong() {
        return previousSong;
    }

    public static void setPreviousSong(Song previousSong) {
        Mp3Player.previousSong = previousSong;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Interface/main.fxml"));
        Region root = loader.load();
        mainController = loader.getController();
        final UndecoratorScene undecoratorScene = new UndecoratorScene(primaryStage, root);
        primaryStage.setScene(undecoratorScene);
        primaryStage.sizeToScene();
        primaryStage.toFront();
        Undecorator undecorator = undecoratorScene.getUndecorator();
        primaryStage.setMinWidth(undecorator.getMinWidth());
        primaryStage.setMinHeight(undecorator.getMinHeight());
        primaryStage.show();
        primaryStage.setResizable(false);

    }

    private void initMain() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String userdir = new File(System.getProperty("user.dir")).getAbsolutePath();
        System.out.println(userdir);
        launch(args);
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static void setSong(Song song) {
        nowPlaying = song;
        if(!previousSongList.isEmpty()) {
            if (!previousSongList.get(previousSongList.size() - 1).getmTitle().equals(nowPlaying.getmTitle()) ) {
                previousSongList.add(nowPlaying);
            }
        }
        else previousSongList.add(nowPlaying);
        MySupportClass.setFocusForListView(MySupportClass.getPositionByName(nowPlaying));
        setSource(nowPlaying.getmFile());
        if (MySupportClass.getmCurrentPosition() != -1) {
            MySupportClass.setmCurrentPosition(MySupportClass.getPositionByName(song));
            nowSongindex = MySupportClass.getmCurrentPosition();
            try {
                nextSong = MySupportClass.getSongAtPosition(nowSongindex + 1);
            } catch (Exception e) {
                nextSong = null;
            }
        } else nextSong = null;
        mainController.setInfo();

    }


    public static void setSource(File file) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timerCounter = 0;
        secondsPlayed = 0;
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.volumeProperty().bind(mainController.getVolumeSlider().valueProperty().divide(200));
        mediaPlayer.setMute(isMuted);
        mainController.initializeTimeSlider();
        mainController.initializeTimeLabels();
        checkLoop();


    }

    public static boolean isPlaying() {
        return mediaPlayer != null && MediaPlayer.Status.PLAYING.equals(mediaPlayer.getStatus());
    }

    public static void mute(boolean isMuted) {
        Mp3Player.isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setMute(!isMuted);
        }
    }

    public static boolean isLoopActive() {
        return isLoopActive;
    }

    public static void toogleLoop() {
        isLoopActive = !isLoopActive;
        checkLoop();

    }

    public static boolean isIsAutoPlay() {
        return isAutoPlay;
    }

    public static void checkLoop() {
        if (isLoopActive()) {
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    mainController.rotatePause();
                    setSong(nowPlaying);
                    if (MySupportClass.getmCurrentPosition() != -1)
                        MySupportClass.setmCurrentPosition(nowSongindex);
                    Mp3Player.play();
                }
            });
        } else {
            if (MySupportClass.getmCurrentPosition() != -1)
                nextSong = MySupportClass.getSongAtPosition(nowSongindex + 1);
            else nextSong = null;
            mediaPlayer.setOnEndOfMedia(new Runnable() {

                @Override
                public void run() {
                    mainController.rotatePause();
                    if (nextSong != null && isAutoPlay == true) {
                        previousSong = nowPlaying;
                        setSong(nextSong);
                        Mp3Player.play();
                    } else mediaPlayer.stop();

                }
            });


        }
    }

    public static void toggleAutoPlay() {
        isAutoPlay = !isAutoPlay;
        if(mediaPlayer!=null)
            checkLoop();
    }

    private static class TimeUpdater extends TimerTask {
        private int length = (int) getNowPlaying().getmDuration() * 4;

        @Override
        public void run() {
            Platform.runLater(() -> {
                if (timerCounter < length) {
                    if (++timerCounter % 4 == 0) {
                        mainController.updateTimeLabels();
                        secondsPlayed++;
                    }
                    if (!mainController.isTimeSliderPressed()) {
                        mainController.updateTimeSlider();
                    }
                }
            });
        }
    }

    public static String getLength() {
        int minutes = (int) (getNowPlaying().getmDuration() / 60);
        int seconds = (int) (getNowPlaying().getmDuration() % 60);
        return Integer.toString(minutes) + ":" + (seconds < 10 ? "0" + seconds : Integer.toString(seconds));
    }

    public static String getTimePassed() {
        int secondsPassed = timerCounter / 4;
        int minutes = secondsPassed / 60;
        int seconds = secondsPassed % 60;
        return Integer.toString(minutes) + ":" + (seconds < 10 ? "0" + seconds : Integer.toString(seconds));
    }

    public static void play() {
        if (mediaPlayer != null && !isPlaying()) {
            mediaPlayer.play();
            mainController.rotatePlay();
            timer.scheduleAtFixedRate(new TimeUpdater(), 0, 250);
            mainController.updatePlayPauseIcon();
            if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                System.out.println("stopped");
                setSong(nowPlaying);
                try {
                    play();
                } catch (Exception e) {
                }
            }


        }
    }

    public static void pause() {
        if (mediaPlayer != null && isPlaying()) {
            mediaPlayer.pause();
            mainController.rotatePause();
            timer.cancel();
            timer = new Timer();
            mainController.updatePlayPauseIcon();

        }
    }

    public static void seek(int seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(new Duration(seconds * 1000));
            timerCounter = seconds * 4;
            mainController.updateTimeLabels();
        }
    }

    public static void back(){
        if(!previousSongList.isEmpty()){
            try {
                nowPlaying = previousSongList.get(previousSongList.size() - 2);
                previousSongList.remove(previousSongList.get(previousSongList.size() - 1));
                setSong(nowPlaying);
                play();
            } catch (Exception e){}
        }
    }
}
