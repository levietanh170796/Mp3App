package sample.Playlist;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * Created by NgThach96 on 5/15/2017.
 */
public class MusicView extends ListCell<String> {

    private HBox hbox = new HBox();
    private Label label = new Label("(empty)");
    private Pane pane = new Pane();
    private ImageView imgView = new ImageView(
            new Image(getClass().getResourceAsStream("/sample/Util/image/thach_plus.png"),
                    16, 16, false, false));
    //Button button = new Button("", imgView);
    private String lastItem;

    public MusicView() {
        super();
        hbox.getChildren().addAll(label, pane, imgView);
        HBox.setHgrow(pane, Priority.ALWAYS);
        imgView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(lastItem);
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
            lastItem = item;
            label.setText(item!=null ? item : "<null>");
            setGraphic(hbox);
        }
    }
}
