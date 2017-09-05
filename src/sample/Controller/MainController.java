package sample.Controller;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import sample.CustomSliderSkin;
import sample.PersistentPromptTextField;
import sample.Mp3Player;
import sample.Playlist.MySupportClass;
import sample.Playlist.Song;
import sample.ToggleSwitch;

import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML TextField searchTextField = new PersistentPromptTextField("", "Search");
    private String filePath;
    @FXML private TreeView<String> tvLibrary;
    @FXML private ListView<String> lvShowSongs;
    @FXML private Pane btnChooseDirectory;
    @FXML private Button playPauseButton;
    @FXML private Button replayButton;
    @FXML private Slider seekSlider;
    @FXML private Slider volumeSlider;
    @FXML private Label volumeLabel;
    @FXML private Region frontVolumeTrack;
    @FXML private Pane mutedButton;
    @FXML private Pane muteButton;
    @FXML private Region frontSliderTrack;
    @FXML private Label timePassed;
    @FXML private Label centerLabel;
    @FXML private Label lengthTime;
    @FXML private Circle imageView;
    @FXML private Label songNameLabel;
    @FXML private Label songArtistLabel;
    @FXML private Label songAlbumLabel;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private Pane autoPlay = new Pane();
    @FXML private Label autoPlayLabel;


    private ToggleSwitch toggle = new ToggleSwitch();
    private CustomSliderSkin sliderSkin;
    private RotateTransition rt;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autoPlay.setPrefSize(56, 25);
        autoPlay.getChildren().addAll(toggle);
        frontSliderTrack.prefWidthProperty().bind(seekSlider.widthProperty().multiply(seekSlider.valueProperty().divide(seekSlider.maxProperty())));

        sliderSkin = new CustomSliderSkin(seekSlider);
        seekSlider.setSkin(sliderSkin);
        seekSlider.setFocusTraversable(false);

        seekSlider.valueChangingProperty().addListener(
                (slider, wasChanging, isChanging) -> {

                    if (wasChanging) {

                        int seconds = (int) Math.round(seekSlider.getValue() / 4.0);
                        seekSlider.setValue(seconds * 4);
                        Mp3Player.seek(seconds);
                    }
                }
        );
        seekSlider.valueProperty().addListener(
                (slider, oldValue, newValue) -> {

                    double previous = oldValue.doubleValue();
                    double current = newValue.doubleValue();
                    if (!seekSlider.isValueChanging() && current != previous + 1 && !isTimeSliderPressed()) {

                        int seconds = (int) Math.round(current / 4.0);
                        seekSlider.setValue(seconds * 4);
                        Mp3Player.seek(seconds);
                    }
                }
        );
        try {

            CustomSliderSkin sliderSkin = new CustomSliderSkin(volumeSlider);
            volumeSlider.setSkin(sliderSkin);
            frontVolumeTrack.prefWidthProperty().bind(volumeSlider.widthProperty().subtract(30).multiply(volumeSlider.valueProperty().divide(volumeSlider.maxProperty())));
            volumeSlider.valueProperty().addListener((x, y, z) -> {
                volumeLabel.setText(Integer.toString(z.intValue()));
            });
            volumeSlider.setOnMousePressed(x -> {
                if (mutedButton.isVisible()) {
                    muteClick();
                }
            });

        } catch (Exception ex) {

            ex.printStackTrace();
        }
        playPauseButton.setOnMouseClicked(event -> {
            if(Mp3Player.isPlaying()) {
                Mp3Player.pause();
            }
            else Mp3Player.play();
        });

        playPauseButton.setOnMouseEntered(event -> {
            if (Mp3Player.isPlaying())
                playPauseButton.setStyle("-fx-background-image: url('./sample/Util/image/pauseLuotQua.png')");
            else
                playPauseButton.setStyle("-fx-background-image: url('./sample/Util/image/playLuotQua.png')");

        });
        playPauseButton.setOnMouseExited(event -> {
            if (Mp3Player.isPlaying())
                playPauseButton.setStyle("-fx-background-image: url('./sample/Util/image/pause.png')");
            else
                playPauseButton.setStyle("-fx-background-image: url('./sample/Util/image/play.png')");

        });
        replayButton.setOnMouseClicked(event ->{
            if(Mp3Player.getNowPlaying()!=null)
                Mp3Player.toogleLoop();
            if(Mp3Player.isLoopActive()){
                replayButton.setStyle("-fx-background-image: url('./sample/Util/image/replayMark.png')");

            }
            else replayButton.setStyle("-fx-background-image: url('./sample/Util/image/replaynotMark.png')");


        });
        nextButton.setOnMouseClicked(event -> {
            if(Mp3Player.getNextSong()!=null){
                Mp3Player.setPreviousSong(Mp3Player.getNowPlaying());
                Mp3Player.setSong(Mp3Player.getNextSong());
                Mp3Player.play();
            }
        });
        previousButton.setOnMouseClicked(event -> {
            Mp3Player.back();
        });
        MySupportClass.HandleBtnCreateDirectoryChooserAndImportSongToLibraryAndUpdateTreeView(btnChooseDirectory, tvLibrary, lvShowSongs, searchTextField);
        MySupportClass.createTreeView(tvLibrary, lvShowSongs, searchTextField);
    }
    public void handleButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File(*.mp4)", "*.mp3", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        File file = null;
        try {
            file = fileChooser.showOpenDialog(null);
        } catch (Exception e) {

        }

        if (file != null) {
            Mp3Player.setSong(new Song(file));
            MySupportClass.setmCurrentPosition(-1);
            Mp3Player.play();
            System.out.println((double) Mp3Player.getNowPlaying().getmDuration());
        }
    }

    public void updatePlayPauseIcon() {
        if(Mp3Player.isPlaying())
            playPauseButton.setStyle("-fx-background-image: url('./sample/Util/image/play.png')");
        else
            playPauseButton.setStyle("-fx-background-image: url('./sample/Util/image/pause.png')");
    }


    @FXML private void muteClick() {

        PseudoClass muted = PseudoClass.getPseudoClass("muted");
        boolean isMuted = mutedButton.isVisible();
        muteButton.setVisible(isMuted);
        mutedButton.setVisible(!isMuted);
        volumeSlider.pseudoClassStateChanged(muted, !isMuted);
        frontVolumeTrack.pseudoClassStateChanged(muted, !isMuted);
        volumeLabel.pseudoClassStateChanged(muted, !isMuted);
        Mp3Player.mute(isMuted);
    }
    public Label getAutoPlayLabel() {
        return autoPlayLabel;
    }

    public void initializeTimeSlider() {
        Song song = Mp3Player.getNowPlaying();
        if (song != null) {
           seekSlider.setMin(0);
            seekSlider.setMax(song.getmDuration() * 4);
            seekSlider.setValue(0);
            seekSlider.setBlockIncrement(1);
        } else {
            seekSlider.setMin(0);
            seekSlider.setMax(1);
            seekSlider.setValue(0);
            seekSlider.setBlockIncrement(1);
        }
    }
    public void updateTimeSlider() {

        seekSlider.increment();
    }
    public boolean isTimeSliderPressed() {
        return sliderSkin.getThumb().isPressed() || sliderSkin.getTrack().isPressed();
    }
    public void initializeTimeLabels() {

        Song song = Mp3Player.getNowPlaying();
        if (song != null) {
            timePassed.setText("0:00");
            centerLabel.setText("|");
            lengthTime.setText(Mp3Player.getLength());
        } else {
            timePassed.setText("");
            centerLabel.setText("");
            lengthTime.setText("");
        }
    }
    public void updateTimeLabels() {
        timePassed.setText(Mp3Player.getTimePassed());
        lengthTime.setText(Mp3Player.getLength());

    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public void setInfo(){
        Image image = Mp3Player.getNowPlaying().getmImage();
        if(image==null){
            Random random = new Random();
            int n = random.nextInt(3)+1;
            switch (n){
                case 1:image = new Image("sample/Util/image/random1.png"); break;
                case 2:image = new Image("sample/Util/image/random2.png"); break;
                case 3:image = new Image("sample/Util/image/random3.png"); break;
            }

        }
        ImagePattern pattern = new ImagePattern(image);
        imageView.setFill(pattern);
        if(rt!=null) rt.stop();
        rt = new RotateTransition(new Duration(9000),imageView);
        rt.setFromAngle(0);
        rt.setToAngle(360);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(Timeline.INDEFINITE);
        songNameLabel.setText(Mp3Player.getNowPlaying().getmTitle());
        songArtistLabel.setText(Mp3Player.getNowPlaying().getmArtis());
        songAlbumLabel.setText(Mp3Player.getNowPlaying().getmAlbum());
        if(Mp3Player.getNextSong()==null) nextButton.setText("Next: Empty ");
        else nextButton.setText("Next: " +Mp3Player.getNextSong().getmTitle());
    }
    public void rotatePlay(){
        rt.play();
    }
    public void rotatePause() {
        rt.pause();
    }
}

